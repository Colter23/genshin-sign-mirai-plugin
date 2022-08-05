package top.colter.mirai.plugin.mihoyo

import kotlinx.coroutines.sync.Mutex
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.utils.info
import top.colter.mirai.plugin.mihoyo.client.MihoyoClient


object MihoyoSign : KotlinPlugin(
    JvmPluginDescription(
        id = "top.colter.genshin-sign",
        name = "GenshinSign",
        version = "0.3.6"
    ) {
        author("Colter")
        info(
            """
            原神米游社签到
        """.trimIndent()
        )
        // author 和 info 可以删除.
    }
) {

    val contactMutex = Mutex()
    val contactMap: MutableMap<Long, Contact> = mutableMapOf()

    val client = MihoyoClient()

    override fun onEnable() {
        logger.info { "Genshin Plugin loaded" }
//        GenshinPluginData.reload()
//        GenshinPluginConfig.reload()
//        Listener.subscribe()
//
//        if (Bot.instances.isEmpty()) {
//            globalEventChannel().subscribeOnce<BotOnlineEvent> { GenshinTasker.start() }
//        } else {
//            GenshinTasker.start()
//        }

    }
    override fun onDisable() {
//        Listener.stop()
//        GenshinTasker.stop()
    }
}
