package de.ka.simpres.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import de.ka.simpres.base.events.*
import de.ka.simpres.repo.Repository
import de.ka.simpres.utils.Snacker
import io.reactivex.disposables.CompositeDisposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * The base view model.
 */
abstract class BaseViewModel : ViewModel(), KoinComponent {

    val events = QueueLiveEvent<Event>()

    val repository: Repository by inject()

    val compositeDisposable = CompositeDisposable()

    private fun queueEvent(event: Event) = events.queueValue(event)

    /**
     * Navigates to the given resource id. Pass -1 as id to simply pop the back stack.
     */
    fun navigateTo(
        @IdRes navigationTargetId: Int,
        clearBackStack: Boolean = false,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        extras: Navigator.Extras? = null,
        animType: AnimType = AnimType.DEFAULT,
        @IdRes popupToId: Int? = null
    ) {
        queueEvent(
            NavigateTo(
                navigationTargetId = navigationTargetId,
                clearBackStack = clearBackStack,
                args = args,
                navOptions = navOptions,
                extras = extras,
                animType = animType,
                navigationPopupToId = popupToId
            )
        )
    }

    fun showSnack(
        message: String,
        snackType: Snacker.SnackType = Snacker.SnackType.DEFAULT,
        action: (() -> Unit)? = null,
        actionText: String? = null
    ) {
        queueEvent(ShowSnack(message, snackType, action, actionText))
    }

    fun handleGeneralError(throwable: Throwable) {
        Timber.e("General error in viewModel: $throwable")
    }

    fun open(url: String? = null, clazz: KClass<*>? = null, args: Bundle? = null) = queueEvent(
        Open(url = url, clazz = clazz, args = args)
    )

    fun <T> handle(element: T) = queueEvent(Handle(element = element))

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }
}