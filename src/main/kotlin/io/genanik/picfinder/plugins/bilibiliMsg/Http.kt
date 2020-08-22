package io.genanik.picfinder.plugins.bilibiliMsg

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun get(url: String): String {
    // 去get
    val con: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
    con.requestMethod = "GET"
    con.setRequestProperty("contentType", "UTF-8")
    val `in` = BufferedReader(InputStreamReader(con.inputStream, "UTF-8"))
    var inputLine: String?
    val response = StringBuffer()

    while (`in`.readLine().also { inputLine = it } != null) {
        response.append(inputLine)
    }
    `in`.close()

    return response.toString()
}