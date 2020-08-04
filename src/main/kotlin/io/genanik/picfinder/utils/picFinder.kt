package io.genanik.picfinder.utils

import net.mamoe.mirai.message.data.*

// 将MessageChain中的所有图片的url拿出来
suspend fun getAllPicture(rawMessage: MessageChain): ArrayList<String>{
    val result = ArrayList<String>()
    var tmp = MessageChainBuilder()
    rawMessage.forEachContent {
        tmp.add(it)
        val isImage =
            tmp.asMessageChain()
                .firstIsInstanceOrNull<Image>() != null

        if (isImage){
            // 添加图片url
            result.add((it as Image).queryUrl())
        }
        tmp = MessageChainBuilder()
    }
    return result
}