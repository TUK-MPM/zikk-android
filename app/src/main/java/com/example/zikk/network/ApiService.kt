package com.example.zikk.network

import com.example.zikk.enum.SortType
import com.example.zikk.model.request.ReportRequest
import com.example.zikk.model.request.LoginRequest
import com.example.zikk.model.response.LoginResponse
import com.example.zikk.model.Report
import com.example.zikk.model.ReportDetail
import com.example.zikk.model.response.ReportResponse
import com.example.zikk.model.Todo
import com.example.zikk.model.request.CreateNoticeRequest
import com.example.zikk.model.response.NoticeResponse
import com.example.zikk.model.response.ReportUpdateResponse
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
        @Body location: ReportRequest
    ): Response<Unit> // 또는 Response<CustomResponse> 사용 가능

    // 신고 수정
    @PATCH("report/{report_id}")
    suspend fun updateReport(
        @Path("report_id") reportId: String,
        @Body request: ReportRequest
    ): Response<ReportUpdateResponse>

    // 메인페이지 처리 사례 조회
    @GET("report/examples")
    suspend fun getReportExamples(): Response<List<Report>>

    // 관리자 - 사용자 신고 내역 전체 조회
    @GET("report")
    suspend fun getReports(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<List<Report>>

    // 상세 조회
    @GET("report/{id}")
    suspend fun getReportDetail(
        @Header("Authorization") token: String,
        @Path("id") reportId: String,
    ): Response<ReportDetail>

    // 관리자 통계 조회
    @GET("statistics")
    suspend fun getStatistics()


    @PATCH("report/status/{id}")
    suspend fun updateReportStatus(
        @Path("id") reportId: String,
        @Body status: String
    )

    @GET("notice")
    suspend fun getNotices(
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Query("keyword") keyword: String,
        @Query("sortType") sortType: SortType
    ): Response<NoticeResponse>

    @GET("notice/{id}")
    suspend fun getNotice()

    @POST("notice")
    suspend fun createNotice(
        @Header("Authorization") token: String,
        @Body request: CreateNoticeRequest
    ): Response<String>
}