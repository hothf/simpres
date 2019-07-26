package de.ka.simpres.base

import android.Manifest
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.net.Uri

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import de.ka.simpres.BR
import de.ka.simpres.base.events.*
import de.ka.simpres.utils.NavigationUtils
import org.koin.androidx.viewmodel.ext.android.getViewModelByClass
import timber.log.Timber
import kotlin.reflect.KClass
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


/**
 * Represents a base fragment. Extending fragments should always be combined with a viewModel,
 * that's why offering the fragment layout resource id and viewModel is mandatory.
 * The viewModel updates the ui with Databinding.
 * To use the binding of the ui after inflating, use [getBinding].
 *
 * Created by Thomas Hofmann
 */
abstract class BaseFragment<out T : ViewDataBinding, E : BaseViewModel>(clazz: KClass<E>) : Fragment() {

    abstract var bindingLayoutId: Int

    private lateinit var binding: ViewDataBinding

    val viewModel: E by lazy {
        getViewModelByClass(
            clazz,
            from = { activity })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(layoutInflater, bindingLayoutId, null, true)

        binding.apply {
            setVariable(BR.viewModel, viewModel)
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        return binding.root
    }

    /**
     * Retrieves the view binding of the fragment. May only be useful after [onCreateView].
     */
    @Suppress("UNCHECKED_CAST")
    fun getBinding() = binding as? T

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.events.observe(
            viewLifecycleOwner,
            Observer {
                Timber.i("Event observed: $it")

                when (it) {
                    is NavigateTo -> navigateTo(it)
                    is ShowSnack -> (requireActivity() as? BaseActivity<*, *>)
                        ?.onShowSnack(view?.findFocus() ?: binding.root, it)
                    is Open -> open(it)
                    is Handle<*> -> handle(it.element)
                }
            }
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissions.forEachIndexed { index, _ ->
            val grantResult = grantResults[index]

            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(requestCode)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Requests permissions like fragments normally do, but can immediately trigger [onPermissionGranted] for the given
     * request code. This makes it convenient to start actions from [onPermissionGranted].
     */
    fun requestPermission(requestCode: Int, permissions: Array<out String>) {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.READ_CONTACTS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(requestCode)
        } else {
            requestPermissions(permissions, requestCode)
        }
    }

    /**
     * Called on a granted permission request.
     *
     * @param request the request code of the permission
     */
    open fun onPermissionGranted(request: Int) {
        // implemented by subclasses
    }

    /**
     * Called when a generic element should be handled.
     */
    open fun handle(element: Any?) {
        // implemented by children
    }

    private fun open(openEvent: Open) {
        if (openEvent.clazz != null) {

            val intent = Intent(activity, openEvent.clazz.java)

            openEvent.args?.let {
                intent.putExtras(it)
            }

            startActivity(intent)
        } else {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(openEvent.url)))
            } catch (e: Exception) {
                Timber.e(e, "Could not open $openEvent.url")
            }
        }
    }

    private fun navigateTo(navigateToEvent: NavigateTo) {

        val navController = view?.findNavController()

        if (navController == null) {
            Timber.e("Could not find nav controller!")
            return
        }

        NavigationUtils.navigateTo(navController, navigateToEvent)
    }
}

