package io.genanik.picfinder.abel

import io.genanik.picfinder.PicFinderPluginMain

fun abelLoad(controller: PicFinderPluginMain, aPM: AbelPlugins){
    // 注册mirai指令 - 添加管理员
    controller.cmdRegister.addChild("AddAdmin") { args ->
        val ramAdmins = aPM.getAllAdmin() as MutableList

        when(args.size){
            1 -> controller.logger.error("参数过少")
            2 -> {
                val newAdminID = args[1].toLong()

                if (controller.bot.exist("Admins")){
                    // 将配置文件中的管理员加入ramAdmins
                    controller.bot.getLongList("Admins").forEach {
                        if(!ramAdmins.contains(it)){
                            ramAdmins.add(it)
                        }
                    }
                }

                // 向配置文件中添加管理员
                ramAdmins.add(newAdminID)
                controller.bot["Admins"] = ramAdmins
                controller.bot.save()

                // 向实例中添加管理员
                aPM.addAdmin(newAdminID)

                controller.logger.info("已成功添加管理员$newAdminID")
                return@addChild true
            }
            3 -> controller.logger.error("参数过多")
        }
        return@addChild false
    }
}