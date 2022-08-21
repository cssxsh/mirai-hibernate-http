package xyz.cssxsh.mirai.hibernate.http

import net.mamoe.mirai.api.http.adapter.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*

public object HibernateHttpExtension : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-hibernate-http",
        name = "mirai-hibernate-http",
        version = "1.0.0",
    ) {
        author("cssxsh")

        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", ">= 2.4.0")
        dependsOn("net.mamoe.mirai-api-http", ">= 2.5.0")
    }
) {
    override fun PluginComponentStorage.onLoad() {
        MahAdapterFactory.register("hibernate", HibernateAdapter::class.java)
        System.setProperty(HibernateAdapter.STATIC_KEY, resolveDataFile("web").path)
    }
}