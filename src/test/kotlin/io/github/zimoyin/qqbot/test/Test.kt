import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.utils.vertxWorker
import kotlinx.coroutines.*
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import kotlin.system.measureTimeMillis

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    GLOBAL_VERTX_INSTANCE.runOnContext {  }
//    val numTasks = 1_000_000
    val numTasks = 100
    val AAA = AAA()

    val time = measureTimeMillis {
        val jobs = mutableListOf<Job>()

        for (i in 1..numTasks) {
//            val job = CoroutineScope(Dispatchers.vertx(GLOBAL_VERTX_INSTANCE)).launch {
            val job = CoroutineScope(Dispatchers.vertxWorker(GLOBAL_VERTX_INSTANCE)).launch {
//            val job = CoroutineScope(Dispatchers.Default).launch {
                println("正在执行任务 $i, 线程： ${Thread.currentThread().name}")
                // 生成密钥对
                val keyPair = AAA.generateKeyPair()
                val publicKey = keyPair.public
                val privateKey = keyPair.private

                // 要加密的数据
                val plainText = "123456789"

                // 加密数据
                val encryptedData = AAA.encrypt(publicKey, plainText)
                // 解密数据
                val decryptedData = AAA.decrypt(privateKey, encryptedData)
            }
            jobs.add(job)
        }

        // 等待所有任务完成
        jobs.forEach { it.join() }
    }

    println("总耗时：$time ms")
}

fun isPrime(n: Int): Boolean {
    if (n <= 1) return false
    if (n <= 3) return true
    if (n % 2 == 0 || n % 3 == 0) return false
    var i = 5
    while (i * i <= n) {
        if (n % i == 0 || n % (i + 2) == 0) return false
        i += 6
    }
    return true
}

class AAA {

    private val keySize = 2048 // 指定密钥长度，这里使用 2048 bit

    /**
     * 生成 RSA 密钥对
     */
    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(keySize)
        return keyPairGenerator.generateKeyPair()
    }

    /**
     * 使用公钥加密数据
     */
    fun encrypt(publicKey: PublicKey, data: String): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data.toByteArray())
    }

    /**
     * 使用私钥解密数据
     */
    fun decrypt(privateKey: PrivateKey, encryptedData: ByteArray): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(cipher.doFinal(encryptedData))
    }
}
