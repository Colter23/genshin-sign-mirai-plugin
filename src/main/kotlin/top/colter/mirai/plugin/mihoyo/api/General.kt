package top.colter.mirai.plugin.mihoyo.api

import io.ktor.client.request.*
import top.colter.mirai.plugin.mihoyo.client.MihoyoClient
import top.colter.mirai.plugin.mihoyo.data.ResultData
import top.colter.mirai.plugin.mihoyo.utils.decode


internal suspend inline fun <reified T> MihoyoClient.getData(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): T {
    val res = get<ResultData>(url, block)

    return if (res.code != 0 || res.data == null) {
        throw Exception("URL: $url ${res.message}")
    } else {
        res.data.decode()
    }
}