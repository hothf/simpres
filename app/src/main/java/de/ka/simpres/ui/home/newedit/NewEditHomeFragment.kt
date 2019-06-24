package de.ka.simpres.ui.home.newedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentNewedithomeBinding
import de.ka.simpres.ui.home.HomeItem

class NewEditHomeFragment: BaseFragment<FragmentNewedithomeBinding, NewEditHomeViewModel>
    (NewEditHomeViewModel::class){

    override var bindingLayoutId = R.layout.fragment_newedithome

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val home = arguments?.getSerializable(HOME_KEY) as? HomeItem
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
        const val HOME_KEY = "home_key"
        const val NEW_KEY = "new_key"
    }

}