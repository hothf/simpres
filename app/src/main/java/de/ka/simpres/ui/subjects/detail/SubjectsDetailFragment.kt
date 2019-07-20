package de.ka.simpres.ui.subjects.detail

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.transition.TransitionInflater
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentSubjectsdetailBinding

class SubjectsDetailFragment :
    BaseFragment<FragmentSubjectsdetailBinding, SubjectsDetailViewModel>(SubjectsDetailViewModel::class) {

    override var bindingLayoutId = R.layout.fragment_subjectsdetail

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val subjectId = arguments?.getLong(SUBJECT_ID_KEY)
        val subjectIsNew = arguments?.getBoolean(SUBJECT_IS_NEW, false) ?: false

        if (subjectId != null) {
            viewModel.setupAdapterAndLoad(viewLifecycleOwner, subjectId)

            if (!subjectIsNew) {
                getBinding()?.detailsCard?.let { ViewCompat.setTransitionName(it, subjectId.toString()) }
                sharedElementEnterTransition =
                    TransitionInflater.from(requireContext())
                        .inflateTransition(R.transition.shared_element_home_transition)
                postponeEnterTransition()
                Handler().postDelayed(
                    { startPostponedEnterTransition() },
                    75
                ) // simply wait for laying out the recycler for ui de-stuttering
            }
        }
        return view
    }

    companion object {
        const val SUBJECT_ID_KEY = "sub_id_key"
        const val SUBJECT_IS_NEW = "sub_new_key"
    }
}