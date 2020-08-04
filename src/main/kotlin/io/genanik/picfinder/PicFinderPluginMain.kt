package io.genanik.picfinder

import io.genanik.picfinder.SauceNAO.SauceMessage
import io.genanik.picfinder.SauceNAO.SauceNaoApi
import io.genanik.picfinder.SauceNAO.SearchMode
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.Config
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.*

object PicFinderPluginMain : PluginBase() {

    lateinit var sauceCfg: Config
    private lateinit var sauceNaoApi: SauceNaoApi

    override fun onLoad() {
        logger.info("读取SauceNAO配置文件中...")
        sauceCfg = loadConfig( "SauceNAO.yml")
        sauceCfg.setIfAbsent("APIKey", "")
        if (sauceCfg["APIKey"]!! == ""){
            logger.error("SauceNAO APIKey不存在")
            logger.warning("请在\"plugins/PicFinder/SauceNAO.yml\"中将APIKey所对应的值改为您的APIKey后获得最佳体验")
            logger.warning("或使用管理员指令添加APIKey")
            logger.warning("APIKey获得方式详见 https://saucenao.com/user.php?page=search-api")
        }
        sauceCfg.save()
        regCommands()
    }

    override fun onEnable() {
        super.onEnable()

        sauceNaoApi = SauceNaoApi(sauceCfg.getString("APIKey"), false)
        logger.info("SauceNao Loaded!")

        subscribeGroupMessages {
            atBot {
                val tmp = getAllPicture(message)
                if (tmp.isEmpty()){
                    reply("你啥图都没发啊。。。发个图试试吧")
                }else{
                    // 开始搜图
                    tmp.forEach{ picUrl ->
                        val code = sauceNaoApi.searchPic(SearchMode.all, picUrl)
                        reply(SauceMessage(code).getMsg(group))
                    }
                }
            }
        }
    }

    private fun regCommands() {
        registerCommand {
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
                        sauceNaoApi = SauceNaoApi(it[1], false)
                        logger.info("APIKey已更改为${sauceCfg["APIKey"]}（无需重启机器人）")
                        return@onCommand true
                    }
                    else -> return@onCommand false
                }
            }
        }
    }

    // 将MessageChain中的所有图片的url拿出来
    private suspend fun getAllPicture(rawMessage: MessageChain): ArrayList<String>{
        val result = ArrayList<String>()
        var tmp = MessageChainBuilder()
        rawMessage.forEachContent {
            tmp.add(it)
            val isImage =
                tmp.asMessageChain()
                    .firstIsInstanceOrNull<Image>() != null

            if (isImage){
                // 添加图片url
                result.add((it as Image).queryUrl())
            }
            tmp = MessageChainBuilder()
        }
        return result
    }
}