package com.github.newscalculator.ui.mainFragment

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.newscalculator.R
import com.github.newscalculator.databinding.FragmentMainBinding
import com.github.newscalculator.domain.entities.AbstractDiseaseType
import com.github.newscalculator.domain.usecases.MainViewModel
import com.github.newscalculator.domain.usecases.MyViewModelFactory
import com.github.newscalculator.ui.mainFragment.recyclerView.Decoration
import com.github.newscalculator.ui.mainFragment.recyclerView.DiseaseAdapter
import com.github.newscalculator.ui.retryDialog.DialogRetry
import com.github.newscalculator.util.AutoClearedValue
import com.github.newscalculator.util.FragmentViewBinding
import com.github.newscalculator.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment :
    FragmentViewBinding<FragmentMainBinding>(FragmentMainBinding::inflate),
    ConnectionToDialog, ConnectionToRetryDialog {
    private var everythingIsEntered = false
    private var diseaseAdapter by AutoClearedValue<DiseaseAdapter>()

    private val retryDialog = DialogRetry()

    private val mainViewModel: MainViewModel by viewModels {
        MyViewModelFactory()
    }

    override var allowToCallDialog = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initToolbar()
    }

    private fun initToolbar() {
        binder.bottomToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
//                кнопка "удалить"
                R.id.id_delete -> {
                    reloadList()
                    binder.motionLayout.apply {
                        setTransition(R.id.transitionScale)
                        transitionToEnd()
                    }
                    resetUI()
                    true
                }
//                кнопка "помощь"
                R.id.id_help -> {
                    binder.motionLayout.apply {
                        setTransition(R.id.transitionScale)
                        transitionToEnd()
                    }
                    true
                }
                else -> error("Unknown menu id")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindViewModel()
        loadData()
    }

    private fun initUI() {
        if (retryDialog.isAdded) retryDialog.dismiss()
        diseaseAdapter = DiseaseAdapter(onItemClick = { position ->
            translateItemIdIntoDialog(position)
        }, onItemLongClick = { position ->
            val everythingWasEntered = everythingIsEntered
            reloadItem(position)
            if (diseaseAdapter.diseaseList[position].required) {
                resetUI()
                if (everythingWasEntered) {
                    binder.motionLayout.apply {
                        setTransition(R.id.transitionJumpStart)
                        transitionToEnd()
                    }
                    everythingIsEntered = false
                }
            }
        })
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
        mainViewModel.getItemsList()
    }

    private fun reloadList() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.IO) {
            mainViewModel.refreshList(diseaseAdapter.diseaseList)
        }
    }

    private fun reloadItem(position: Int) {
        mainViewModel.resetItem(diseaseAdapter.diseaseList[position])
    }

    private fun bindViewModel() {
        mainViewModel.apply {
            getItemsList.observe(viewLifecycleOwner) { newList ->
                resetUI()
                diseaseAdapter.diseaseList = newList
                diseaseAdapter.notifyDataSetChanged()
            }

            getChangedItem.observe(viewLifecycleOwner) { changedParameter ->
                val position = changedParameter.id.toInt()
                diseaseAdapter.diseaseList[position] = changedParameter
                diseaseAdapter.notifyItemChanged(position)
                mainViewModel.checkEverythingIsEntered()
            }

            getEverythingIsEntered.observe(viewLifecycleOwner) { newSum ->
                binder.totalScore.customBinder.textTotalValue.text = "$newSum/19"
                makeEvalColor(newSum)
                everythingIsEntered = true
            }

            getLoadErrorEvent.observe(viewLifecycleOwner) {
                retryDialog.show(childFragmentManager, null)
            }

            getToastEvent.observe(viewLifecycleOwner) {toastString ->
                showToast(requireContext(), toastString)
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
        mainViewModel.changeInputValue(diseaseParameter, measuredValue, measuredIsChecked)
    }

    override fun doOnRetry() {
        loadData()
    }

    override fun onStop() {
//        убиваем диалог, если пользователь так и не загрузил данные
        if (retryDialog.isAdded) retryDialog.dismissAllowingStateLoss()
        super.onStop()
    }
}