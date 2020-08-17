package io.genanik.picfinder.plugins.ascii2d

import io.genanik.picfinder.PicFinderPluginMain
import io.genanik.picfinder.abel.AbelPlugins
import io.genanik.picfinder.utils.getAllPicture
import net.mamoe.mirai.event.GroupMessageSubscribersBuilder

class A2dPlugin {
    private var a2dAPI = Ascii2d()

    fun onLoad(env: PicFinderPluginMain){
        env.logger.info("A2d已加载")
    }

    fun trigger(abelPM: AbelPlugins, controller: GroupMessageSubscribersBuilder){
        controller.atBot {
            val tmp = getAllPicture(message)
            if (tmp.isEmpty()){
                reply("你啥图都没发啊。。。发个图试试吧")
            }else{
                // 开始搜图
                tmp.forEach{ picUrl ->
                    reply( a2dAPI.searchPic(picUrl, group) )
                }
            }
        }
    }
}