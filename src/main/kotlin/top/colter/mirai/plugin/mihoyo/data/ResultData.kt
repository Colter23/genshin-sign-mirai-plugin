package top.colter.mirai.plugin.mihoyo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ResultData(
    @SerialName("retcode")
    val code: Int,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: JsonElement? = null
)