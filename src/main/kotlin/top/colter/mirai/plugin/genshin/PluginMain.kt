package top.colter.mirai.plugin.genshin

import kotlinx.coroutines.sync.Mutex
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.disable
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.utils.info
import top.colter.mirai.plugin.genshin.utils.HttpUtils
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.imageio.ImageIO

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "top.colter.genshin-sign",
        name = "GenshinSign",
        version = "0.1.0"
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

    val httpUtils = HttpUtils()

    override fun onEnable() {
        logger.info { "Genshin Plugin loaded" }
        GenshinPluginData.reload()
        GenshinPluginConfig.reload()
        Listener.subscribe()
        GenshinTasker.start()

        val cookieImg = dataFolder.resolve("cookie.png")
        if (!cookieImg.exists()) {
            val imgInput = URL("https://img.colter.top/cookie.png").openConnection().getInputStream()
            imgInput.transferTo(FileOutputStream(cookieImg))
            imgInput.close()
        }
    }
    override fun onDisable() {
        Listener.stop()
        GenshinTasker.stop()
    }
}