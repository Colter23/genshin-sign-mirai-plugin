package top.colter.mirai.plugin.genshin.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object AesUtils {

     fun encrypt(data: String, k: String): String? {
        var key = k
        if (key.length < 16) key += k
        val keySpec = SecretKeySpec(key.substring(0,16).toByteArray(), "AES")
        val iv = IvParameterSpec(md5Hex(key).substring(0,16).toByteArray())
        return try {
            val cipher = Cipher.getInstance("AES/CFB/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
            Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray()))
        } catch (e: Exception) {
            null
        }
    }

    fun decrypt(secret: String, k: String): String? {
        var key = k
        if (key.length < 16) key += k
        val keySpec = SecretKeySpec(key.substring(0,16).toByteArray(), "AES")
        val iv = IvParameterSpec(md5Hex(key).substring(0,16).toByteArray())
        return try {
            val cipher = Cipher.getInstance("AES/CFB/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
            String(cipher.doFinal(Base64.getDecoder().decode(secret)))
        } catch (e: Exception) {
            null
        }
    }

    fun randomKey(size: Int): ByteArray? {
        var result: ByteArray? = null
        result = try {
            val gen: KeyGenerator = KeyGenerator.getInstance("AES")
            gen.init(size, SecureRandom())
            gen.generateKey().encoded
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return result
    }
}

fun md5Hex(str: String): String {
    val secretBytes = MessageDigest.getInstance("md5").digest(str.toByteArray())
    var md5code: String = BigInteger(1, secretBytes).toString(16)
    for (i in 0 until 32 - md5code.length) {
        md5code = "0$md5code"
    }
    return md5code
}