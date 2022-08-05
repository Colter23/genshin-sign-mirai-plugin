package top.colter.mirai.plugin.mihoyo.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import top.colter.mirai.plugin.mihoyo.api.USER_AGENT
import top.colter.mirai.plugin.mihoyo.utils.decode
import top.colter.mirai.plugin.mihoyo.utils.json
import java.io.IOException

class MihoyoClient: Closeable {

    override fun close() = clients.forEach { it.close() }

    val clients = MutableList(3) { client() }

    private var clientIndex = 0

    protected fun client() = HttpClient(OkHttp) {
        install(UserAgent) {
            agent = USER_AGENT
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 10_000L
            connectTimeoutMillis = 10_000L
            requestTimeoutMillis = 10_000L
        }
        expectSuccess = true
        Json {
            json
        }
    }

    suspend inline fun <reified T> get(url: String, crossinline block: HttpRequestBuilder.() -> Unit = {}): T =
        useHttpClient<String> {
            it.get(url) {
//                header(HttpHeaders.Cookie, BiliBiliDynamic.cookie.toString())
                block()
            }.body()
        }.decode()

    suspend inline fun <reified T> post(url: String, crossinline block: HttpRequestBuilder.() -> Unit = {}): T =
        useHttpClient<String> {
            it.post(url) {
//                header(HttpHeaders.Cookie, BiliBiliDynamic.cookie.toString())
                block()
            }.body()
        }.decode()

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        while (isActive) {
            try {
                val client = clients[clientIndex]
                return@supervisorScope block(client)
            } catch (throwable: Throwable) {
                if (isActive && (throwable is IOException || throwable is HttpRequestTimeoutException)) {
                    clientIndex = (clientIndex + 1) % clients.size
                } else {
                    throw throwable
                }
            }
        }
        throw CancellationException()
    }

}