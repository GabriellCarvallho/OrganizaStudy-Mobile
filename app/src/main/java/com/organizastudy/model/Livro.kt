package com.organizastudy.model

import com.google.gson.annotations.SerializedName

data class RespostaLivros(
    @SerializedName("docs") val livros: List<Livro> = emptyList()
)

data class Livro(
    @SerializedName("title")              val titulo: String = "",
    @SerializedName("author_name")        val autores: List<String>? = null,
    @SerializedName("first_publish_year") val ano: Int? = null,
    @SerializedName("subject")            val assuntos: List<String>? = null
)