package io.genanik.picfinder.Ascii2d

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.uploadAsImage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL


class Ascii2d {

    suspend fun searchPic(picUrl: String, contact: Contact): MessageChain {
        val html = request(picUrl)
        val elements = html.body().getElementsByClass("container")
        val imgUrl: String
        val sources = elements.select(".info-box")[1].select("a")
        val original: String
        try {
            imgUrl = "https://ascii2d.net/" + elements.select(".image-box")[1].select("img")[0].attributes()["src"]
            original = sources[0].attributes()["href"]
        } catch (ignored: IndexOutOfBoundsException) {
            return "获取失败" as MessageChain
        }
        return A2dMessage(imgUrl, original).getMsg(contact)
    }

    private fun request(picUrl: String): Document {
        val ascii2d = "https://ascii2d.net/search/url/"
        val ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36"

        // 造url
        val request = Jsoup.connect(ascii2d + picUrl)
        request.header("User-Agent", ua).followRedirects(true)

        return request.get()
    }

}

class A2dMessage(imgUrl: String, origUrl: String) {
    private val resultMessage = MessageChainBuilder()
    private val previewPic = imgUrl
    private val original = origUrl

    suspend fun getMsg(contact: Contact): MessageChain {
        resultMessage.add("ascii2d:\n")
        val img = URL(previewPic).uploadAsImage(contact)
        resultMessage.add(img)
        resultMessage.add("\n链接: $original")
        return resultMessage.asMessageChain()
    }
}
