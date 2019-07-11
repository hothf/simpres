package de.ka.simpres.ui

import android.view.View
import androidx.navigation.Navigation.findNavController
import de.ka.simpres.R
import de.ka.simpres.base.BaseActivity
import de.ka.simpres.databinding.ActivityMainBinding
import de.ka.simpres.base.events.ShowSnack


class MainActivity: BaseActivity<ActivityMainBinding, MainViewModel>(MainViewModel::class) {

    override var bindingLayoutId = R.layout.activity_main

    override fun onSupportNavigateUp() = findNavController(this, R.id.main_nav_host_fragment).navigateUp()

    override fun onShowSnack(view: View, showSnack: ShowSnack) {
        getBinding()?.mainSnacker?.reveal(showSnack)
    }
}
