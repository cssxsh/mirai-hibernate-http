package xyz.cssxsh.mirai.hibernate.http

import kotlinx.serialization.*

@Serializable
public data class HibernateAdapterSetting(
    /**
     * 监听 url
     */
    val host: String = "localhost",
    /**
     * 监听端口
     */
    val port: Int = 8081,
)