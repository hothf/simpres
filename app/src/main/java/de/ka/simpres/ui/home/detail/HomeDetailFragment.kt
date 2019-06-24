package de.ka.simpres.ui.home.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.transition.TransitionInflater
import de.ka.simpres.R
import de.ka.simpres.base.BaseFragment
import de.ka.simpres.databinding.FragmentHomedetailBinding

class HomeDetailFragment: BaseFragment<FragmentHomedetailBinding, HomeDetailViewModel>(HomeDetailViewModel::class) {

    override var bindingLayoutId = R.layout.fragment_homedetail

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val homeId = arguments?.getString(HOME_ID_KEY)
        if (homeId != null) {
           viewModel.setupAdapterAndLoad(viewLifecycleOwner, homeId.toInt())
        }

        getBinding()?.transitionText?.let { ViewCompat.setTransitionName(it, homeId) }
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_element_home_transition)

        return view
    }

    companion object {
        const val HOME_ID_KEY = "home_id_key"
    }
}