package de.ka.simpres.repo

import de.ka.simpres.repo.db.AppDatabase
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.repo.model.IdeaItem_
import io.objectbox.kotlin.boxFor
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RepositoryImpl(db: AppDatabase) : Repository {

    private val subjectsBox = db.get().boxFor<SubjectItem>()
    private val ideasBox = db.get().boxFor<IdeaItem>()

    override val observableSubjects =
        PublishSubject.create<IndicatedList<SubjectItem, List<SubjectItem>>>()

    override val observableIdeas =
        PublishSubject.create<IndicatedList<IdeaItem, List<IdeaItem>>>()

    override fun getSubjects() {
        GlobalScope.launch {
            delay(250)
            val list = subjectsBox.all.sortedBy { it.position }
            observableSubjects.onNext(IndicatedList(list))
        }
    }

    override fun getSubject(subjectId: Long) {
        findSubjectById(subjectId)?.let {
            observableSubjects.onNext(IndicatedList(listOf(it), update = true))
        }
    }

    override fun moveSubject(subject1: SubjectItem, subject2: SubjectItem, oldPosition: Int, newPosition: Int) {
        subject1.position = newPosition
        subject2.position = oldPosition
        subjectsBox.put(subject1)
        subjectsBox.put(subject2)
    }

    override fun findSubjectById(subjectId: Long): SubjectItem? {
        return subjectsBox.get(subjectId)
    }

    override fun saveSubject(subject: SubjectItem) {
        val list = subjectsBox.all.toMutableList()
        list.forEach { it.position = it.position + 1 }
        subjectsBox.put(list)
        subjectsBox.put(subject)
        observableSubjects.onNext(IndicatedList(listOf(subject), addToTop = true))
    }

    override fun updateSubject(subject: SubjectItem) {
        subjectsBox.put(subject)
        observableSubjects.onNext(IndicatedList(listOf(subject), update = true))
    }

    override fun removeSubject(subject: SubjectItem) {
        findSubjectById(subject.id)?.let {
            ideasBox.remove(ideasBox.query().equal(IdeaItem_.subjectId, subject.id).build().find())
            subjectsBox.remove(it)
            val list = subjectsBox.all.toMutableList()
            list.forEach { subject -> subject.position = subject.position - 1 }
            subjectsBox.put(list)
            observableSubjects.onNext(IndicatedList(listOf(it), remove = true))
        }
    }

    override fun getIdeasOf(subjectId: Long) {
        val items = ideasBox.query().equal(IdeaItem_.subjectId, subjectId).build().find()
        observableIdeas.onNext(IndicatedList(items))
    }

    override fun removeIdea(subjectId: Long, ideaItem: IdeaItem) {
        findSubjectById(subjectId)?.let { subject ->
            ideasBox.remove(ideaItem)

            getIdeasOf(subjectId)

            recalculateSum(subject)
        }
    }

    override fun saveOrUpdateIdea(subjectId: Long, idea: IdeaItem) {
        findSubjectById(subjectId)?.let { subject ->
            ideasBox.put(idea)

            getIdeasOf(subjectId)

            recalculateSum(subject)
        }
    }

    private fun recalculateSum(subject: SubjectItem) {
        val ideas = ideasBox.query().equal(IdeaItem_.subjectId, subject.id).build().find()
        subject.sum = ideas
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

        updateSubject(subject)
    }
}