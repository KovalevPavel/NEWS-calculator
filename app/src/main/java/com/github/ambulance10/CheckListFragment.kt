package com.github.ambulance10

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ambulance10.adapters.EvalAdapter
import kotlinx.android.synthetic.main.fragment_checklist.*
import kotlinx.android.synthetic.main.item_evaluation_parameter.view.*

class CheckListFragment : Fragment(R.layout.fragment_checklist), ConnectionToDialog {
    private lateinit var evalAdapter: EvalAdapter
    private lateinit var evalViewModel: EvalViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        evalViewModel = EvalViewModel(requireActivity().application)
        var everythingIsEntered = false

        evalAdapter = EvalAdapter { position -> translateItemIdIntoDialog(position) }

        evalViewModel.getDiseaseBooleanList.observe(viewLifecycleOwner) { newBooleanArray ->
            val tempList = newBooleanArray.filterIndexed { index, _ -> index != 5 }
            everythingIsEntered =
                !tempList.contains(false)
        }
        evalViewModel.getCommonPoints.observe(viewLifecycleOwner) { newCommonPoints ->
            textViewResult.text = if (everythingIsEntered) "$newCommonPoints/19" else "__ /19"
            makeEvalColor(newCommonPoints, everythingIsEntered)
        }

        evalViewModel.getDiseasePointsList.observe(viewLifecycleOwner) { newDiseasePointsList ->
            var additionalValue = ""
            for (index in 0 until 6) {
                if (newDiseasePointsList[1][index] != 0) {
                    additionalValue = "\n${newDiseasePointsList[1][index]}"
                }
                recView.getChildAt(index)?.let {
                    recView[index].textViewDiseasePoints.text =
                        when (newDiseasePointsList[1][index]) {
                            0 -> "${newDiseasePointsList[0][index]}"
                            else -> if (newDiseasePointsList[0][index] == 0) "${newDiseasePointsList[1][index]}" else "${newDiseasePointsList[0][index]}$additionalValue"
                        }
                }
            }
            evalViewModel.changeSum(newDiseasePointsList)
        }

        evalAdapter.items = evalViewModel.getEvalParametersList

        recView.apply {
            adapter = evalAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun translateItemIdIntoDialog(id: Int) {
//        CheckListFragmentDirections.actionCheckListFragmentToEditValueDialog(id)
//        findNavController().navigate(R.id.action_checkListFragment_to_editValueDialog)
        EditValueDialog.newInstance(
            evalAdapter.items[id]
        ).show(childFragmentManager, null)
    }

    private fun makeEvalColor(inputParam: Int, everythingIsEntered: Boolean = false) {
        var color = when (inputParam) {
            in (0 until 2) -> R.color.green
            in (3 until 5) -> R.color.yellow
            else -> R.color.red
        }
        if (!everythingIsEntered) color = R.color.red
        textViewResult.setTextColor(
            ResourcesCompat.getColor(resources, color, null)
        )
    }

    override fun onDialogClicked(
        evalParameter: EvalParameter,
        evalDiseasePoints: Double,
        evalCheck: Boolean
    ) {
        val incomingItemPosition = evalViewModel.getEvalParametersList.indexOfFirst {
            evalParameter == it
        }
        evalViewModel.changeInputValue(incomingItemPosition, evalDiseasePoints, evalCheck)
        val incomingEvalValue =
            if (evalParameter.normalValue == 36.6) evalDiseasePoints else evalDiseasePoints.toInt()
        recView[incomingItemPosition].textViewEvalPoints.text = when (incomingEvalValue) {
            -1 -> if (evalCheck) evalParameter.specialMark?.substring(0 until 5) ?: "ошибка" else ""
            else -> when (evalParameter.specialMark) {
                null -> incomingEvalValue.toString()
                else -> if (evalCheck)
                    "${incomingEvalValue}\n${evalParameter.specialMark.substring(0 until 5)}"
                else incomingEvalValue.toString()
            }
        }
    }
}