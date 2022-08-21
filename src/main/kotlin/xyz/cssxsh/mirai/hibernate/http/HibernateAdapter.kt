package xyz.cssxsh.mirai.hibernate.http

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.api.http.adapter.*
import net.mamoe.mirai.api.http.context.session.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.hibernate.*
import xyz.cssxsh.mirai.hibernate.*
import xyz.cssxsh.mirai.hibernate.entry.*
import java.io.File

public class HibernateAdapter : MahKtorAdapter("hibernate") {

    internal val setting: HibernateAdapterSetting by lazy {
        getSetting() ?: HibernateAdapterSetting(port = 8081)
    }

    @Suppress("INVISIBLE_MEMBER")
    private val factory: org.hibernate.SessionFactory get() = xyz.cssxsh.mirai.hibernate.factory

    override fun onEnable() {
        log.info(">>> [hibernate message recorder] is listening at http://${host}:${port}")
    }

    override suspend fun onReceiveBotEvent(event: BotEvent, session: Session) {
        // ignore
    }

    override fun MahKtorAdapterInitBuilder.initKtorAdapter() {
        host = setting.host
        port = setting.port
        module {
            routing {
                static("/") {
                    staticRootFolder = File(System.getProperty(STATIC_KEY, "./web"))
                    default("index.html")
                    files(".")
                }
                // face
                get("/face/random") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val record = FaceRecord.random()
                            success(data = record)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = "random failure, face record may be empty.")
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                put("/face/disable") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val md5 = call.parameters["md5"] ?: throw NoSuchElementException("need parameter md5")
                            val records = FaceRecord.disable(md5 = md5)
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: IllegalStateException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                get("/face/tag") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val md5 = call.parameters["md5"] ?: throw NoSuchElementException("need parameter md5")
                            val records = FaceTagRecord.get(md5 = md5)
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                put("/face/tag") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val md5 = call.parameters["md5"] ?: throw NoSuchElementException("need parameter md5")
                            val tag = call.parameters["tag"] ?: throw NoSuchElementException("need parameter tag")
                            val records = FaceTagRecord.set(md5 = md5, tag = tag)
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                delete("/face/tag") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val md5 = call.parameters["md5"] ?: throw NoSuchElementException("need parameter md5")
                            val tag = call.parameters["tag"] ?: throw NoSuchElementException("need parameter tag")
                            val records = FaceTagRecord.remove(md5 = md5, tag = tag)
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                // message
                get("/message/bot") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter bot")
                            val start = call.parameters["start"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter start")
                            val end = call.parameters["end"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter end")
                            val records = factory.fromSession { session ->
                                session.withCriteria<MessageRecord> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    criteria.select(record)
                                        .where(
                                            between(record.get("time"), start, end),
                                            equal(record.get<Long>("bot"), bot)
                                        )
                                        .orderBy(desc(record.get<Int>("time")))
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                get("/message/group") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter bot")
                            val group = call.parameters["group"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter group")
                            val start = call.parameters["start"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter start")
                            val end = call.parameters["end"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter end")
                            val records = factory.fromSession { session ->
                                session.withCriteria<MessageRecord> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    criteria.select(record)
                                        .where(
                                            equal(record.get<Int>("bot"), bot),
                                            between(record.get("time"), start, end),
                                            equal(record.get<MessageSourceKind>("kind"), MessageSourceKind.GROUP),
                                            equal(record.get<Long>("targetId"), group)
                                        )
                                        .orderBy(desc(record.get<Int>("time")))
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                get("/message/friend") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter bot")
                            val friend = call.parameters["friend"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter friend")
                            val start = call.parameters["start"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter start")
                            val end = call.parameters["end"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter end")
                            val records = factory.fromSession { session ->
                                session.withCriteria<MessageRecord> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    criteria.select(record)
                                        .where(
                                            equal(record.get<Int>("bot"), bot),
                                            between(record.get("time"), start, end),
                                            equal(record.get<MessageSourceKind>("kind"), MessageSourceKind.FRIEND),
                                            or(
                                                equal(record.get<Long>("fromId"), friend),
                                                equal(record.get<Long>("targetId"), friend)
                                            )
                                        )
                                        .orderBy(desc(record.get<Int>("time")))
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                get("/message/member") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter bot")
                            val group = call.parameters["group"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter group")
                            val member = call.parameters["member"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter member")
                            val start = call.parameters["start"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter start")
                            val end = call.parameters["end"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter end")
                            val records = factory.fromSession { session ->
                                session.withCriteria<MessageRecord> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    criteria.select(record)
                                        .where(
                                            equal(record.get<Int>("bot"), bot),
                                            between(record.get("time"), start, end),
                                            equal(record.get<MessageSourceKind>("kind"), MessageSourceKind.GROUP),
                                            equal(record.get<Long>("fromId"), member),
                                            equal(record.get<Long>("targetId"), group)
                                        )
                                        .orderBy(desc(record.get<Int>("time")))
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                get("/message/stranger") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter bot")
                            val stranger = call.parameters["stranger"]?.toLongOrNull()
                                ?: throw NoSuchElementException("need parameter stranger")
                            val start = call.parameters["start"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter start")
                            val end = call.parameters["end"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter end")
                            val records = factory.fromSession { session ->
                                session.withCriteria<MessageRecord> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    criteria.select(record)
                                        .where(
                                            equal(record.get<Int>("bot"), bot),
                                            between(record.get("time"), start, end),
                                            equal(record.get<MessageSourceKind>("kind"), MessageSourceKind.STRANGER),
                                            or(
                                                equal(record.get<Long>("fromId"), stranger),
                                                equal(record.get<Long>("targetId"), stranger)
                                            )
                                        )
                                        .orderBy(desc(record.get<Int>("time")))
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                get("/message/kind") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val kind = call.parameters["kind"]
                                ?: throw NoSuchElementException("need parameter kind")
                            val start = call.parameters["start"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter start")
                            val end = call.parameters["end"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter end")
                            val records = factory.fromSession { session ->
                                session.withCriteria<MessageRecord> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    criteria.select(record)
                                        .where(
                                            between(record.get("time"), start, end),
                                            equal(
                                                record.get<MessageSourceKind>("kind"),
                                                MessageSourceKind.valueOf(kind)
                                            )
                                        )
                                        .orderBy(desc(record.get<Int>("time")))
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                // archive
                get("/archive/bot") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val records = factory.fromSession { session ->
                                session.withCriteria<Long> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    criteria.select(record.get("bot"))
                                        .distinct(true)
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                get("/archive/group") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                            val records = factory.fromSession { session ->
                                session.withCriteria<Long> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    criteria.select(record.get("targetId"))
                                        .where(
                                            *if (bot != null) {
                                                arrayOf(
                                                    equal(record.get<Long>("bot"), bot),
                                                    equal(
                                                        record.get<MessageSourceKind>("kind"),
                                                        MessageSourceKind.GROUP
                                                    )
                                                )
                                            } else {
                                                arrayOf(
                                                    equal(
                                                        record.get<MessageSourceKind>("kind"),
                                                        MessageSourceKind.GROUP
                                                    )
                                                )
                                            }
                                        )
                                        .distinct(true)
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
                get("/archive/user") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                            val records = factory.fromSession { session ->
                                session.withCriteria<Long> { criteria ->
                                    val record = criteria.from<MessageRecord>()
                                    val target = nullif(record.get<Long>("targetId"), record.get<Long>("bot"))

                                    criteria.select(coalesce(target, record.get("fromId")))
                                        .where(
                                            *if (bot != null) {
                                                arrayOf(
                                                    equal(record.get<Long>("bot"), bot),
                                                    notEqual(record.get<MessageSourceKind>("kind"),
                                                        MessageSourceKind.GROUP)
                                                )
                                            } else {
                                                arrayOf(
                                                    notEqual(record.get<MessageSourceKind>("kind"),
                                                        MessageSourceKind.GROUP)
                                                )
                                            }
                                        )
                                        .distinct(true)
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message.orEmpty())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message.orEmpty())
                        }
                    }
                }
            }
        }
    }

    public companion object {
        @JvmStatic
        internal val STATIC_KEY: String = "xyz.cssxsh.mirai.hibernate.http.static"

        @JvmStatic
        internal inline fun <reified T> success(data: T): String {
            val result = HibernateAdapterResult(
                code = 0,
                message = "success",
                data = data
            )
            return Json.encodeToString(result)
        }

        @JvmStatic
        internal inline fun failure(code: Int, message: String): String {
            val result = HibernateAdapterResult<Unit>(
                code = code,
                message = message
            )

            return Json.encodeToString(result)
        }
    }
}