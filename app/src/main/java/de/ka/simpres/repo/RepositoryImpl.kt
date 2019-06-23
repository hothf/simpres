package de.ka.simpres.repo

import de.ka.simpres.ui.home.HomeItem
import io.reactivex.subjects.PublishSubject
import kotlin.random.Random

class RepositoryImpl : Repository {

    override val observableHomeItems =
        PublishSubject.create<IndicatedList<HomeItem, List<HomeItem>>>()

    override fun getHomeItems() {

        val randomCount = Random.nextInt(20)

        val list =
        generateSequence(0, { it + 1 })
            .take(randomCount)
            .map { HomeItem(it) }
            .toList()

        observableHomeItems.onNext(IndicatedList(list))

    }

}