package de.ka.simpres.repo

import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.repo.model.IdeaItem
import io.reactivex.Observable


/**
 * The interface for the abstraction of the data sources of the app.
 */
interface Repository {

    val observableSubjects: Observable<IndicatedList<SubjectItem>>
    val observableIdeas: Observable<IndicatedList<IdeaItem>>

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

    fun removeIdea(ideaItem: IdeaItem)

    fun saveOrUpdateIdea(idea: IdeaItem)

    fun findSubjectById(subjectId: Long): SubjectItem?

    /**
     * Will try to undo the last removed subject action.
     */
    fun undoDeleteSubject()

    /**
     * Will try to undo the last removed idea action.
     */
    fun undoDeleteIdea()
}

/**
 * An array [List] container.
 *
 * A optional [update] flag can be used to indicate, that the list is only a update.
 *
 * All flags default to **false** for a simple list indication, that could contain removed, updated and new data.
 */
data class IndicatedList<T : Any>(val list: List<T>, var update: Boolean = false) : ArrayList<T>(list)