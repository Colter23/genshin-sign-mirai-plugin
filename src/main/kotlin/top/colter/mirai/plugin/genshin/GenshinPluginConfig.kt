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

    @ValueDescription("教程描述\n {cookie-img} 为在插件数据路径下的cookie.png图片")
    val desc: String by value("请用电脑访问米游社并登陆 https://bbs.mihoyo.com/ys/\n之后按照下图的步骤获取cookie\n并把cookie发送给bot\n(图片发送可能存在一定延迟){cookie-img}")

}