package io.genanik.picfinder.plugins.autoAccept

import io.genanik.picfinder.PicFinderPluginMain
import io.genanik.picfinder.abel.AbelPlugins
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent

class AutoAccept {
    
    fun onLoad(env: PicFinderPluginMain){
        env.bot.setIfAbsent("TempMsg", false)
        env.bot.setIfAbsent("FriendMsg", false)
        env.bot.setIfAbsent("NewFriendRequest", false)
        env.bot.setIfAbsent("InvitedJoinGroup", false)

        if (env.bot["TempMsg"]!! == false){
            env.logger.info("已关闭自动回复临时消息")
        }else{
            tempMsg(env)
            env.logger.info("已开启自动回复临时消息")
        }

        if (env.bot["FriendMsg"]!! == false){
            env.logger.info("已关闭自动回复好友消息")
        }else{
            friendMsg(env)
            env.logger.info("已开启自动回复好友消息")
        }

        if (env.bot["NewFriendRequest"]!! == false){
            env.logger.info("已关闭自动同意好友申请")
        }else{
            newFriendRequest(env)
            env.logger.info("已开启自动同意好友申请")
        }

        if (env.bot["InvitedJoinGroup"]!! == false){
            env.logger.info("已关闭自动同意邀请至群")
        }else{
            invitedJoinGroup(env)
            env.logger.info("已开启自动同意邀请至群")
        }

        env.bot.save()
    }

    fun trigger(abelPM: AbelPlugins, controller: GroupMessageSubscribersBuilder){

    }

    private fun tempMsg(controller: PicFinderPluginMain) {
        // 临时消息
        controller.subscribeTempMessages {
            always {
                reply("emm抱歉。。暂不支持临时会话，但是可以通过邀请至群使用（加好友自动通过验证），群内/help查看帮助")
            }
        }
    }

    private fun friendMsg(controller: PicFinderPluginMain) {
        // 好友消息
        controller.subscribeFriendMessages {
            always {
                reply("emm抱歉。。暂不支持私聊，但是可以通过邀请至群使用（加好友自动通过验证），群内/help查看帮助")
            }
        }
    }

    private fun newFriendRequest(controller: PicFinderPluginMain) {
        // 好友申请
        controller.subscribeAlways<NewFriendRequestEvent> {
            accept()
        }
    }
    private fun invitedJoinGroup(controller: PicFinderPluginMain) {
        // 邀请至群申请
        controller.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            accept()
        }
    }
}





