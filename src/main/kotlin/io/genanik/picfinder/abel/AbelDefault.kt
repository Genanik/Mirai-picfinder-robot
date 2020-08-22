package io.genanik.picfinder.abel

import io.genanik.picfinder.PicFinderPluginMain
import io.genanik.picfinder.plugins.picFind.sauceNao.SauceNaoApi
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.subscribeTempMessages

fun regAbelDefault(controller: PicFinderPluginMain, aPM: AbelPlugins){
    // Abel指令绑定
    controller.subscribeGroupMessages {
        for (i in aPM.adminGetAllCommands()) {
            case(i) {
                reply(aPM.adminTransferCommand(i)(this.group.id))
            }
        }
        for (i in aPM.getAllCommands()) {
            case(i) {
                reply(aPM.transferCommand(i)(this.group.id))
            }
        }
    }
    // Abel功能绑定
    controller.subscribeGroupMessages {
        // 用户组
        for (i in aPM.getAllFunctions()) {
            // 操作
            case("关闭$i") {
                if (!aPM.adminGetStatus(i, this.group.id)) {
                    if (aPM.getStatus(i, this.group.id)) {
                        aPM.disableFunc(i, this.group.id)
                        reply("不出意外的话。。咱关掉${i}了")
                    } else {
                        reply(
                            "这个功能已经被关掉了呢_(:з」∠)_不用再关一次了\n" +
                                    "推荐使用\"功能名称+打开了嘛\"获取功能运行状态"
                        )
                    }
                }
            }
            case("开启$i") {
                if (!aPM.adminGetStatus(i, this.group.id)) {
                    if (!aPM.getStatus(i, this.group.id)) {
                        aPM.enableFunc(i, this.group.id)
                        reply("不出意外的话。。咱打开${i}了")
                    } else {
                        reply(
                            "(｡･ω･)ﾉﾞ${i}\n这个已经打开了哦，不用再开一次啦\n" +
                                    "推荐使用\"功能名称+打开了嘛\"获取功能运行状态"
                        )
                    }
                }
            }

            case("切换$i") {
                // 有没有被管理员禁用
                if (!aPM.adminGetStatus(i, this.group.id)) {
                    return@case
                }
                if (!aPM.getStatus(i, this.group.id)) {
                    aPM.enableFunc(i, this.group.id)
                    reply("不出意外的话。。咱打开${i}了")
                } else {
                    aPM.disableFunc(i, this.group.id)
                    reply("不出意外的话。。咱关掉${i}了")
                }
            }

            // 查询
            case("${i}打开了嘛") {
                var status = aPM.getStatus(i, this.group.id)
                if (i == "翻译") {
                    status = !status
                }
                if (status) {
                    reply("开啦(′▽`〃)")
                } else {
                    reply("没有ヽ(･ω･｡)ﾉ ")
                }
            }
        }

        // 管理员
        for (i in aPM.adminGetAllFunctions()) {
            // 操作
            case("关闭所有$i") {
                if (aPM.isAdmin(this.sender.id)) {

                    aPM.disableFuncInAllGroup(i)
                    reply("所有群已关闭功能: $i")
                }
            }
            case("禁用$i") {
                if (aPM.isAdmin(this.sender.id)) {

                    aPM.adminDisableFunc(i, this.group.id)
                    aPM.disableFunc(i, this.group.id)
                    reply("群: ${this.group.id}\n已禁用功能: $i")
                }
            }
            case("启用$i") {
                if (aPM.isAdmin(this.sender.id)) {

                    aPM.adminEnableFunc(i, this.group.id)
                    aPM.enableFunc(i, this.group.id)
                    reply("群: ${this.group.id}\n已启用功能: $i")
                }
            }
        }
    }
}