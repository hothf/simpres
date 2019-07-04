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
        volatileSubjects.addAll(list)

        observableSubjects.onNext(IndicatedList(list))

    }

    override fun getSubject(subjectId: String) {
        findSubjectById(subjectId)?.let {
            observableSubjects.onNext(IndicatedList(listOf(it), update = true))
        }
    }

    override fun saveOrUpdateSubject(subject: SubjectItem) {
        val index = volatileSubjects.indexOfFirst { subject.id == it.id }
        if (index >= 0) {
            volatileSubjects[index] = subject
            observableSubjects.onNext(IndicatedList(listOf(subject), update = true))
        } else {
            volatileSubjects.add(subject)
            observableSubjects.onNext(IndicatedList(listOf(subject), addToTop = true))
        }
    }

    override fun removeSubject(subject: SubjectItem) {
        findSubjectById(subject.id)?.let {
            volatileSubjects.remove(it)
            observableSubjects.onNext(IndicatedList(listOf(it), remove = true))
        }
    }

    override fun getIdeasOf(subjectId: String) {
        findSubjectById(subjectId)?.let {
            observableIdeas.onNext(IndicatedList(it.ideas))
        }
    }

    override fun removeIdea(subjectId: String, ideaItem: IdeaItem) {
        findSubjectById(subjectId)?.let { subject ->
            subject.ideas.remove(ideaItem)
            observableIdeas.onNext(IndicatedList(subject.ideas))

            recalculateSum(subject)
        }
    }

    override fun saveOrUpdateIdea(subjectId: String, idea: IdeaItem) {
        findSubjectById(subjectId)?.let { subject ->
            val index = subject.ideas.indexOfFirst { idea.id == it.id }
            if (index >= 0) {
                subject.ideas[index] = idea
            } else {
                subject.ideas.add(idea)
            }
            observableIdeas.onNext(IndicatedList(subject.ideas))

            recalculateSum(subject)
        }
    }

    override fun findSubjectById(subjectId: String): SubjectItem? = volatileSubjects.find { subjectId == it.id }

    private fun recalculateSum(subject: SubjectItem) {
        subject.sum = subject.ideas
            .filter { !it.done }
            .map {
                if (it.sum.isBlank()) {
                    0
                } else {
                    it.sum.toInt()
                }
            }
            .fold(0) { sum, item -> sum + item }
            .toString()

        saveOrUpdateSubject(subject)
    }
}