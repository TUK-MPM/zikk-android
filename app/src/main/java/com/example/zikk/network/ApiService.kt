package com.example.zikk.network

import com.example.zikk.model.ReportResponse
import com.example.zikk.model.Todo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("todos")
    suspend fun getTodos(): Response<List<Todo>>

    @GET("reports")
    suspend fun getReports(@Query("page") page: Int): Response<ReportResponse>
}