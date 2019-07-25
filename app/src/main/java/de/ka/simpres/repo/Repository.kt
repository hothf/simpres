package de.ka.simpres.repo

import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.repo.model.IdeaItem
import io.reactivex.Observable


/**
 * The interface for the abstraction of the data sources of the app.
 */
interface Repository {

    /**
     * A list which can be subscribed to which contains subject data.
     */
    val observableSubjects: Observable<IndicatedList<SubjectItem>>

    /**
     * A list which can be subscribed to which contains
     */
    val observableIdeas: Observable<IndicatedList<IdeaItem>>

    /**
     * Retrieves all subjects.
     *
     * @param wait if set to true, will try to at least wait a few milliseconds before emitting data. This is useful for
     * de-stuttering uis
     */
    fun getSubjects(wait: Boolean = false)

    /**
     * Use to save a position change of two subjects. Usually has only internal use for updating the database. Most
     * likely will not emit data changes.
     *
     * @param subject1 the first subject which has moved
     * @param subject2 the second subject which has now the position of the first object moved
     * @param newPosition the new position of subject1 and the old position of subject2
     * @param oldPosition the old position of subject1 abd the new position of subject2
     */
    fun moveSubject(
        subject1: SubjectItem,
        subject2: SubjectItem,
        oldPosition: Int,
        newPosition: Int
    )

    /**
     * Removes the given subject.
     *
     * @param subject the subject to remove
     */
    fun removeSubject(subject: SubjectItem)

    /**
     * Saves a subject.
     *
     * @param subject the subject to save
     */
    fun saveSubject(subject: SubjectItem)

    /**
     * Updates a subject.
     *
     * @param subject the subject to update
     */
    fun updateSubject(subject: SubjectItem)

    /**
     * Retrieves the ideas of a subject.
     *
     * @param subjectId the id of the subject to retrieve the ideas of
     */
    fun getIdeasOf(subjectId: Long)

    /**
     * Removes the given idea.
     *
     * @param ideaItem the idea to remove
     */
    fun removeIdea(ideaItem: IdeaItem)

    /**
     * Saves or updates the given idea.
     *
     * @param idea the idea to save
     */
    fun saveOrUpdateIdea(idea: IdeaItem)

    /**
     * Finds a subject by its id. Immediately returns the item, if available.
     *
     * @param subjectId the id of the subject to find
     * @return the item or null if it could not be found
     */
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
 * A optional [update] flag can be used to indicate, that the list is only a update and does not contain deleted or
 * added data.
 *
 * All flags default to **false** for a simple list indication, that could contain removed, updated and new data.
 */
data class IndicatedList<T : Any>(val list: List<T>, var update: Boolean = false) : ArrayList<T>(list)