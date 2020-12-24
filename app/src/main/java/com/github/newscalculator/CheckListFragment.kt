package com.github.newscalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.newscalculator.adapters.EvalAdapter
import com.github.newscalculator.databinding.FragmentChecklistBinding
import kotlinx.android.synthetic.main.fragment_checklist.*
import kotlinx.android.synthetic.main.fragment_checklist.view.*

class CheckListFragment : androidx.fragment.app.Fragment(R.layout.fragment_checklist),
    ConnectionToDialog {
    private var evalAdapter by AutoClearedValue<EvalAdapter>()
    private lateinit var evalViewModel: EvalViewModel
    override var allowToCallDialog = true

    lateinit var binder: FragmentChecklistBinding

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

        evalViewModel = EvalViewModel(requireActivity().application)

        binder.root.clearFAB.isVisible = false

        evalAdapter = EvalAdapter { position -> translateItemIdIntoDialog(position) }


        evalViewModel.getCommonPoints.observe(viewLifecycleOwner) { newCommonPoints ->
            binder.root.textViewResult.text =
                if (evalViewModel.isEverythingIsEntered) "$newCommonPoints/19" else "__ /19"
            makeEvalColor(newCommonPoints, evalViewModel.isEverythingIsEntered)
        }

        evalViewModel.getEditingParameter.observe(viewLifecycleOwner) { newItem ->
            val tempList = evalAdapter.items.toMutableList()
            tempList[newItem.id] = newItem
            evalAdapter.items = tempList
            binder.root.clearFAB.isVisible =
                evalViewModel.getListOfCurrentParameters.find { it != null } != null
            evalAdapter.notifyItemChanged(newItem.id)
            evalViewModel.changeSum()
        }


        evalAdapter.items = evalViewModel.getEvalParametersList

        recView.apply {
            adapter = evalAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(Decoration(requireContext()))
        }

        binder.root.clearFAB.setOnClickListener {
            evalAdapter.items.forEach {
                evalViewModel.changeInputValue(it, null, false)
//                Log.d("ZAWARUDO", "${it.id} is OK")
                evalAdapter.notifyItemChanged(evalAdapter.items.indexOf(it))
            }
//            Log.d("ZAWARUDO", "${ evalViewModel.getListOfCurrentParameters }")
        }
    }

    private fun translateItemIdIntoDialog(id: Int) {
        val action =
            CheckListFragmentDirections.actionCheckListFragmentToEditValueDialog(evalAdapter.items[id])
        if (allowToCallDialog) {
            allowToCallDialog = false
            findNavController().navigate(action)
        }
    }

    private fun makeEvalColor(inputParam: Int, everythingIsEntered: Boolean = false) {
        var color = when (inputParam) {
            in (0 until 3) -> R.color.green
            in (3 until 6) -> R.color.yellow
            else -> R.color.red
        }
        if (!everythingIsEntered) color = R.color.red
        textViewResult.setTextColor(
            ResourcesCompat.getColor(resources, color, null)
        )
    }

    override fun onDialogClicked(
        evalParameter: EvalParameter,
        measuredValue: Double?,
        measuredIsChecked: Boolean
    ) {
        evalViewModel.changeInputValue(evalParameter, measuredValue, measuredIsChecked)
//        Log.d("ZAWARUDO", "measuredValue = $measuredValue, measuredChecked = $measuredIsChecked")
    }
}