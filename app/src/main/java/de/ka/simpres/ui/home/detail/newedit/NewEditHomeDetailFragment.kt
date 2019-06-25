package de.ka.simpres.ui.home.detail.newedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentNewedithomeBinding
import de.ka.simpres.databinding.FragmentNewedithomedetailBinding
import de.ka.simpres.ui.home.HomeItem
import de.ka.simpres.ui.home.detail.HomeDetailItem
import de.ka.simpres.ui.home.newedit.NewEditHomeViewModel

class NewEditHomeDetailFragment: BaseFragment<FragmentNewedithomedetailBinding, NewEditHomeDetailViewModel>
    (NewEditHomeDetailViewModel::class){

    override var bindingLayoutId = R.layout.fragment_newedithomedetail

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val home = arguments?.getSerializable(HOME_KEY) as? HomeDetailItem
        if (home != null) {
            viewModel.setupEdit(home)
        } else {
            val new = arguments?.getBoolean(NEW_KEY, false) ?: false
            if (new) {
                viewModel.setupNew()
            }
        }
        arguments?.clear()

        return view
    }

    companion object {
        const val HOME_KEY = "home_detail_key"
        const val NEW_KEY = "new_detail_key"
    }

}