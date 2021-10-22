package top.colter.mirai.plugin.genshin

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object GenshinPluginConfig: ReadOnlyPluginConfig("GenshinPluginConfig"){

    @ValueDescription("模式\n1: 单用户模式\n2: 多用户模式")
    val mode: Int by value(1)

}