package xyz.cssxsh.mirai.hibernate.http

import kotlinx.serialization.*

@Serializable
public data class HibernateAdapterResult<T>(
    @SerialName("code")
    val code: Int,
    @SerialName("msg")
    val message: String,
    @SerialName("data")
    val data: T? = null
)