package com.github.newscalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.newscalculator.adapters.EvalAdapter
import com.github.newscalculator.databinding.FragmentChecklistBinding

class CheckListFragment : Fragment(), ConnectionToDialog {
    lateinit var binder: FragmentChecklistBinding
    private var evalAdapter by AutoClearedValue<EvalAdapter>()
    private val evalViewModel: EvalViewModel by viewModels()

    override var allowToCallDialog = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = FragmentChecklistBinding.inflate(inflater)
        return binder.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
        bindViewModel()
        loadData()
    }

    private fun initUI() {
        evalAdapter = EvalAdapter { position -> translateItemIdIntoDialog(position) }

        binder.recView.apply {
            adapter = evalAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(Decoration(requireContext()))
        }

        binder.clearFAB.setOnClickListener {
            loadData()
        }
    }

    private fun resetUI() {
        binder.clearFAB.isVisible = false
        binder.textViewResult.text = "__/19"
        makeEvalColor(20)
    }

    private fun loadData() {
        evalViewModel.getEvalList(this.resources)
    }

    private fun bindViewModel() {
        evalViewModel.apply {
            getEvalParametersList.observe(viewLifecycleOwner) { newList ->
                resetUI()
                evalAdapter.diseaseList = newList
                evalAdapter.notifyDataSetChanged()
            }

            getChangedParameter.observe(viewLifecycleOwner) { changedParameter ->
                if (!binder.clearFAB.isVisible) binder.clearFAB.isVisible = true
                val position = changedParameter.id
                evalAdapter.diseaseList[position] = changedParameter
                evalAdapter.notifyItemChanged(position)
                evalViewModel.checkEverythingIsChanged()
                evalViewModel.countSum()
            }

            getEverythingIsEntered.observe(viewLifecycleOwner) { newSum ->
                binder.textViewResult.text = "$newSum/19"
                makeEvalColor(newSum)
            }
        }
    }

    private fun translateItemIdIntoDialog(id: Int) {
        if (allowToCallDialog) {
            allowToCallDialog = false
            val action =
                CheckListFragmentDirections.actionCheckListFragmentToEditValueDialog(evalAdapter.diseaseList[id])
            findNavController().navigate(action)
        }
    }

    private fun makeEvalColor(inputParam: Int) {
        val color = when (inputParam) {
            in (0 until 3) -> R.color.green
            in (3 until 6) -> R.color.yellow
            else -> R.color.red
        }
        binder.textViewResult.setTextColor(
            ResourcesCompat.getColor(resources, color, null)
        )
    }

    override fun onDialogClicked(
        evalParameter: EvalParameter,
        measuredValue: Double?,
        measuredIsChecked: Boolean
    ) {
        evalViewModel.changeInputValue(evalParameter, measuredValue, measuredIsChecked)
    }
}