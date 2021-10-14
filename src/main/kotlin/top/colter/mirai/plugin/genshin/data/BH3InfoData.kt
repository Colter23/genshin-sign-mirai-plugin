package top.colter.mirai.plugin.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BH3InfoData(
    @SerialName("sign")
    val sign: BH3SignData
)
@Serializable
data class BH3SignData(
    @SerialName("start_at")
    val start: String,
    @SerialName("end_at")
    val end: String,
    @SerialName("now")
    val now: String,
    @SerialName("list")
    val list: List<BH3AwardItem>
)

@Serializable
data class BH3AwardItem(
    @SerialName("name")
    val name: String,
    @SerialName("day")
    val day: Int,
    @SerialName("status")
    val status: Int,
    @SerialName("cnt")
    val cnt: Int
)
