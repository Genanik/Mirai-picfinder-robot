package io.genanik.picfinder.plugins.picFind

import io.genanik.picfinder.PicFinderPluginMain
import io.genanik.picfinder.abel.AbelPlugins
import io.genanik.picfinder.plugins.picFind.a2d.Ascii2d
import io.genanik.picfinder.plugins.picFind.sauceNao.SauceMessage
import io.genanik.picfinder.plugins.picFind.sauceNao.SauceNaoApi
import io.genanik.picfinder.plugins.picFind.sauceNao.SearchMode
import io.genanik.picfinder.utils.getAllPicture
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.Config
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.getOrFail

class PicFind {

    private var a2dAPI = Ascii2d()
    private var sauceNaoAPI = SauceNaoApi("", false)
    private lateinit var sauceCfg: Config

    fun onLoad(env: PicFinderPluginMain){
        env.logger.info("读取SauceNAO配置文件中...")
        sauceCfg = env.loadConfig("SauceNAO.yml")
        sauceCfg.setIfAbsent("APIKey", "")
        if (sauceCfg["APIKey"]!! == ""){
            env.logger.error("SauceNAO APIKey不存在")
            env.logger.warning("请在\"plugins/PicFinder/SauceNAO.yml\"中将APIKey所对应的值改为您的APIKey后获得最佳体验")
            env.logger.warning("或使用管理员指令添加APIKey")
            env.logger.warning("APIKey获得方式详见 https://saucenao.com/user.php?page=search-api")
        }else{
            sauceNaoAPI =
                SauceNaoApi(
                    sauceCfg.getString("APIKey"),
                    false
                )
        }
        sauceCfg.save()
        regCommands(env)
        env.logger.info("Ascii2d已加载")
    }

    private fun regCommands(env: PicFinderPluginMain) {
        env.registerCommand {
            name = "PicFinder"
            alias = listOf("picfinder", "picFinder")
            description = "PicFinder 插件管理"
            usage = "PicFinder" +
                    "\n[/PicFinder APIKey ...]    用于设置APIKey" +
                    "\n也可以在配置文件中设置APIKey"
            onCommand {
                if (it.size < 2){
                    return@onCommand false
                }
                when (it[0]) {
                    "APIKey" -> {
                        sauceCfg["APIKey"] = it[1]
                        sauceCfg.save()
                        sauceNaoAPI =
                            SauceNaoApi(it[1], false)
                        PicFinderPluginMain.logger.info("APIKey已更改为${sauceCfg["APIKey"]}（无需重启机器人）")
                        return@onCommand true
                    }
                    else -> return@onCommand false
                }
            }
        }
    }

    /**
     * 直接@+图片使用sauceNao
     * @+图片+"a2d"使用a2d
     */
    fun trigger(abelPM: AbelPlugins, controller: GroupMessageSubscribersBuilder){
        controller.atBot {
            var useA2d = isUseA2d(message)
            val tmp = getAllPicture(message)
            if (tmp.isEmpty()){
                reply("你啥图都没发_(:з」∠)_\n@我的时候发个图试试吧")
            }else{
                // 开始搜图
                tmp.forEach{ picUrl ->
                    if (useA2d){
                        reply( a2dAPI.searchPic(picUrl, group) )
                    }else{
                        val code = sauceNaoAPI.searchPic(SearchMode.all, picUrl)
                        reply(SauceMessage(code).getMsg(group))
                    }
                }
            }
        }
    }

    private fun isUseA2d(msg: MessageChain): Boolean = try {
        val text = msg.getOrFail(PlainText).content
        text.indexOf("a2d") != -1
    }catch (_: NoSuchElementException){
        false
    }
}