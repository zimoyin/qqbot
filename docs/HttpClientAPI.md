
1. 发送Http请求
   * get/post/postJson/postJsonObject
     * DefaultHttpClient.get()
     * DefaultHttpClient.post()
     * .....
     * 该方法只提供最基本的发送请求功能以及携带参数与请求头等，不支持自定义各项配置等。
     * 该方法为阻塞方法
   * createGet()/createPost()/createPut/createDelete()/createHead()/createPatch()
     * DefaultHttpClient.createGet()
     * DefaultHttpClient.createPost()
     * .....
     * 该方法提供创建请求对象功能，允许配置各项参数，如请求头、请求体、请求参数、请求体类型、请求体编码等。
     * 该方法为非阻塞方法
   *  DefaultHttpClient.isHeadSSL 属性
     * 是否使用使用 head 查看服务器是否支持 SSL。如果设置为 false 则不自动修改 http 协议.(只对get/post/等方法有效) (不针对 HEAD 方法)
     * 该方法通常用于检测服务器是否支持 SSL。并自动更改http为https，或者https为http。
2. 创建Client 客户端
   * 上面各种功能都是存在一个默认的`HttpClient`客户端`DefaultHttpClient.DefaultClient`通过他来发送请求。
   * 获取客户端： DefaultHttpClient.DefaultClient
   * 创建一个新的客户端： DefaultHttpClient.createClient()
   * 创建自定义客户端需要提供客户端配置，默认配置为：DefaultHttpClient.DefaultOptions 。如果不合适请自行创建
     * setDefaultHost("sandbox.api.sgroup.qq.com") // 配置默认服务器，这样提供客户端直接使用 get(uri) 等方法就是默认访问该服务器； 如 get("/api/v1/user/info")
     * ssl(true) // 是否使用 SSL。如果服务器使用了SSL 则需要设置为 true
   * 通过客户端发送请求：
       * DefaultHttpClient.DefaultClient.get().... (使用复杂)
       * DefaultHttpClient.DefaultClient.getAbs()... (使用简单)
       * 注意 get(uri) 方法需要客户端设置了默认服务器，并指定了是否使用 SSL。
3. Post 发送 from-data 示例
```kotlin
// 构建表单。自行理解各项方法，或查阅源码
  val form = MultipartForm.create()
  form.attribute("字段key","字段value")
  // 第一个参数为服务器接收的文件变量字段并非文件名称。第二项为文件名称
  // 部分服务器无法识别到该HttpClient 框架发送的 from-data。如果需要自行构建请见下列代码
  form.binaryFileUpload("file_image", UUID.randomUUID().toString(), File("/test.json").toString(), "application/json")
  DefaultHttpClient.createPost("https://test.com").sendMultipartForm(from)
```

4. JDK 发送  from-data 示例
```kotlin
private fun uploadImage(inputStream: InputStream, fileName: String = "image.jpg"): String {
    val url = "https://www.imgtp.com/api/upload"

    val boundary = UUID.randomUUID().toString()
    val lineEnd = "\r\n"
    val twoHyphens = "--"

    var connection: HttpURLConnection? = null
    try {
        // Create connection
        val apiUrl = URL(url)
        connection = apiUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
        )
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        connection.doOutput = true

        // Create output stream writer
        val outputStream = DataOutputStream(connection.outputStream)

        // Write file part
        outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"$fileName\"$lineEnd")
        outputStream.writeBytes("Content-Type: multipart/form-data$lineEnd$lineEnd")

        // 循环读取 inputStream
        val buffer = ByteArray(1024)
        while (inputStream.read(buffer) != -1) {
            outputStream.write(buffer)
            outputStream.flush()
        }

        outputStream.writeBytes(lineEnd)

        // End of multipart/form-data
        outputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")

        // Flush output stream
        outputStream.flush()
        outputStream.close()

        // Get response
        val br = BufferedReader(InputStreamReader(connection.inputStream))
        var line: String?
        val response = StringBuilder()
        while (br.readLine().also { line = it } != null) {
            response.append(line)
        }
        br.close()

        return response.toString()
    } catch (e: Exception) {
        throw e
    } finally {
        connection?.disconnect()
    }
}

```
