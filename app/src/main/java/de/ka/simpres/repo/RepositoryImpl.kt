package de.ka.simpres.repo

import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.repo.model.IdeaItem
import io.reactivex.subjects.PublishSubject
import kotlin.random.Random

class RepositoryImpl : Repository {

    override val observableSubjects =
        PublishSubject.create<IndicatedList<SubjectItem, List<SubjectItem>>>()

    override val observableIdeas =
        PublishSubject.create<IndicatedList<IdeaItem, List<IdeaItem>>>()

    private val volatileSubjects = mutableListOf<SubjectItem>()

    override fun getSubjects() {
        val randomCount = Random.nextInt(20)

        val list =
            generateSequence(0, { it + 1 })
                .take(randomCount)
                .map { SubjectItem(it.toString()) }
                .toMutableList()

        list.addAll(volatileSubjects)

        observableSubjects.onNext(IndicatedList(list))

    }

    override fun saveSubject(subject: SubjectItem) {
        volatileSubjects.add(subject)

        observableSubjects.onNext(IndicatedList(listOf(subject), addToTop = true))
    }

    override fun getIdeasOf(subjectId: String) {
        volatileSubjects.find { subjectId == it.id }?.let {
            observableIdeas.onNext(IndicatedList(it.ideas))
        }
    }

    override fun saveIdea(subjectId: String, idea: IdeaItem) {
        volatileSubjects.find { subjectId == it.id }?.let {
            it.ideas.add(idea)
            observableIdeas.onNext(IndicatedList(it.ideas))
        }


    }
}