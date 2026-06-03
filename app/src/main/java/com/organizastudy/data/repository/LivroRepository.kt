package com.organizastudy.data.repository

import com.organizastudy.model.RespostaLivros
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun buscarLivros(
        @Query("q")     query: String,
        @Query("limit") limit: Int = 10
    ): RespostaLivros
}

class LivroRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(OpenLibraryApi::class.java)

    suspend fun buscarLivros(query: String): Result<RespostaLivros> = try {
        Result.success(api.buscarLivros(query))
    } catch (e: Exception) {
        Result.failure(e)
    }
}