package top.colter.mirai.plugin.mihoyo.signin

import kotlinx.coroutines.*
import top.colter.mirai.plugin.mihoyo.MihoyoSign
import top.colter.mirai.plugin.mihoyo.api.getAward
import top.colter.mirai.plugin.mihoyo.api.getSignInfo
import kotlin.coroutines.CoroutineContext

abstract class MihoyoSignin(
    private val taskerName: String? = null
) : CoroutineScope, CompletableJob by SupervisorJob(MihoyoSign.coroutineContext.job) {
    override val coroutineContext: CoroutineContext
        get() = this + CoroutineName(taskerName ?: this::class.simpleName ?: "Tasker")

    private val client by MihoyoSign::client

    abstract val actId: String
    abstract val refererUrl: String
    abstract val awardUrl: String
    abstract val signInfoUrl: String
    abstract val signUrl: String

    suspend fun getSignInfo() {
//        client.getAward(awardUrl, actId)
//        client.getSignInfo(signInfoUrl, actId)
    }

    override fun start(): Boolean {
        return false
    }


}