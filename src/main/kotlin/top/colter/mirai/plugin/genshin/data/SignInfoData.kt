package top.colter.mirai.plugin.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInfoData(
    @SerialName("total_sign_day")
    val totalSignDay: Int,
    @SerialName("today")
    val today: String,
    @SerialName("is_sign")
    val isSign: Boolean,
    @SerialName("first_bind")
    val firstBind: Boolean,
    @SerialName("is_sub")
    val isSub: Boolean,
    @SerialName("month_first")
    val monthFirst: Boolean
)
