package io.genanik.picfinder.plugins.messageReapeater

import io.genanik.picfinder.abel.AbelPlugins
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder

class MessageRepeater{
    private val msgRepeatController = mutableMapOf<Long, MessagesRepeat>()

    fun trigger(abelPM: AbelPlugins, controller: GroupMessageSubscribersBuilder){
        controller.always {
            if (!abelPM.getStatus("复读", this.group.id)) {
                return@always
            }
            if (msgRepeatController.contains(this.group.id)) {
                // 更新msgRepeat内容
                if (msgRepeatController[this.group.id]!!.update(this.message)) {
                    reply(msgRepeatController[this.group.id]!!.repeatMsg())
                }
            } else {
                // 为本群创建一个msgRepeat
                msgRepeatController[this.group.id] = MessagesRepeat(this.message)
            }
        }
    }
}