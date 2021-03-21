package com.github.newscalculator.screens.mainfragment

import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.newscalculator.AutoClearedValue
import com.github.newscalculator.FragmentViewBinding
import com.github.newscalculator.R
import com.github.newscalculator.adapters.Decoration
import com.github.newscalculator.adapters.DiseaseAdapter
import com.github.newscalculator.databinding.FragmentMainBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType

class MainFragment :
    FragmentViewBinding<FragmentMainBinding>(FragmentMainBinding::inflate),
    ConnectionToDialog {
    private var diseaseAdapter by AutoClearedValue<DiseaseAdapter>()
    private val evalViewModel: MainViewModel by viewModels()

    override var allowToCallDialog = true

    override fun onStart() {
        super.onStart()
        initUI()
        bindViewModel()
        loadData()
    }

    private fun initUI() {
        diseaseAdapter = DiseaseAdapter { position -> translateItemIdIntoDialog(position) }

        binder.recView.apply {
            adapter = diseaseAdapter
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
                diseaseAdapter.diseaseList = newList
                diseaseAdapter.notifyDataSetChanged()
            }

//            getChangedParameter.observe(viewLifecycleOwner) { changedParameter ->
//                if (!binder.clearFAB.isVisible) binder.clearFAB.isVisible = true
//                val position = changedParameter.id
//                diseaseAdapter.diseaseList[position] = changedParameter
//                diseaseAdapter.notifyItemChanged(position)
//                evalViewModel.checkEverythingIsChanged()
//                evalViewModel.countSum()
//            }

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
                MainFragmentDirections.actionCheckListFragmentToEditValueDialog(diseaseAdapter.diseaseList[id])
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
        diseaseParameter: AbstractDiseaseType,
        measuredValue: Double,
        measuredIsChecked: Boolean
    ) {
        evalViewModel.changeInputValue(diseaseParameter, measuredValue, measuredIsChecked)
    }
}