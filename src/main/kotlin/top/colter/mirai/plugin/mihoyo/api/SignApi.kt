package top.colter.mirai.plugin.mihoyo.api

import io.ktor.client.request.*
import top.colter.mirai.plugin.mihoyo.client.MihoyoClient
import top.colter.mirai.plugin.mihoyo.data.*
import top.colter.mirai.plugin.mihoyo.utils.decode
import top.colter.mirai.plugin.mihoyo.utils.encode
import top.colter.mirai.plugin.mihoyo.utils.json


suspend fun MihoyoClient.getAward(url: String, actId: String): Awards {
    return getData(url) {
        parameter("act_id", actId)
    }
}

suspend fun MihoyoClient.getSignInfo(url: String, actId: String, region: String, uid: String): SignInfoData {
    return getData(url) {
        parameter("act_id", actId)
        parameter("region", region)
        parameter("uid", uid)
    }
}

suspend fun MihoyoClient.sign(url: String, actId: String, region: String, uid: String): ResultData {
    return post(url) {
        setBody(SignData(actId, region, uid).encode())
    }
}

