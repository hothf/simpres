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

    private val ideasBox = db.get().boxFor<IdeaItem>()
    private val subjectsBox = db.get().boxFor<SubjectItem>()

    private var lastRemovedIdea: IdeaItem? = null
    private var lastRemovedSubjectItem: SubjectItem? = null

    override val observableSubjects =
        PublishSubject.create<List<SubjectItem>>()

    override val observableIdeas =
        PublishSubject.create<List<IdeaItem>>()

    override fun getSubjects(wait: Boolean) {
        if (wait) {
            GlobalScope.launch {
                delay(250)
                getSubjectsInternally()
            }
        } else {
            getSubjectsInternally()
        }
    }

    private fun getSubjectsInternally() {
        val list = subjectsBox.all.sortedBy { it.position }
        observableSubjects.onNext(list)
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

        getSubjects()
    }

    override fun updateSubject(subject: SubjectItem) {
        subjectsBox.put(subject)

        getSubjects()
    }

    override fun removeSubject(subject: SubjectItem) {
        findSubjectById(subject.id)?.let {
            ideasBox.remove(ideasBox.query().equal(IdeaItem_.subjectId, subject.id).build().find())
            subjectsBox.remove(it)
            lastRemovedSubjectItem = it
            val list = subjectsBox.all.toMutableList()
            list.forEach { subject -> subject.position = subject.position - 1 }
            subjectsBox.put(list)

            getSubjects()
        }
    }

    override fun getIdeasOf(subjectId: Long) {
        val items = ideasBox.query().equal(IdeaItem_.subjectId, subjectId).build().find().sortedBy { it.done }
        observableIdeas.onNext(items)
    }

    override fun removeIdea(subjectId: Long, ideaItem: IdeaItem) {
        findSubjectById(subjectId)?.let { subject ->
            ideasBox.remove(ideaItem)
            lastRemovedIdea = ideaItem

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

    override fun undoDeleteSubject() {
        lastRemovedSubjectItem?.let {
            val list = subjectsBox.all.toMutableList()
            list.forEach { subject -> subject.position = subject.position + 1 }
            subjectsBox.put(list)
            subjectsBox.put(it)

            getSubjects()
        }
    }

    override fun undoDeleteIdea() {
        lastRemovedIdea?.let {
            ideasBox.put(it)

            getIdeasOf(it.subjectId)
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
        subject.ideasCount = ideas.size
        subject.ideasDoneCount = ideas.count { it.done }

        updateSubject(subject)
    }
}