package de.ka.simpres.ui.subjects.subjectlist.newedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentSubjectneweditBinding
import de.ka.simpres.utils.DatePickeable
import de.ka.simpres.utils.DatePicker

class NewEditSubjectFragment : BaseFragment<FragmentSubjectneweditBinding, NewEditSubjectViewModel>
    (NewEditSubjectViewModel::class), DatePickeable {

    override var bindingLayoutId = R.layout.fragment_subjectnewedit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val subjectId = arguments?.getLong(SUBJECT_ID_KEY)
        if (subjectId != null && subjectId > 0) {
            viewModel.setupEdit(subjectId)
        } else {
            val new = arguments?.getBoolean(NEW_KEY, false) ?: false
            if (new) {
                viewModel.setupNew()
            }
        }
        arguments?.clear()

        return view
    }

    override fun onDateSet(year: Int, month: Int, day: Int, callerId: Int) {
        if (callerId == 1) {
            viewModel.updateDate(year, month, day)
        }
    }

    override fun handle(element: Any?) {
        if (element is NewEditSubjectViewModel.OpenDatePickerEvent) {
            DatePicker
                .newInstance(element.date, this@NewEditSubjectFragment, 1)
                .show(fragmentManager, "ddtlg")
        }
    }

    companion object {
        const val SUBJECT_ID_KEY = "sub_key"
        const val NEW_KEY = "new_key"
    }

}