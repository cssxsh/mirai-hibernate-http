package xyz.cssxsh.mirai.hibernate.http

import net.mamoe.mirai.api.http.adapter.*
import net.mamoe.mirai.api.http.setting.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*

public object HibernateHttpExtension : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-hibernate-http",
        name = "mirai-hibernate-http",
        version = "1.1.0",
    ) {
        author("cssxsh")

        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", ">= 2.6.0")
        dependsOn("net.mamoe.mirai-api-http", ">= 2.7.0")
    }
) {
    override fun PluginComponentStorage.onLoad() {
        MahAdapterFactory.register("hibernate", HibernateAdapter::class.java)
        System.setProperty(HibernateAdapter.STATIC_KEY, resolveDataFile("web").path)
    }

    override fun onEnable() {
        if (MainSetting.persistenceFactory != "hibernate") {
            logger.warning { "如果要使用 hibernate 作为 mirai-api-http persistence service, 请修改 persistenceFactory 为 hibernate" }
        }
    }
}