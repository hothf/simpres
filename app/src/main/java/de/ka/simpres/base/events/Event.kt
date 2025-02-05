package de.ka.simpres.base.events

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import de.ka.simpres.utils.Snacker
import kotlin.reflect.KClass

/**
 * A collection of commonly used events.
 */
sealed class Event

data class ShowSnack(
    val message: String,
    val type: Snacker.SnackType,
    val action: (() -> Unit)? = null,
    val actionText: String? = null
) : Event()

enum class AnimType {
    DEFAULT, MODAL, NONE
}

/**
 * Handles navigation to fragments.
 *
 * @param navigationTargetId the target to navigate to. May reference a destination or action
 * @param clearBackStack clears the backstack. Note that you can not use [navigationPopupToId] when this is set to true
 * @param args optional arguments to pass for the target fragment
 * @param navOptions optional navigator options for setting the default animations and behaviour
 * @param extras options for the transaction, like shared views fro transitions
 * @param animType a type of animation, defaults to system animations
 * @param navigationPopupToId id for target to popup to. This will only work, if [clearBackStack] is set to false
 */
data class NavigateTo(
    @IdRes val navigationTargetId: Int,
    val clearBackStack: Boolean = false,
    val args: Bundle? = null,
    val navOptions: NavOptions? = null,
    val extras: Navigator.Extras? = null,
    val animType: AnimType = AnimType.DEFAULT,
    @IdRes val navigationPopupToId: Int? = null
) : Event()

data class RequestPermission(val request: Int, val permissions: Array<out String>): Event() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestPermission

        if (request != other.request) return false
        if (!permissions.contentEquals(other.permissions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = request
        result = 31 * result + permissions.contentHashCode()
        return result
    }
}

data class Open(
    val url: String? = null,
    val clazz: KClass<*>? = null,
    val args: Bundle? = null
) : Event()

data class Handle<T>(
    val element: T
) : Event()

