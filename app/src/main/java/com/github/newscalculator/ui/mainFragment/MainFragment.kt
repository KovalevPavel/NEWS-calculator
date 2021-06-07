package com.github.newscalculator.ui.mainFragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.github.newscalculator.util.loggingDebug
import com.github.newscalculator.util.showToast
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment :
    FragmentViewBinding<FragmentMainBinding>(FragmentMainBinding::inflate),
    ConnectionToDialog, ConnectionToRetryDialog, NavigationView.OnNavigationItemSelectedListener {
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
        initNavigationView()
        loadData()
    }

    private fun initNavigationView() {
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            binder.layoutDrawer,
            binder.bottomToolbar,
            R.string.drawerOpen,
            R.string.drawerClose
        )
        binder.layoutDrawer.addDrawerListener(toggle)
        toggle.syncState()
        binder.viewNavigation.setNavigationItemSelectedListener(this)
    }

    private fun initToolbar() {
        binder.bottomToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.id_delete -> {
                    reloadList()
                    binder.motionLayout.apply {
                        setTransition(R.id.transitionScale)
                        transitionToEnd()
                    }
                    resetUI()
                    true
                }
                else -> error("Unknown menu id")
            }
        }
        binder.bottomToolbar.setNavigationOnClickListener {

        }
    }

    override fun onStart() {
        super.onStart()
        bindViewModel()
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

    private fun enableUI(enable: Boolean) {
        binder.motionLayout.visibility = if (enable) View.VISIBLE else View.GONE
        binder.synchronizingView.visibility = if (enable) View.GONE else View.VISIBLE
    }

    private fun resetUI() = binder.totalScore.setTotalScore(-1)

    private fun loadData() {
        enableUI(false)
        mainViewModel.getItemsList()
    }

    private fun reloadList() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.IO) {
            mainViewModel.refreshList(diseaseAdapter.diseaseList)
        }
    }

    private fun reloadItem(position: Int) =
        mainViewModel.resetItem(diseaseAdapter.diseaseList[position])

    private fun bindViewModel() {
        mainViewModel.apply {
            getItemsList.observe(viewLifecycleOwner) { newList ->
                resetUI()
                enableUI(true)
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
                binder.totalScore.setTotalScore(newSum)
                everythingIsEntered = true
            }

            getLoadErrorEvent.observe(viewLifecycleOwner) {
                enableUI(true)
                retryDialog.show(childFragmentManager, null)
            }

            getToastEvent.observe(viewLifecycleOwner) { toastString ->
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

    override fun onDialogClicked(
        diseaseParameter: AbstractDiseaseType,
        measuredValue: Double,
        measuredIsChecked: Boolean
    ) {
        mainViewModel.changeInputValue(diseaseParameter, measuredValue, measuredIsChecked)
    }

    override fun doOnRetry() = loadData()

    override fun onStop() {
//        убиваем диалог, если пользователь так и не загрузил данные
        if (retryDialog.isAdded) retryDialog.dismissAllowingStateLoss()
        super.onStop()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val string = when (item.itemId) {
            R.id.support_Feedback -> "feedback"
            R.id.support_Rate -> "rate"
            R.id.support_Help -> "help"
            R.id.support_Share -> "share"
            else -> "wrong"
        }
        loggingDebug("support menu item: $string")
        return true
    }
}