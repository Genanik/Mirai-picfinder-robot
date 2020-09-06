package io.genanik.picfinder.plugins.picFind.sauceNao

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.uploadAsImage
import java.net.URL

class SauceMessage(SauceNaoJSON: SauceNaoData) {
    private val resultMessage = MessageChainBuilder()
    private val resultHeader = SauceNaoJSON.results[0].header
    private val resultData = SauceNaoJSON.results[0].data
    private val thumbnailURL = resultHeader.thumbnail

    suspend fun getMsg(contact: Contact): MessageChain {
        resultMessage.add("SauceNAO:\n")
        resultMessage.add("相似度: ${resultHeader.similarity}%\n")
        val img = URL(thumbnailURL).uploadAsImage(contact)
        resultMessage.add(img)

        if (resultData.title != null){
            resultMessage.add("\n标题: ${resultData.title}")
        }
        if (resultData.author_name != null){
            resultMessage.add("\n作者: ${resultData.author_name}")
        }

        resultData.ext_urls.forEach{ extUrl ->
            resultMessage.add("\n${extUrl}")
        }
        return resultMessage.asMessageChain()
    }

}

