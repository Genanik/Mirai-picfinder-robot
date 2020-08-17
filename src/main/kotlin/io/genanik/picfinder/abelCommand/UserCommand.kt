package io.genanik.picfinder.abelCommand

import io.genanik.picfinder.plugins.time.Time
import io.genanik.picfinder.abel.AbelPlugins
import net.mamoe.mirai.message.data.MessageChainBuilder

fun AbelPlugins.regHelp(){
    this.regCommand("/help", "展示帮助信息") {
        val result = MessageChainBuilder()
        result.add("你好你好\n\n")
        for (i in this.getAllCommands()) {
            result.add("* $i  ${this.getCommandDescription()[i]}\n")
        }
        result.add("\n咱介绍完指令了，然后\n该介绍功能了\n\n")
        for (i in this.getAllFunctions()) {
            result.add("* $i  ${this.getFunctionDescription()[i]}\n")
        }
        result.add(
            "\n其他功能：\n" +
                    "* \"功能名称+打开了嘛\" 获取功能运行状态\n"
        )
        result.add("* /adminHelp 获取管理员帮助信息")
        return@regCommand result.asMessageChain()
    }
}

fun AbelPlugins.regGetTime(timeController: Time){
    this.regCommand("报时", "发送当前时间") {
        val result = MessageChainBuilder()
        result.add(timeController.getNow())
        return@regCommand result.asMessageChain()
    }
}

fun AbelPlugins.regFunctions(){
    this.regFunction("复读", "同一条消息出现两次后，Abel机器人自动跟读")
    this.regFunction("搜图", "@机器人并带上一张图片，自动搜索图片来源，加上\"a2d\"即可使用ascii2d搜图引擎")
    this.regFunction("bilibili", "发现b站视频链接自动生成预览信息")
}