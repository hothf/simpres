package de.ka.simpres.ui.subjects.subjectlist.newedit

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflecopter.multicontactpicker.MultiContactPicker
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentSubjectneweditBinding
import de.ka.simpres.utils.DatePickeable
import de.ka.simpres.utils.DatePicker
import android.content.Intent
import android.app.Activity.RESULT_OK


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

        viewModel.setupAdapterAndLoad(viewLifecycleOwner)

        return view
    }

    override fun onDateSet(year: Int, month: Int, day: Int, callerId: Int) {
        if (callerId == 1) {
            viewModel.updateDate(year, month, day)
        }
    }

    override fun handle(element: Any?) {
        if (element is NewEditSubjectViewModel.OpenDatePickerEvent) {
            DatePicker.newInstance(element.date, this@NewEditSubjectFragment, 1).show(fragmentManager, "ddtlg")
        } else if (element is NewEditSubjectViewModel.OpenContactsPicker) {
            requestPermission(CONTACT_PICKER_REQUEST, arrayOf(Manifest.permission.READ_CONTACTS))
        }
    }

    override fun onPermissionGranted(request: Int) {
        super.onPermissionGranted(request)
        if (request == CONTACT_PICKER_REQUEST) {
            MultiContactPicker.Builder(this)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE)
                .setTitleText(getString(R.string.subject_newedit_contact_choose))
                .showPickerForResult(CONTACT_PICKER_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                val results = MultiContactPicker.obtainResult(data)
                if (results.isNotEmpty()) {
                    viewModel.updateContact(results[0])
                }
            }
        }
    }

    companion object {
        const val SUBJECT_ID_KEY = "sub_key"
        const val NEW_KEY = "new_key"
        const val CONTACT_PICKER_REQUEST = 1335
    }

}