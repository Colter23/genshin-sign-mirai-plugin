package top.colter.mirai.plugin.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameRoles(
    @SerialName("list")
    val list: MutableList<Role>
)

@Serializable
data class Role(
    @SerialName("game_biz")
    val biz: String,
    @SerialName("region")
    val region: String,
    @SerialName("game_uid")
    val uid: String,
    @SerialName("nickname")
    val nickname: String,
//    @SerialName("level")
//    val level: Int,
//    @SerialName("is_chosen")
//    val isChosen: Boolean,
    @SerialName("region_name")
    val regionName: String
//    @SerialName("is_official")
//    val isOfficial: Boolean
)
