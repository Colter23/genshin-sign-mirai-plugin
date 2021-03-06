package top.colter.mirai.plugin.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeData (
    @SerialName("push_msg")
    var pushMsg: Boolean = true,
    @SerialName("accounts")
    val accounts: MutableList<AccountData> = mutableListOf()
)

@Serializable
data class AccountData(
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("uid")
    val uid: String = "",
    @SerialName("game_roles")
    var gameRoles: MutableList<Role>? = null,
    @SerialName("bh3_game_roles")
    var bh3GameRoles: MutableList<Role>? = null,
    @SerialName("cookie")
    val cookie: String = ""
)
