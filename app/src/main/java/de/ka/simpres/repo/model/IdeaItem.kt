package de.ka.simpres.repo.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import java.io.Serializable

@Entity
data class IdeaItem(
    @Id var id: Long,
    var subjectId: Long,
    var title: String = "",
    var done: Boolean = false,
    var sum: String = "",
    @Convert(converter = CommentsConverter::class, dbType = String::class) var comments: Comments? = null
) : Serializable

data class Comments(var comments: List<Comment>)

data class Comment(var id: Long, var text: String, var isLink: Boolean)

class CommentsConverter : PropertyConverter<Comments, String> {

    override fun convertToEntityProperty(databaseValue: String?): Comments? {
        if (databaseValue == null) {
            return null
        }

        val mapper = ObjectMapper().registerModule(KotlinModule())
        return mapper.readValue<Comments>(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: Comments?): String? {
        if (entityProperty == null) {
            return null
        }

        val mapper = ObjectMapper().registerModule(KotlinModule())
        val writer = mapper.writerFor(Comments::class.java)
        return writer.writeValueAsString(entityProperty)
    }
}
