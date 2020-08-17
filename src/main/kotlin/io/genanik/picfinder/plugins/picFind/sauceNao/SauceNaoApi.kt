package io.genanik.picfinder.plugins.picFind.sauceNao

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SauceNaoApi(ApiKey: String, debug: Boolean) {
    private val outputType = 2 // json
    private val apiKey = ApiKey
    private var testMode = 0 // false

    init {
        testMode = if (debug){
            1
        }else{
            0
        }
    }

    fun searchPic(searchMode: SearchMode, picUrl: String): String {
        return(request(searchMode.value, picUrl , 0))
    }

    private fun request(db: Int, picUrl: String, times: Int): String {
        if (times > 3){
            throw Exception("请求次数过多")
        }
        // 造url
        val fullURL = StringBuffer()
        fullURL.append("https://saucenao.com/search.php?")
        fullURL.append("db=$db&")
        fullURL.append("output_type=$outputType&")
        fullURL.append("testmode=$testMode&")
        fullURL.append("api_key=$apiKey&")
        fullURL.append("numres=1&")
        fullURL.append("url=$picUrl")

        // 去get
        val con: HttpURLConnection = URL(fullURL.toString()).openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        val `in` = BufferedReader(InputStreamReader(con.inputStream))
        var inputLine: String?
        val response = StringBuffer()

        while (`in`.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        `in`.close()

        if (con.responseCode == 200){
            return response.toString()
        }
        // 请求没成功 重试
        return request(db, picUrl, times+1)
    }
}

enum class SearchMode(val value: Int) {
    all(999),
    pixiv(5),
    danbooru(9),
    book(18),
    anime(21)
}