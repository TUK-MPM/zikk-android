package com.example.zikk.network

import com.example.zikk.model.ReportResponse
import com.example.zikk.model.Todo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("todos")
    suspend fun getTodos(): Response<List<Todo>>

    @GET("api/report")
    suspend fun getReports(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("keyword") keyword: String? = null,
        @Query("status") status: String? = null,     // 예: "COMPLETED"
        @Query("sortType") sortType: String? = null  // 예: "LATEST", "STATUS"
    ): Response<ReportResponse>

}