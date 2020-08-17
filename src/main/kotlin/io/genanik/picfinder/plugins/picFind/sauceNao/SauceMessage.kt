package io.genanik.picfinder.plugins.picFind.sauceNao

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.uploadAsImage
import org.json.JSONObject
import java.net.URL

class SauceMessage(SauceNaoJSON: String) {
    private val resultMessage = MessageChainBuilder()
    private val jsonData = JSONObject(SauceNaoJSON)
    private val resultHeader = jsonData
        .getJSONArray("results")
        .getJSONObject(0)
        .getJSONObject("header")
    private val resultData = jsonData
        .getJSONArray("results")
        .getJSONObject(0)
        .getJSONObject("data")
    private val thumbnailURL = resultHeader["thumbnail"].toString()

    suspend fun getMsg(contact: Contact): MessageChain {
        resultMessage.add("SauceNAO:\n")
        resultMessage.add("相似度: ${resultHeader["similarity"]}%\n")
        val img = URL(thumbnailURL).uploadAsImage(contact)
        resultMessage.add(img)
        try{
            resultMessage.add("\n标题: ${resultData["title"]}")
            resultMessage.add("\n作者: ${resultData["author_name"]}")
            // 加不进去就不加了
        }catch (e:Exception){}
        resultData.getJSONArray("ext_urls").forEach{ extUrl ->
            resultMessage.add("\n${extUrl}")
        }
        return resultMessage.asMessageChain()
    }

}

