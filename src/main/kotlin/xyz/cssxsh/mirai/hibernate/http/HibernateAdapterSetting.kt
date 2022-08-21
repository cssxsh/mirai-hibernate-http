package xyz.cssxsh.mirai.hibernate.http

import kotlinx.serialization.*

/**
 * @param host 监听 url
 * @param port 监听端口
 */
@Serializable
public data class HibernateAdapterSetting(
    val host: String = "localhost",
    val port: Int = 8081,
)