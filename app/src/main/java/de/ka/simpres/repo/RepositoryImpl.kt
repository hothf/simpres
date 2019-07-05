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
        //TODO evaluate: is objectbox nice for persisting the position? We will need this!
        GlobalScope.launch {
            delay(250)
            observableSubjects.onNext(IndicatedList(subjectsBox.all.reversed()))
        }
    }

    override fun getSubject(subjectId: Long) {
        findSubjectById(subjectId)?.let {
            observableSubjects.onNext(IndicatedList(listOf(it), update = true))
        }
    }

    override fun findSubjectById(subjectId: Long): SubjectItem? {
        return subjectsBox.get(subjectId)
    }

    override fun saveOrUpdateSubject(subject: SubjectItem) {
        val was = subject.id
        val id = subjectsBox.put(subject)
        if (id == was) {
            observableSubjects.onNext(IndicatedList(listOf(subject), update = true))
        } else {
            observableSubjects.onNext(IndicatedList(listOf(subject), addToTop = true))
        }
    }

    override fun removeSubject(subject: SubjectItem) {
        findSubjectById(subject.id)?.let {
            ideasBox.remove(ideasBox.query().equal(IdeaItem_.subjectId, subject.id).build().find())
            subjectsBox.remove(it)
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

        saveOrUpdateSubject(subject)
    }
}