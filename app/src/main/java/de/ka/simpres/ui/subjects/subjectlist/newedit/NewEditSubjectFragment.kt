package de.ka.simpres.ui.subjects.subjectlist.newedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentSubjectneweditBinding
import de.ka.simpres.repo.model.SubjectItem

class NewEditSubjectFragment: BaseFragment<FragmentSubjectneweditBinding, NewEditSubjectViewModel>
    (NewEditSubjectViewModel::class){

    override var bindingLayoutId = R.layout.fragment_subjectnewedit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val subject = arguments?.getSerializable(SUBJECT_KEY) as? SubjectItem
        if (subject != null) {
            viewModel.setupEdit(subject)
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
        const val SUBJECT_KEY = "sub_key"
        const val NEW_KEY = "new_key"
    }

}