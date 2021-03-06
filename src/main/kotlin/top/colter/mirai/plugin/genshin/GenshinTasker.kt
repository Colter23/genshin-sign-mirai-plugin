package top.colter.mirai.plugin.genshin

import kotlinx.coroutines.*
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
import top.colter.mirai.plugin.genshin.data.*
import top.colter.mirai.plugin.genshin.utils.AesUtils
import top.colter.mirai.plugin.genshin.utils.decode
import java.time.*
import java.time.temporal.ChronoUnit


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
        while (isActive){
            runCatching {
                if (LocalTime.now().hour == GenshinPluginConfig.signTime){
                    sign()
                }
            }.onSuccess {
                val next = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(GenshinPluginConfig.signTime,(1..10).random()))
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
                PluginMain.logger.warning({ "????????????????????????" }, it)
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
        var successMessage = "====????????????====\n"
        var bh3SuccessMessage = "====??????3??????====\n"
        var errorMessage = "====????????????====\n"

        subData.accounts.forEach {
            httpUtils.cookie = AesUtils.decrypt(it.cookie, "$delegate${it.uid}")?:""
            httpUtils.actId = ACT_ID
            it.gameRoles?.forEach l@{ r ->
                runCatching{
                    val signInfo = httpUtils.getAndDecode<SignInfoData>(INFO_URL(r.region, r.uid))
                    if (signInfo.isSign){
                        if (subData.pushMsg){
                            successMessage += "?????????: ${r.nickname}\n" +
                                "????????????????????????( ????? ?? ????? )y\n" +
                                "===============\n"
                        }
                        return@l
                    }
                    val postBody = "{\"act_id\":\"${ACT_ID}\",\"region\":\"${r.region}\",\"uid\":\"${r.uid}\"}"

                    val res = httpUtils.post(SIGN_URL, postBody).decode<ResultData>()
                    if (res.code == 0){
                        val award = GenshinPluginData.awards[signInfo.totalSignDay]
                        if (subData.pushMsg){
                            successMessage += "?????????: ${r.nickname}\n" +
                                "??????: ${award.name}x${award.count}\n" +
                                "??????: ${signInfo.totalSignDay+1}\n" +
                                "===============\n"
                        }
                    }else{
                        errorMessage += "?????????: ${r.nickname}" +
                            "???????????????(??? ???? ??;)???\n" +
                            "===============\n"
                    }
                }.onFailure { e ->
                    errorMessage += "?????????: ${r.nickname}" +
                        "???????????????(??? ???? ??;)???\n" +
                        "===============\n"
                    PluginMain.logger.warning({ "????????????" }, e)
                }
            }
            httpUtils.actId = BH3_ACT_ID
            it.bh3GameRoles?.forEach b@{ r->
                runCatching{
//                    val signInfo = httpUtils.getAndDecode<BH3InfoData>(BH3_INFO_URL(r.region, r.uid))
                    val postBody = "{\"act_id\":\"${BH3_ACT_ID}\",\"region\":\"${r.region}\",\"uid\":\"${r.uid}\"}"
                    val res = httpUtils.post(BH3_SIGN_URL, postBody).decode<ResultData>()
                    when(res.code){
                        0 -> {
                            val signData = res.data?.decode<BH3SignData>()
                            val award = signData?.list?.stream()?.filter { a -> a.status == 2 }?.max { o1, o2 -> if(o1.day>o2.day) 1 else -1 }?.get()

                            if (subData.pushMsg){
                                bh3SuccessMessage += "??????: ${r.nickname}\n" +
                                    "??????: ${award?.name}x${award?.cnt}\n" +
                                    "??????: ${award?.day}\n" +
                                    "===============\n"
                            }
                        }
                        -5003 -> {
                            if (subData.pushMsg){
                                bh3SuccessMessage += "??????: ${r.nickname}\n" +
                                    "????????????????????????( ????? ?? ????? )y\n" +
                                    "===============\n"
                            }
                        }
                        else -> {
                            errorMessage += "??????: ${r.nickname}" +
                                "???????????????(??? ???? ??;)???\n" +
                                "===============\n"
                        }
                    }
                }.onFailure { e ->
                    errorMessage += "??????: ${r.nickname}" +
                        "???????????????(??? ???? ??;)???\n" +
                        "===============\n"
                    PluginMain.logger.warning({ "????????????" }, e)
                }
            }
        }
        if (subData.pushMsg){
            if (successMessage != "====????????????====\n"){
                delegate.sendMessage(successMessage.dropLast(1))
            }
            if (bh3SuccessMessage != "====??????3??????====\n"){
                delegate.sendMessage(bh3SuccessMessage.dropLast(1))
            }
        }
        if (errorMessage != "====????????????====\n"){
            delegate.sendMessage(errorMessage.dropLast(1))
        }
    }

}

suspend inline fun Long.sendMessage(message: String) {
    runCatching {
        requireNotNull(findContact(this)) { "??????????????????" }.sendMessage(message)
    }.onFailure {
        PluginMain.logger.warning({ "???[${this}]??????????????????" }, it)
    }
}


/**
 * ??????Contact
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
 * ?????????????????????????????????
 * @author cssxsh
 */
val Contact.delegate get() = if (this is Group) id * -1 else id
