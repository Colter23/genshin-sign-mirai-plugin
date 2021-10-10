package top.colter.mirai.plugin.genshin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.utils.warning
import top.colter.mirai.plugin.genshin.PluginMain.contactMap
import top.colter.mirai.plugin.genshin.PluginMain.contactMutex
import top.colter.mirai.plugin.genshin.PluginMain.httpUtils
import top.colter.mirai.plugin.genshin.data.Awards
import top.colter.mirai.plugin.genshin.data.ResultData
import top.colter.mirai.plugin.genshin.data.SignInfoData
import top.colter.mirai.plugin.genshin.data.SubscribeData
import top.colter.mirai.plugin.genshin.utils.AesUtils
import top.colter.mirai.plugin.genshin.utils.decode
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*


object GenshinTasker: CoroutineScope by PluginMain.childScope("GenshinTasker") {

    private var listener: Job? = null

    val mutex = Mutex()
    val genshinSub: MutableMap<Long, SubscribeData> by GenshinPluginData::genshinSub

    fun start(){
        listener = listen()
    }

    fun stop(){
        listener?.cancel()
    }

    private fun listen() = launch{



        while (true){
            runCatching {
                if (LocalTime.now().hour == 7){
                    sign()
                }
            }.onSuccess {
                val next = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(7,(1..10).random()))
                val instant = Instant.now().until(next.toInstant(OffsetDateTime.now().offset), ChronoUnit.MILLIS)
                delay(instant)
            }.onFailure {
                delay(Duration.ofMinutes(30).toMillis())
            }
        }
    }

    private fun updateAwards(){
        if (GenshinPluginData.awardsMonth != LocalDate.now().monthValue){
            runCatching {
                val awards = httpUtils.getAndDecode<Awards>(AWARD_URL)
                GenshinPluginData.awardsMonth = awards.month
                GenshinPluginData.awards = awards.awards
            }.onFailure {
                PluginMain.logger.warning({ "获取奖励信息失败" }, it)
            }
        }
    }

    suspend fun sign(){
        val subData: Map<Long, SubscribeData>
        mutex.withLock { subData = genshinSub.toMap() }
        updateAwards()
        subData.forEach { (k, v) -> signAction(k, v) }
    }

    suspend fun signSingle(delegate: Long){
        updateAwards()
        genshinSub[delegate]?.let { signAction(delegate,it) }
    }

    private suspend fun signAction(delegate: Long, subData: SubscribeData){
        var successMessage = "====原神签到====\n"
        var errorMessage = "====签到失败====\n"
        subData.accounts.forEach {
            httpUtils.cookie = AesUtils.decrypt(it.cookie, "$delegate${it.uid}")?:""
            it.gameRoles?.forEach l@{ r ->
                runCatching{
                    val signInfo = httpUtils.getAndDecode<SignInfoData>(INFO_URL(r.region, r.uid))
                    if (signInfo.isSign){
                        if (subData.pushMsg){
                            successMessage += "旅行者: ${r.nickname}\n" +
                                "今天已经签过到了( •̀ ω •́ )y\n" +
                                "=============\n"
                        }
                        return@l
                    }
                    val postBody = "{\"act_id\":\"${ACT_ID}\",\"region\":\"${r.region}\",\"uid\":\"${r.uid}\"}"

                    val res = httpUtils.post(SIGN_URL, postBody).decode<ResultData>()
                    if (res.code == 0){
                        val award = GenshinPluginData.awards[signInfo.totalSignDay]
                        if (subData.pushMsg){
                            successMessage += "旅行者: ${r.nickname}\n" +
                                "奖励: ${award.name}x${award.count}\n" +
                                "天数: ${signInfo.totalSignDay+1}\n" +
                                "=============\n"
                        }
                    }else{
                        errorMessage += "旅行者: ${r.nickname}" +
                            "签到失败了(っ °Д °;)っ\n" +
                            "=============\n"
                    }
                }.onFailure { e ->
                    errorMessage += "旅行者: ${r.nickname}" +
                        "签到失败了(っ °Д °;)っ\n" +
                        "=============\n"
                    PluginMain.logger.warning({ "签到失败" }, e)
                }
            }
        }
        if (subData.pushMsg){
            if (successMessage != "====原神签到====\n"){
                delegate.sendMessage(successMessage)
            }
        }
        if (errorMessage != "====签到失败====\n"){
            delegate.sendMessage(errorMessage)
        }
    }

}

suspend inline fun Long.sendMessage(message: String) {
    runCatching {
        requireNotNull(findContact(this)) { "找不到联系人" }.sendMessage(message)
    }.onFailure {
        PluginMain.logger.warning({ "对[${this}]构建消息失败" }, it)
    }
}


/**
 * 查找Contact
 */
suspend fun findContact(delegate: Long): Contact? = contactMutex.withLock{
    contactMap[delegate]?.let { return@findContact it }
    Bot.instances.forEach { bot ->
        if (delegate < 0) {
            bot.getGroup(delegate * -1)?.let {
                contactMap[delegate] = it
                return@findContact it
            }
        } else {
            bot.getFriend(delegate)?.let {
                contactMap[delegate] = it
                return@findContact it
            }
            bot.getStranger(delegate)?.let {
                contactMap[delegate] = it
                return@findContact it
            }
            bot.groups.forEach { group ->
                group.getMember(delegate)?.let {
                    contactMap[delegate] = it
                    return@findContact it
                }
            }
        }
    }
    return null
}

/**
 * 通过正负号区分群和用户
 * @author cssxsh
 */
val Contact.delegate get() = if (this is Group) id * -1 else id
