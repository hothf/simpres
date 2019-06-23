package de.ka.simpres.ui.home.newedit

import android.app.Application
import android.os.Bundle
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.ui.home.detail.HomeDetailFragment

class NewEditHomeViewModel(app: Application) : BaseViewModel(app) {

    fun submit() {

        val id = 485859
        navigateTo(
            navigationTargetId = R.id.action_homeNewEditFragment_to_homeDetailFragment,
            args = Bundle().apply { putString(HomeDetailFragment.HOME_ID_KEY, id.toString()) },
            popupToId = R.id.homeNewEditFragment
        )
    }
}