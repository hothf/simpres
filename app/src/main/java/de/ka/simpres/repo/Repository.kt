package de.ka.simpres.repo

import de.ka.simpres.ui.home.HomeItem
import de.ka.simpres.ui.home.detail.HomeDetailItem
import io.reactivex.Observable


/**
 * The interface for the abstraction of the data sources of the app.
 */
interface Repository {

    val observableHomeItems: Observable<IndicatedList<HomeItem, List<HomeItem>>>

    val observableHomeDetailItems: Observable<IndicatedList<HomeDetailItem, List<HomeDetailItem>>>

    fun getHomeItems()

    fun getHomeDetailItemsOf(id: String)

    fun saveHomeItem(homeItem: HomeItem)

    fun saveHomeDetailItem(id: String, homeDetailItem: HomeDetailItem)
}

/**
 * A [List] container.
 *
 *
 * With an optional [invalidate] flag for giving the hint, that the list has invalidated data and
 * should be re-fetched.
 * A optional [remove] flag can be used to indicate, that the list contains items to be removed.
 * A optional [addToTop] flag can be used to indicate, that new items should be added to the top (instead of default
 * behaviour, which may be bottom).
 * A optional [update] flag can be used to indicate, that the list should only be updated and not extended or
 * manipulated somehow differently.
 * A optional [isFiltered] flag to indicate that the results are filtered.
 *
 * All flags default to **false** for a simple list indication, that could contain updated and new data.
 */
data class IndicatedList<E : Any, T : List<E>>(
    val list: T,
    var invalidate: Boolean = false,
    var remove: Boolean = false,
    var addToTop: Boolean = false,
    var update: Boolean = false,
    var isFiltered: Boolean = false
)