package com.example.zikk.network

import com.example.zikk.model.Todo
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("todos")
    suspend fun getTodos(): Response<List<Todo>>
}