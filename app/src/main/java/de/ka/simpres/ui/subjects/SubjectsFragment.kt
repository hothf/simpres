package de.ka.simpres.ui.subjects

import android.os.Bundle
import android.view.View
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentSubjectsBinding


/**
 * The home fragment displays a list to discover all consensuses this app has to offer.
 */
class SubjectsFragment : BaseFragment<FragmentSubjectsBinding, SubjectsViewModel>(SubjectsViewModel::class) {

    override var bindingLayoutId = R.layout.fragment_subjects

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setupAdapterAndLoad(viewLifecycleOwner, getBinding()?.recyclerSubjects!!)
        super.onViewCreated(view, savedInstanceState)
    }
}
