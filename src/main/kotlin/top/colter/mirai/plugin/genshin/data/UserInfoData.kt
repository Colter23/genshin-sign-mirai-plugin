package top.colter.mirai.plugin.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoData(
    @SerialName("user_info")
    val userInfo: UserInfo
)

@Serializable
data class UserInfo(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("uid")
    val uid: String
)
