package com.isen.secumobileisen

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

class Encrypt {

    fun generateSymmetricKeyCBC(keyAlias: String): Key {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")


            val keyGenParameterSpec =
                KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()

            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
            return keyStore.getKey(keyAlias,null)

    }

    fun getKey(keyAlias: String): Key {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(keyAlias,null)
    }

    fun encryptCBC(plainText: ByteArray, key: Key) : Pair<ByteArray,ByteArray>? {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE,key)
        val cipherText = cipher.doFinal(plainText)
        return Pair(cipherText,cipher.iv)
    }

    fun decryptCBC(cipherText: ByteArray, key: Key, iv: ByteArray? = null) : ByteArray? {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE,key, IvParameterSpec(iv))
        val plainText = cipher.doFinal(cipherText)
        return plainText
    }

}