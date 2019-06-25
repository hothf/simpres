package de.ka.simpres.ui.subjects.detail.idealist.newedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentIdeaneweditBinding
import de.ka.simpres.repo.model.IdeaItem

class NewEditIdeaFragment : BaseFragment<FragmentIdeaneweditBinding, NewEditIdeaViewModel>
    (NewEditIdeaViewModel::class) {

    override var bindingLayoutId = R.layout.fragment_ideanewedit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        arguments?.getString(SUBJECT_ID_KEY)?.let {
            val idea = arguments?.getSerializable(IDEA_KEY) as? IdeaItem
            if (idea != null) {
                viewModel.setupEdit(it, idea)
            } else {
                val new = arguments?.getBoolean(NEW_KEY, false) ?: false
                if (new) {
                    viewModel.setupNew(it)
                }
            }
        }
        arguments?.clear()

        return view
    }

    companion object {
        const val SUBJECT_ID_KEY = "sub_id_key"
        const val IDEA_KEY = "idea_key"
        const val NEW_KEY = "new_idea_key"
    }

}