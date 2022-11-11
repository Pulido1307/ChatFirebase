package com.polar.industries.chatfirebase.helpers

import com.google.android.gms.common.util.Hex.stringToBytes
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException


class CifradoK {
    var PrivateKey: PrivateKey? = null
    var PublicKey: PublicKey? = null


    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun setPrivateKeyString(key: String?) {
        val encodedPrivateKey = stringToBytes(key!!)
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val privateKeySpec = PKCS8EncodedKeySpec(encodedPrivateKey)
        val privateKey: PrivateKey = keyFactory.generatePrivate(privateKeySpec)
        PrivateKey = privateKey
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun setPublicKeyString(key: String?) {
        val encodedPublicKey = stringToBytes(key!!)
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val publicKeySpec = X509EncodedKeySpec(encodedPublicKey)
        val publicKey: PublicKey = keyFactory.generatePublic(publicKeySpec)
        PublicKey = publicKey
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeySpecException::class,
        UnsupportedEncodingException::class,
        NoSuchProviderException::class
    )
    fun Encrypt(plain: String): String? {
        val encryptedBytes: ByteArray
        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, PublicKey)
        encryptedBytes = cipher.doFinal(plain.toByteArray())
        return bytesToString(encryptedBytes)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun Decrypt(result: String?): String? {
        val decryptedBytes: ByteArray
        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, PrivateKey)
        decryptedBytes = cipher.doFinal(stringToBytes(result!!))
        return String(decryptedBytes)
    }

    fun bytesToString(b: ByteArray): String? {
        val b2 = ByteArray(b.size + 1)
        b2[0] = 1
        System.arraycopy(b, 0, b2, 1, b.size)
        return BigInteger(b2).toString(36)
    }

}