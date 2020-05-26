package io.genanik.picfinder.SauceNAO

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

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

    fun searchPic(searchMode: SearchMode, picUrl: String): String{
        return(request(searchMode.value, picUrl))
    }

    private fun request(db: Int, picUrl: String): String {
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
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url(fullURL.toString())
            .method("GET", null)
            .build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful){
            return response.body!!.string()
        }
        // 请求没成功 重试
        return request(db, picUrl)
    }
}

enum class SearchMode(val value: Int) {
    all(999),
    pixiv(5),
    danbooru(9),
    book(18),
    anime(21)
}