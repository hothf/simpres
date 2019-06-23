package de.ka.simpres.ui

import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import de.ka.simpres.R
import de.ka.simpres.base.BaseActivity
import de.ka.simpres.databinding.ActivityMainBinding

class MainActivity: BaseActivity<ActivityMainBinding, MainViewModel>(MainViewModel::class) {

    override var bindingLayoutId = R.layout.activity_main

    override fun onSupportNavigateUp() = findNavController(this, R.id.main_nav_host_fragment).navigateUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}
