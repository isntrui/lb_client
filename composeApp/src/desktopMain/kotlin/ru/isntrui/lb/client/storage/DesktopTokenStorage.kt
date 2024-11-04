package ru.isntrui.lb.client.storage

import java.io.File
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

actual object TokenStorage {
    private const val TOKEN_FILE_PATH = "token.enc"
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val KEY_FILE_PATH = ".key"

    private fun generateSecretKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance(ALGORITHM)
        keyGen.init(256)
        return keyGen.generateKey()
    }

    private fun loadKey(): SecretKey {
        val keyBytes = File(KEY_FILE_PATH).readBytes()
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    private fun saveKey(key: SecretKey) {
        File(KEY_FILE_PATH).writeBytes(key.encoded)
    }

    actual fun saveToken(token: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val secretKey: SecretKey = if (File(KEY_FILE_PATH).exists()) {
            loadKey()
        } else {
            val newKey = generateSecretKey()
            saveKey(newKey)
            newKey
        }

        val iv = ByteArray(16)
        Random.nextBytes(iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encryptedToken = cipher.doFinal(token.toByteArray())

        File(TOKEN_FILE_PATH).writeBytes(iv + encryptedToken)
    }

    actual fun getToken(): String? {
        return if (System.getProperty("token") != null) System.getProperty("token") else {
            if (!File(TOKEN_FILE_PATH).exists() || !File(KEY_FILE_PATH).exists()) {
                println()
                return null
            }
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = loadKey()
            val data = File(TOKEN_FILE_PATH).readBytes()
            val iv = data.copyOfRange(0, 16)
            val encryptedToken = data.copyOfRange(16, data.size)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            val token = String(cipher.doFinal(encryptedToken))
            System.setProperty("token", token)
            token
        }
    }

    actual fun clearToken() {
        File(TOKEN_FILE_PATH).delete()
    }
}