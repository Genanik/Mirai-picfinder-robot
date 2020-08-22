package io.genanik.picfinder.bot

import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.utils.MiraiLogger

class CommandRegister(
    targetClass: PluginBase,
    name: String,
    alias: List<String>,
    description: String,
    usage: String
) {

    private var cmdName = name
    private var cmdAlias = alias
    private var cmdDescription = description
    private var cmdUsage = usage
    private val target = targetClass

    private val childNames = mutableListOf<String>()
    private val childFunction = mutableMapOf<String, (List<String>) -> Boolean >()

    fun addChild(childName: String, func:(List<String>) -> Boolean){
        childNames.add(childName)
        childFunction[childName] = func
    }

    fun reg(){
        target.registerCommand {
            name = cmdName
            alias = cmdAlias
            description = cmdDescription
            usage = cmdUsage

            onCommand {
                // 在注册的子指令里匹配
                childNames.forEach { targetFunc ->
                    if (it.isEmpty() || it[0] == "help"){
                        return@onCommand false
                    }

                    if (it[0] == targetFunc) {
                        return@onCommand childFunction[targetFunc]!!(it)
                    }
                }

                target.logger.error("输入了一个不存在的指令")
                // 没有在注册的子指令里找到收到的指令
                return@onCommand false
            }
        }
    }
}