package de.ka.simpres.ui.subjects.detail.idealist.newedit

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.repo.model.Comment
import de.ka.simpres.repo.model.Comments
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.ui.subjects.detail.idealist.newedit.comments.CommentsAdapter
import de.ka.simpres.utils.*
import de.ka.simpres.utils.resources.ResourcesProvider
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import org.koin.standalone.inject
import java.net.URLEncoder


class NewEditIdeaViewModel : BaseViewModel() {

    val getTitleTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue(null)
        titleSelection.value = it.length

        currentIdea?.title = it
    }
    val getSumTextChangedListener = ViewUtils.TextChangeListener {
        sum.value = it
        sumError.postValue(null)
        sumSelection.value = it.length

        currentIdea?.sum = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { }
    val sum = MutableLiveData<String>().apply { value = "" }
    val sumError = MutableLiveData<String>().apply { value = "" }
    val sumSelection = MutableLiveData<Int>().apply { value = null }
    val navTitle = MutableLiveData<String>().apply { value = "" }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String?>().apply { value = null }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }
    val commentsAdapter = MutableLiveData<CommentsAdapter>().apply { value = null }

    private val resourcesProvider: ResourcesProvider by inject()
    private val inputValidator: InputValidator by inject()

    private val titleValidator = inputValidator.Validator(
        InputValidator.ValidatorConfig(
            titleError,
            listOf(ValidationRules.NOT_EMPTY)
        )
    )

    private var currentIdea: IdeaItem? = null
    private var currentSubjectId: Long = -1
    private var isUpdating = false

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun itemAnimator() = SlideInRightAnimator()

    fun onBack(v: View) {
        v.closeAttachedKeyboard()
        navigateTo(NavigationUtils.BACK)
    }

    fun submit(view: View? = null) {
        if (!titleValidator.isValid(title.value)) return

        view?.closeAttachedKeyboard()

        commentsAdapter.value?.getComments(shouldSave = true)?.let {
            currentIdea?.comments = Comments(it)
        }

        currentIdea?.let { idea ->
            repository.saveOrUpdateIdea(idea)
            navigateTo(NavigationUtils.BACK)
        }
    }

    private val openComment: (Comment) -> Unit = {
        open(url = "http://www.google.com/#q=${URLEncoder.encode(it.text, "UTF-8")}")
    }

    /**
     * Sets up a new empty idea.
     */
    fun setupNew(subjectId: Long, owner: LifecycleOwner) {
        currentIdea = IdeaItem(0, subjectId)
        currentSubjectId = subjectId

        isUpdating = false

        updateTextViews()

        commentsAdapter.postValue(
            CommentsAdapter(
                owner = owner,
                open = openComment,
                sourceItems = currentIdea?.comments?.copy()
            )
        )
    }

    /**
     * Sets up an editable idea, taken from the given item.
     */
    fun setupEdit(subjectId: Long, idea: IdeaItem, owner: LifecycleOwner) {
        currentIdea = idea.copy()
        currentSubjectId = subjectId

        isUpdating = true

        updateTextViews()

        commentsAdapter.postValue(
            CommentsAdapter(
                owner = owner,
                open = openComment,
                sourceItems = currentIdea?.comments?.copy()
            )
        )
    }


    private fun updateTextViews() {
        if (isUpdating) {
            navTitle.postValue(resourcesProvider.getString(R.string.idea_newedit_edit))
        } else {
            navTitle.postValue(resourcesProvider.getString(R.string.idea_newedit_add))
        }
        if (currentIdea != null) {
            title.postValue(currentIdea?.title)
            titleSelection.postValue(currentIdea?.title?.length)
            titleError.postValue(null)
            sum.postValue(currentIdea?.sum)
            sumSelection.postValue(currentIdea?.sum?.length)
            sumError.postValue(null)
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue(null)
            sum.value = ("")
            sumSelection.postValue(0)
            sumError.postValue(null)
        }
    }

}