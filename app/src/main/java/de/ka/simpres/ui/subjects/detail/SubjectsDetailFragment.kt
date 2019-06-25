package de.ka.simpres.ui.subjects.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.transition.TransitionInflater
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentSubjectsdetailBinding

class SubjectsDetailFragment: BaseFragment<FragmentSubjectsdetailBinding, SubjectsDetailViewModel>(SubjectsDetailViewModel::class) {

    override var bindingLayoutId = R.layout.fragment_subjectsdetail

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val subjectId = arguments?.getString(SUBJECT_ID_KEY)
        if (subjectId != null) {
           viewModel.setupAdapterAndLoad(viewLifecycleOwner, subjectId)
        }

        getBinding()?.transitionText?.let { ViewCompat.setTransitionName(it, subjectId) }
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_element_home_transition)

        return view
    }

    companion object {
        const val SUBJECT_ID_KEY = "sub_id_key"
    }
}