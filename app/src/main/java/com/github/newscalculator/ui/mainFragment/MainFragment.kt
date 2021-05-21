package com.github.newscalculator.ui.mainFragment

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.newscalculator.R
import com.github.newscalculator.databinding.FragmentMainBinding
import com.github.newscalculator.diseaseparameterstypes.AbstractDiseaseType
import com.github.newscalculator.ui.mainFragment.recyclerView.Decoration
import com.github.newscalculator.ui.mainFragment.recyclerView.DiseaseAdapter
import com.github.newscalculator.util.AutoClearedValue
import com.github.newscalculator.util.FragmentViewBinding
import com.github.newscalculator.util.loggingDebug

class MainFragment :
    FragmentViewBinding<FragmentMainBinding>(FragmentMainBinding::inflate),
    ConnectionToDialog {
    private var diseaseAdapter by AutoClearedValue<DiseaseAdapter>()
    private val evalViewModel: MainViewModel by viewModels()

    override var allowToCallDialog = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        binder.bottomAppBar.apply {
            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.id_delete -> {
                        loggingDebug("deleting")
                        binder.motionLayout.apply {
                            setTransition(R.id.transitionScale)
                            transitionToEnd()
                        }
                        loadData()
                        resetUI()
                        true
                    }
                    R.id.id_help -> {
                        loggingDebug("helping")
                        binder.motionLayout.apply {
                            setTransition(R.id.transitionScale)
                            transitionToEnd()
                        }
                        true
                    }
                    else -> {
                        loggingDebug("else")
                        true
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindViewModel()
        loadData()
    }

    private fun initUI() {
        diseaseAdapter = DiseaseAdapter { position ->
            translateItemIdIntoDialog(position)
        }
        binder.recView.apply {
            adapter = diseaseAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            if (itemDecorationCount == 0)
                addItemDecoration(Decoration(requireContext()))
        }
    }

    private fun resetUI() {
        binder.totalScore.customBinder.textTotalValue.setText(R.string.defaultTotalValue)
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

            getChangedParameter.observe(viewLifecycleOwner) { changedParameter ->
                val position = changedParameter.id.toInt()
                diseaseAdapter.diseaseList[position] = changedParameter
                diseaseAdapter.notifyItemChanged(position)
                evalViewModel.checkEverythingIsEntered()
            }

            getEverythingIsEntered.observe(viewLifecycleOwner) { newSum ->
                binder.totalScore.customBinder.textTotalValue.text = "$newSum/19"
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
        binder.totalScore.customBinder.textTotalValue.setTextColor(
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