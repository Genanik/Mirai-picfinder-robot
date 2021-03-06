package io.genanik.picfinder

import io.genanik.picfinder.abel.abelLoad
import io.genanik.picfinder.abel.AbelPlugins
import io.genanik.picfinder.abel.regAbelDefault
import io.genanik.picfinder.abelCommand.*
import io.genanik.picfinder.bot.CommandRegister
import io.genanik.picfinder.plugins.autoAccept.AutoAccept
import io.genanik.picfinder.plugins.bilibiliMsg.BilibiliMsg
import io.genanik.picfinder.plugins.time.Time
import io.genanik.picfinder.plugins.messageReapeater.MessageRepeater
import io.genanik.picfinder.plugins.picFind.PicFind
import net.mamoe.mirai.console.plugins.Config
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.event.subscribeGroupMessages

object PicFinderPluginMain : PluginBase() {

    lateinit var bot: Config
    lateinit var cmdRegister: CommandRegister

    // 为每个Abel插件创建对象
    private val picFind = PicFind()
    private val autoAccept = AutoAccept()
    private val msgRepeaterController = MessageRepeater()
    private val timeController = Time()
    private val bilibiliPlugin = BilibiliMsg()

    private var abelPluginController = AbelPlugins(logger)

    override fun onLoad() {
        super.onLoad()

        cmdRegister = CommandRegister(
            this,
            "PicFinder",
            listOf("picfinder", "Picfinder", "picFinder"),
            "PicFinder 插件管理",
            "PicFinder\n" +
                    "\t[/PicFinder APIKey ...]\t用于设置SauceNaoAPIKey\n" +
                    "\t[/PicFinder AddAdmin ...]\t用于添加管理员\n" +
                    "也可以在配置文件中设置SauceNaoAPIKey")

        logger.info("读取Bot配置文件中...")
        bot = loadConfig("Bot.yml")
        abelLoad(this, abelPluginController)

        // Abel插件onLoad
        picFind.onLoad(this)
        autoAccept.onLoad(this)

        cmdRegister.reg()

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