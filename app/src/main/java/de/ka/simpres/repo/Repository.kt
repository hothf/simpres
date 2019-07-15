package de.ka.simpres.repo

import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.repo.model.IdeaItem
import io.reactivex.Observable


/**
 * The interface for the abstraction of the data sources of the app.
 */
interface Repository {

    val observableIdeas: Observable<List<IdeaItem>>
    val observableSubjects: Observable<List<SubjectItem>>

    fun getSubjects(wait: Boolean = false)

    fun moveSubject(
        subject1: SubjectItem,
        subject2: SubjectItem,
        oldPosition: Int,
        newPosition: Int
    )

    fun removeSubject(subject: SubjectItem)

    fun saveSubject(subject: SubjectItem)

    fun updateSubject(subject: SubjectItem)

    fun getIdeasOf(subjectId: Long)

    fun removeIdea(subjectId: Long, ideaItem: IdeaItem)

    fun saveOrUpdateIdea(subjectId: Long, idea: IdeaItem)

    fun findSubjectById(subjectId: Long): SubjectItem?

    /**
     * Will try to undo the last removed subject action.
     */
    fun undoDeleteSubject()

    /**
     * Will try to undo the last removed idea action.
     */
    fun undoDeleteIdea(subjectId: Long)
}