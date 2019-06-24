package de.ka.simpres.repo

import de.ka.simpres.ui.home.HomeItem
import de.ka.simpres.ui.home.detail.HomeDetailItem
import io.reactivex.subjects.PublishSubject
import kotlin.random.Random

class RepositoryImpl : Repository {

    override val observableHomeItems =
        PublishSubject.create<IndicatedList<HomeItem, List<HomeItem>>>()

    override val observableHomeDetailItems =
        PublishSubject.create<IndicatedList<HomeDetailItem, List<HomeDetailItem>>>()

    private val volatileHomeItems = mutableListOf<HomeItem>()

    override fun getHomeItems() {
        val randomCount = Random.nextInt(20)

        val list =
        generateSequence(0, { it + 1 })
            .take(randomCount)
            .map { HomeItem(it) }
            .toMutableList()

        list.addAll(volatileHomeItems)

        observableHomeItems.onNext(IndicatedList(list))

    }

    override fun getHomeDetailItems() {

        val randomCount = Random.nextInt(20)

        val list =
            generateSequence(0, { it + 1 })
                .take(randomCount)
                .map { HomeDetailItem(it) }
                .toList()

        observableHomeDetailItems.onNext(IndicatedList(list))

    }

    override fun saveHomeItem(homeItem: HomeItem) {
        volatileHomeItems.add(homeItem)

        observableHomeItems.onNext(IndicatedList(listOf(homeItem), addToTop = true))
    }
}