package io.github.zimoyin.qqbot.net.webhook.handler

import io.github.zimoyin.qqbot.bot.Bot
import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.util.encoders.Hex

/**
 *
 * @author : zimo
 * @date : 2024/12/21
 */
class Verify {

    fun verify(bot: Bot, ts: String, token: String): String {
        val bodyBytes = Buffer.buffer(ts + token).bytes


        val seed = generateSeed(bot.config.token.clientSecret)
        val privateKeyParams = Ed25519PrivateKeyParameters(seed)
        return signMessage(privateKeyParams, bodyBytes)
    }

    fun signMessage(privateKey: Ed25519PrivateKeyParameters, msg: ByteArray): String {
        val signer = Ed25519Signer()
        signer.init(true, privateKey)  // Initialize the signer with the private key for signing
        signer.update(msg, 0, msg.size)  // Update the signer with the message
        val signature = signer.generateSignature()  // Generate the signature
        return Hex.toHexString(signature)  // Convert the signature to a hex string
    }


    fun generateSeed(botSecret: String): ByteArray {
        val SeedSize = 32
        var seed = botSecret
        while (seed.length < SeedSize) {
            seed += seed
        }
        return seed.substring(0, SeedSize).toByteArray()
    }
}
