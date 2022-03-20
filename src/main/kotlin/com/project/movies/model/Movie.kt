package com.project.movies.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "movie")
data class Movie(
    @Id var id: String?,
    val name: String,
    val actor: String,
    val type: String
)