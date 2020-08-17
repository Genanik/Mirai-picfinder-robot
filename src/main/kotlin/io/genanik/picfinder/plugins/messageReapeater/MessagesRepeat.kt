package io.genanik.picfinder.plugins.messageReapeater

import io.genanik.picfinder.utils.isEqualWithRemoveMsgSource
import io.genanik.picfinder.utils.removeMsgSource
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*

/**
 * 判断出现两条相同内容后 将内容镜像并返回镜像的MessageChain
 * 构造时需要传入GroupMessageEvent
 */
class MessagesRepeat(message: MessageChain) {
    private var lastMessage = message.removeMsgSource() // 保证没有MsgSource
    private var times = 1
    private var needTimes = 2
    private var repeatTimes = 0

    // 更新缓存并返回是否复读
    fun update(newMessage: MessageChain): Boolean{

        val isSame = newMessage.isEqualWithRemoveMsgSource(lastMessage)

        if (isSame) { // 当前消息与上一条消息内容相同
            times++
        }

        val result = times == needTimes
        lastMessage = newMessage.removeMsgSource() // 保证没有MsgSource

        if (result){
            repeatTimes++
            needTimes += 3
            times = 1
        }

        if (!isSame){
            repeatTimes = 0
            needTimes = 2
            times = 1
        }

        return result
    }

    fun repeatMsg(oldMsgChain: MessageChain): MessageChain {
        return oldMsgChain.removeMsgSource()
    }
}