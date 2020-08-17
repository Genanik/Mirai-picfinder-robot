package io.genanik.picfinder

import io.genanik.picfinder.abel.AbelPlugins
import io.genanik.picfinder.abel.regAbelDefault
import io.genanik.picfinder.abelCommand.*
import io.genanik.picfinder.plugins.bilibiliMsg.BilibiliMsg
import io.genanik.picfinder.plugins.time.Time
import io.genanik.picfinder.plugins.messageReapeater.MessageRepeater
import io.genanik.picfinder.plugins.picFind.PicFind
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.event.subscribeGroupMessages

object PicFinderPluginMain : PluginBase() {


    // 为每个Abel插件创建对象
    private val picFind = PicFind()
    private val msgRepeaterController = MessageRepeater()
    private val timeController = Time()
    private val bilibiliPlugin = BilibiliMsg()

    private var abelPluginController = AbelPlugins(logger)

    override fun onLoad() {
        super.onLoad()

        picFind.onLoad(this)

        // 注册Abel管理员指令
        logger.info("注册Abel管理员指令")
        abelPluginController.regDumpvars()
        abelPluginController.regAdminHelp()

        // 注册Abel指令
        logger.info("注册Abel指令")
        abelPluginController.regHelp()
        abelPluginController.regGetTime(timeController)

        // 注册Abel管理员功能
        logger.info("注册Abel管理员功能")
        abelPluginController.regAdminFunctions()

        // 注册Abel功能
        logger.info("注册Abel功能")
        abelPluginController.regFunctions()
    }

    override fun onEnable() {
        super.onEnable()

        /**
         * 实现功能Abel订阅
         */
        subscribeGroupMessages {
            // 搜图
            picFind.trigger(abelPluginController, this)

            // 复读
            msgRepeaterController.trigger(abelPluginController, this)

            // bilibili
            bilibiliPlugin.trigger(abelPluginController, this)

        }

        // 注册Abel内容
        regAbelDefault(this, abelPluginController)

    }

}