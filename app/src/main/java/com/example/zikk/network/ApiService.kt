package com.example.zikk.network

import com.example.zikk.model.request.LocationRequest
import com.example.zikk.model.request.LoginRequest
import com.example.zikk.model.response.LoginResponse
import com.example.zikk.model.Report
import com.example.zikk.model.response.ReportResponse
import com.example.zikk.model.Todo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("todos")
    suspend fun getTodos(): Response<List<Todo>>

    // 유저/관리자 로그인
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // 신고 작성
    @POST("report")
    suspend fun sendLocation(
        @Body location: LocationRequest
    ): Response<Unit> // 또는 Response<CustomResponse> 사용 가능

    // 메인페이지 처리 사례 조회
    @GET("report/examples")
    suspend fun getReportExamples(): Response<List<Report>>

    // 관리자 신고 내역 전체 조회
    @GET("report")
    suspend fun getReports(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("keyword") keyword: String? = null,
        @Query("status") status: String? = null,     // 예: "COMPLETED"
        @Query("sortType") sortType: String? = null  // 예: "LATEST", "STATUS"
    ): Response<ReportResponse>

    @GET("report/{id}")
    suspend fun getReportDetail(
        @Path("id") reportId: String,
    ): Response<Report>

    // 관리자 통계 조회
    @GET("statistics")
    suspend fun getStatistics()

    @PATCH("report/status/{id}")
    suspend fun updateReportStatus(
        @Path("id") reportId: String,
        @Body status: String
    )
}