package de.ka.simpres.repo.model

import java.io.Serializable

class IdeaItem(var id: String = "0", var title: String = "", var done: Boolean = false, var sum: String = "") :
    Serializable