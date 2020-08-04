package io.genanik.picfinder.plugins.sauceNAO

import io.genanik.picfinder.PicFinderPluginMain
import io.genanik.picfinder.abel.AbelPlugins
import io.genanik.picfinder.utils.getAllPicture
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.Config
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder

class SauceNaoPlugin() {
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
            sauceNaoAPI = SauceNaoApi(sauceCfg.getString("APIKey"), false)
        }
        sauceCfg.save()
        regCommands(env)
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
                        sauceNaoAPI = SauceNaoApi(it[1], false)
                        PicFinderPluginMain.logger.info("APIKey已更改为${sauceCfg["APIKey"]}（无需重启机器人）")
                        return@onCommand true
                    }
                    else -> return@onCommand false
                }
            }
        }
    }

    fun trigger(abelPM: AbelPlugins, controller: GroupMessageSubscribersBuilder){
        controller.atBot {
            val tmp = getAllPicture(message)
            if (tmp.isEmpty()){
                reply("你啥图都没发啊。。。发个图试试吧")
            }else{
                // 开始搜图
                tmp.forEach{ picUrl ->
                    val code = sauceNaoAPI.searchPic(SearchMode.all, picUrl)
                    reply(SauceMessage(code).getMsg(group))
                }
            }
        }
    }
}