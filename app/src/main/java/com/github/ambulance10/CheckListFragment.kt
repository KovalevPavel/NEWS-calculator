package com.github.ambulance10

import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ambulance10.adapters.EvalAdapter
import kotlinx.android.synthetic.main.fragment_checklist.*

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
            val tempList = evalAdapter.items.toMutableList()

            tempList.forEach {
                it.diseasePoints = newDiseasePointsList[0][tempList.indexOf(it)]
                it.diseaseBooleanPoints = newDiseasePointsList[1][tempList.indexOf(it)]
            }

            evalAdapter.items = tempList
            evalAdapter.notifyDataSetChanged()
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
        val action = CheckListFragmentDirections.actionCheckListFragmentToEditValueDialog(evalAdapter.items[id])
        findNavController().navigate(action)
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
        val tempEvalParameter = evalAdapter.items[incomingItemPosition]
        tempEvalParameter.evalValue = evalDiseasePoints
        val tempList = evalAdapter.items.toMutableList()
        tempList[incomingItemPosition] = tempEvalParameter
        evalAdapter.items = tempList
        evalAdapter.notifyItemChanged(incomingItemPosition)
//        Log.d("ZAWARUDO", "__ $incomingItemPosition: $evalDiseasePoints")
//        Log.d("ZAWARUDO", "___ $incomingItemPosition: ${tempEvalParameter.diseasePoints}")
        Log.d("ZAWARUDO", tempList[incomingItemPosition].toString())

    }
}