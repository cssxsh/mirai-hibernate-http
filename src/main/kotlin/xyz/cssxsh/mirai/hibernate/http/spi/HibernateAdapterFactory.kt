package xyz.cssxsh.mirai.hibernate.http.spi

import net.mamoe.mirai.api.http.spi.adapter.*
import xyz.cssxsh.mirai.hibernate.http.*

public class HibernateAdapterFactory : MahAdapterServiceFactory {
    override fun getAdapterClass(): Class<out HibernateAdapter> = HibernateAdapter::class.java

    override fun getAdapterName(): String = "hibernate"
}