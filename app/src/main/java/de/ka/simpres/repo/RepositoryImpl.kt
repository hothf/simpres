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

    private var lastRemovedIdea: IdeaItem? = null
    private var lastRemovedSubjectItem: SubjectItem? = null
    private var lastRemovedIdeas: List<IdeaItem> = listOf()

    override val observableSubjects =
        PublishSubject.create<IndicatedList<SubjectItem>>()

    override val observableIdeas =
        PublishSubject.create<IndicatedList<IdeaItem>>()

    override fun getSubjects(wait: Boolean) {
        if (wait) {
            GlobalScope.launch {
                delay(250)
                getSubjectsInternally(false)
            }
        } else {
            getSubjectsInternally(false)
        }
    }

    private fun getSubjectsInternally(isUpdate: Boolean) {
        val list = subjectsBox.all.sortedBy { it.position }
        observableSubjects.onNext(IndicatedList(list, isUpdate))
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

        getSubjectsInternally(false)
    }

    override fun updateSubject(subject: SubjectItem) {
        subjectsBox.put(subject)

        getSubjectsInternally(true)
    }

    override fun removeSubject(subject: SubjectItem) {
        findSubjectById(subject.id)?.let {
            lastRemovedIdeas = ideasBox.query().equal(IdeaItem_.subjectId, subject.id).build().find()
            ideasBox.remove(lastRemovedIdeas)
            subjectsBox.remove(it)
            lastRemovedSubjectItem = it
            val list = subjectsBox.all.toMutableList()
            list.forEach { subject -> subject.position = subject.position - 1 }
            subjectsBox.put(list)

            getSubjectsInternally(false)
        }
    }

    override fun getIdeasOf(subjectId: Long) {
        getIdeasOfInternally(subjectId, false)
    }

    private fun getIdeasOfInternally(subjectId: Long, isUpdate: Boolean) {
        val items = ideasBox.query().equal(IdeaItem_.subjectId, subjectId).build().find().sortedBy { it.done }
        observableIdeas.onNext(IndicatedList(items, isUpdate))
    }

    override fun removeIdea(ideaItem: IdeaItem) {
        findSubjectById(ideaItem.subjectId)?.let { subject ->
            ideasBox.remove(ideaItem)
            lastRemovedIdea = ideaItem

            getIdeasOfInternally(subject.id, false)

            recalculateSum(subject)
        }
    }

    override fun saveOrUpdateIdea(idea: IdeaItem) {
        findSubjectById(idea.subjectId)?.let { subject ->
            val update = idea.id != 0L

            ideasBox.put(idea)

            getIdeasOfInternally(subject.id, update)

            recalculateSum(subject)
        }
    }

    override fun undoDeleteSubject() {
        lastRemovedSubjectItem?.let {
            saveSubject(it)
            ideasBox.put(lastRemovedIdeas)
        }
    }

    override fun undoDeleteIdea() {
        lastRemovedIdea?.let {
            saveOrUpdateIdea(it)
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

        subjectsBox.put(subject)
        getSubjectsInternally(false)
    }
}