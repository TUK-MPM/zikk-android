import com.google.gson.annotations.SerializedName

data class ReportResponse(
    val reportId: Long,
    val message: String,

    @SerializedName("reason")
    val type: String,  // reason을 type으로 매핑하여 클라이언트 일관성 유지

    val createdAt: String,
    val repliedAt: String?
)
