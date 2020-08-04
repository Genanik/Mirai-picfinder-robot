package io.genanik.picfinder.plugins.bilibiliMsg

import com.google.gson.Gson
import io.genanik.picfinder.abel.AbelPlugins
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.uploadAsImage
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class BilibiliMsg {

    fun trigger(abelPM: AbelPlugins, controller: GroupMessageSubscribersBuilder){
        controller.always {
            // 是否开启
            if (!abelPM.getStatus("bilibili", this.group.id)) {
                return@always
            }
            // bilibili
            val id = getAvBvFromMsg(message) ?: return@always
            reply(VideoInfo(id).beautyMsg(group))
        }
    }

    class VideoInfo(id: String) {
        private var param: String = id
        private var data: BiliData

        init {
            val requestBody = get("https://api.bilibili.com/x/web-interface/view?$param")
            data = Gson().fromJson(requestBody, BiliResponse::class.javaObjectType).data
        }

        data class BiliResponse(
            val data: BiliData
        )

        data class BiliData(
            val bvid: String,
            val aid: Int,
            val pic: String,
            val title: String,
            val owner: BilOwner,
            val stat: BiliStat
        )

        data class BilOwner(
            val name: String
        )

        data class BiliStat(
            val view: Int,      // 播放
            val danmaku: Int,   // 弹幕
            val favorite: Int,  // 收藏
            val like: Int       // 点赞
        )

        suspend fun beautyMsg(contact: Contact): MessageChain {
            val result = MessageChainBuilder()
            result.add( URL(data.pic).uploadAsImage(contact) + "\n" )
            result.add( data.title + "\n")
            result.add( "UP: ${data.owner.name}\n" )
            result.add( "${toHumanNum(data.stat.view)}播放 ${toHumanNum(data.stat.danmaku)}弹幕\n" )
            result.add( "https://www.bilibili.com/video/av${data.aid}" )
            return result.asMessageChain()
        }

        private fun toHumanNum(num: Int): String {
            return if (num >= 10000) {
                val numStr = num.toDouble()
                String.format("%.2f",(numStr/10000)) + "万"
            } else {
                num.toString()
            }
        }
    }

    class SearchVideo(keyword: String) {
        private var param: String = "keyworld=$keyword"

        data class BiliRequest(
            val data: BiliData
        )

        data class BiliData(
            val result: List<BliResult>
        )

        data class BliResult(
            val result_type: String,
            val data: List<ResultData>
        )

        data class ResultData(
            val id: Int // aid
        )

        fun getAid(): Int {
            // 返回第一个内容的aid
            val requestBody = get("https://api.bilibili.com/x/web-interface/search/all/v2?$param")
            val bean = Gson().fromJson(requestBody, BiliRequest::class.javaObjectType)
            return bean.data.result[0].data[0].id
        }

    }

    fun getAvBvFromNormalLink(link: String): String{
        val rules = Regex("""bilibili\.com\/video\/(?:[Aa][Vv]([0-9]+)|([Bb][Vv][0-9a-zA-Z]+))""")
        val search = rules.find(link) ?: throw Exception("[Bilibili] 获取视频ID错误")
        return if (search.groupValues[1] == ""){
            "bvid=${search.groupValues[2]}"
        }else{
            "aid=${search.groupValues[1]}"
        }
    }

    fun shortToLongLink(shortLink: String): String {
        var conn: HttpURLConnection? = null
        try {
            conn = URL(shortLink).openConnection() as HttpURLConnection
        } catch (e: IOException) {
            e.printStackTrace()
        }
        conn!!.instanceFollowRedirects = false
        conn!!.connectTimeout = 5000
        val url: String = conn.getHeaderField("Location")
        conn.disconnect()
        return url
    }

    private fun getAvBvFromMsg(msg: MessageChain): String? {
        val plainText: PlainText = msg.firstIsInstanceOrNull() ?: return null
        var maybeLink = plainText.content

        // normalLink
        var rule = Regex("""bilibili\.com\/video\/(?:[Aa][Vv]([0-9]+)|([Bb][Vv][0-9a-zA-Z]+))""")
        if (rule.containsMatchIn(maybeLink)){
            return getAvBvFromNormalLink(maybeLink)
        }

        // shortLink
        rule = Regex("""(b23|acg)\.tv\/[0-9a-zA-Z]+""")
        if (rule.containsMatchIn(maybeLink)){
            maybeLink = shortToLongLink(maybeLink)
            return getAvBvFromNormalLink(maybeLink)
        }

        // app
        val app = getApp(msg) ?: return null
        return BiliLightApp(app).getId()
    }

    private fun getApp(msg: MessageChain): LightApp? {
        return msg.firstIsInstanceOrNull() ?: return null
    }

    class BiliLightApp(app: LightApp){

        private val content = app.content
        private var isBiliLightApp = false
        private lateinit var biliJsonBean: BiliLightApp

        init {
            val prompt = Gson().fromJson(content, UniversalLightApp::class.javaObjectType).prompt
            if (prompt.contains("哔哩哔哩")){
                isBiliLightApp = true
                biliJsonBean = Gson().fromJson(content, BiliLightApp::class.javaObjectType)
            }
        }

        data class UniversalLightApp (
            val prompt: String
        )

        data class BiliLightApp (
            val meta: BiliMeta,
            val prompt: String
        )

        data class BiliMeta (
            val detail_1: BiliDetail
        )

        data class BiliDetail (
            val desc: String, // 标题
            val qqdocurl: String? // 可能存在的短链接
        )

        private fun getUrlOfNull(): String? {
            if (!isBiliLightApp){
                return null
            }
            return biliJsonBean.meta.detail_1.qqdocurl
        }

        fun getId(): String {
            var url = getUrlOfNull()
            return if (url != null){
                url = BilibiliMsg().shortToLongLink(url)
                return BilibiliMsg().getAvBvFromNormalLink(url)
            }else{
                // 搜索
                "aid=av" + SearchVideo(biliJsonBean.meta.detail_1.desc).getAid()
            }
        }
    }

}