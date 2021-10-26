package top.colter.mirai.plugin.genshin

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object GenshinPluginConfig: ReadOnlyPluginConfig("GenshinPluginConfig"){

    @ValueDescription("管理员账号")
    val admin: Long by value(0L)

    @ValueDescription("签到时间(0-23)")
    val signTime: Int by value(7)

    @ValueDescription("模式\n1: 单用户模式\n2: 多用户模式")
    val mode: Int by value(1)

}