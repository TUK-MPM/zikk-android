package com.example.zikk.network

import com.example.zikk.model.LocationRequest
import com.example.zikk.model.ReportResponse
import com.example.zikk.model.Todo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("todos")
    suspend fun getTodos(): Response<List<Todo>>

    // 신고 작성
    @POST("/api/report")
    suspend fun sendLocation(
        @Body location: LocationRequest
    ): Response<Unit> // 또는 Response<CustomResponse> 사용 가능

    @GET("api/report")
    suspend fun getReports(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("keyword") keyword: String? = null,
        @Query("status") status: String? = null,     // 예: "COMPLETED"
        @Query("sortType") sortType: String? = null  // 예: "LATEST", "STATUS"
    ): Response<ReportResponse>

}