package top.colter.mirai.plugin.genshin.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import top.colter.mirai.plugin.genshin.APP_VERSION
import top.colter.mirai.plugin.genshin.REFERER_URL
import top.colter.mirai.plugin.genshin.USER_AGENT
import top.colter.mirai.plugin.genshin.data.ResultData
import java.math.BigInteger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.util.*

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}
inline fun <reified T> JsonElement.decode(): T = json.decodeFromJsonElement(json.serializersModule.serializer(),this)

class HttpUtils {

    var cookie: String = ""

    private var client: HttpClient = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(10000)).build()

    private fun getDS(): String {
        val n = "h8w582wxwgqvahcdkpvdhbh2w9casgfl"
        val i = Instant.now().epochSecond
        val r = getRandomStr()
        val c = md5Hex("salt=$n&t=$i&r=$r")
        return "${i},${r},${c}"
    }

    private fun getRandomStr(): String {
        val random = Random()
        val sb = StringBuilder()
        for (i in 1..6) {
            val CONSTANTS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val number = random.nextInt(CONSTANTS.length)
            val charAt = CONSTANTS[number]
            sb.append(charAt)
        }
        return sb.toString()
    }

    private fun getBasicHeader(): HttpRequest.Builder {
        return HttpRequest.newBuilder()
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("User-Agent", USER_AGENT)
            .header("Referer", REFERER_URL)
            .header("Accept-Encoding", "deflate, br")
            .header("Cookie",cookie)
    }

    private fun getHeader(): HttpRequest.Builder {
        return getBasicHeader()
//            .header("Accept-Encoding", "gzip, deflate, br")
            .header("x-rpc-device_id", UUID.randomUUID().toString().replace("-", "").toUpperCase())
            .header("x-rpc-client_type","5")
            .header("x-rpc-app_version", APP_VERSION)
            .header("DS",getDS())
//            .header("x-rpc-channel", "appstore")
//            .header("accept-language", "zh-CN,zh;q=0.9,ja-JP;q=0.8,ja;q=0.7,en-US;q=0.6,en;q=0.5")
//            .header("accept-encoding", "gzip, deflate")
//            .header("x-requested-with", "com.mihoyo.hyperion")
//            .header("Host", "api-takumi.mihoyo.com")
    }

    private fun sendRequest(request: HttpRequest): JsonElement {
        val bodyHandler: HttpResponse.BodyHandler<String> = HttpResponse.BodyHandlers.ofString()
        val response: HttpResponse<String> = client.send(request, bodyHandler)
        val body: String = response.body()
        return json.parseToJsonElement(body)
    }

    fun get(url: String): JsonElement {
        val request = getBasicHeader().uri(URI.create(url)).GET().build()
        return sendRequest(request)
    }

    fun post(url: String, postBody: String): JsonElement {
        val request = getHeader().uri(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(postBody)).build()
        return sendRequest(request)
    }

    inline fun <reified T> getAndDecode(url: String): T{
        val js = get(url).decode<ResultData>()
        if (js.code != 0){
            throw Exception(js.message)
        }
        return js.data!!.decode()
    }

}