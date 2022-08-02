package top.colter.mirai.plugin.genshin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import top.colter.mirai.plugin.genshin.data.SubscribeData
import net.mamoe.mirai.console.data.value
import top.colter.mirai.plugin.genshin.data.AwardItem

object GenshinPluginData: AutoSavePluginData("GenshinPluginData") {
    @ValueDescription("原神订阅信息")
    val genshinSub: MutableMap<Long, SubscribeData> by value(mutableMapOf())

    @ValueDescription("原神奖励月份")
    var awardsMonth: Int by value(0)

    @ValueDescription("原神奖励信息")
    var awards: List<AwardItem> by value()

    @ValueDescription("崩坏3奖励信息")
    var bh3Awards: List<AwardItem> by value()

}