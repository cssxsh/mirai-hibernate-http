package xyz.cssxsh.mirai.hibernate.http

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

    @PublishedApi
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
                // region face
                get("/face/random") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val record = FaceRecord.random()
                            success(data = record)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = "random failure, face record may be empty.")
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: IllegalStateException) {
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
                        }
                    }
                }
                // endregion

                // region message
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
                                session.withCriteria<MessageRecord> { query ->
                                    val record = query.from<MessageRecord>()
                                    query.select(record)
                                        .where(
                                            between(record.get("time"), start, end),
                                            equal(record.get<Long>("bot"), bot)
                                        )
                                        .orderBy(desc(record.get<Int>("time")))
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                                session.withCriteria<MessageRecord> { query ->
                                    val record = query.from<MessageRecord>()
                                    query.select(record)
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                                session.withCriteria<MessageRecord> { query ->
                                    val record = query.from<MessageRecord>()
                                    query.select(record)
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                                session.withCriteria<MessageRecord> { query ->
                                    val record = query.from<MessageRecord>()
                                    query.select(record)
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                                session.withCriteria<MessageRecord> { query ->
                                    val record = query.from<MessageRecord>()
                                    query.select(record)
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
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
                                session.withCriteria<MessageRecord> { query ->
                                    val record = query.from<MessageRecord>()
                                    query.select(record)
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
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
                        }
                    }
                }
                get("/message/source") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val target = call.parameters["target"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter target")
                            val time = call.parameters["time"]?.toIntOrNull()
                                ?: throw NoSuchElementException("need parameter time")
                            val ids = call.parameters["ids"]
                                ?: throw NoSuchElementException("need parameter ids")
                            val records = factory.fromSession { session ->
                                session.withCriteria<MessageRecord> { query ->
                                    val record = query.from<MessageRecord>()
                                    query.select(record)
                                        .where(
                                            equal(record.get<Long>("targetId"), target),
                                            equal(record.get<Long>("time"), time),
                                            equal(record.get<String>("ids"), ids)
                                        )
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
                        }
                    }
                }
                // endregion

                // region archive
                get("/archive/bot") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val records = factory.fromSession { session ->
                                session.withCriteria<BotRecord> { query ->
                                    val record = query.from<BotRecord>()
                                    query.select(record)
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
                        }
                    }
                }
                get("/archive/group") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                            val records = factory.fromSession { session ->
                                session.withCriteria<GroupRecord> { query ->
                                    val record = query.from<GroupRecord>()
                                    val group = record.get<Long>("group")
                                    query.select(record)

                                    if (bot != null) {
                                        val subquery = query.subquery<GroupMemberRecord>()
                                        val member = subquery.from<GroupMemberRecord>()
                                        val uuid = member.get<GroupMemberIndex>("uuid")
                                        subquery.select(member)
                                            .where(
                                                equal(uuid.get<Long>("group"), group),
                                                equal(uuid.get<Long>("uid"), bot)
                                            )
                                        query.where(exists(subquery))
                                    }
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
                        }
                    }
                }
                get("/archive/friend") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val bot = call.parameters["bot"]?.toLongOrNull()
                            val records = factory.fromSession { session ->
                                session.withCriteria<FriendRecord> { query ->
                                    val record = query.from<FriendRecord>()
                                    val uuid = record.get<FriendIndex>("uuid")
                                    query.select(record)
                                    if (bot != null) {
                                        query.where(
                                            equal(uuid.get<Long>("bot"), bot)
                                        )
                                    }
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
                        }
                    }
                }
                get("/archive/member") {
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        try {
                            val group = call.parameters["group"]
                                ?: throw NoSuchElementException("need parameter group")
                            val records = factory.fromSession { session ->
                                session.withCriteria<GroupMemberRecord> { query ->
                                    val record = query.from<GroupMemberRecord>()
                                    val uuid = record.get<GroupMemberIndex>("uuid")
                                    query.select(record)
                                        .where(
                                            equal(uuid.get<Long>("group"), group)
                                        )
                                }.list()
                            }
                            success(data = records)
                        } catch (cause: NoSuchElementException) {
                            failure(code = 400, message = cause.message ?: cause.stackTraceToString())
                        } catch (cause: Throwable) {
                            failure(code = 500, message = cause.message ?: cause.stackTraceToString())
                        }
                    }
                }
                // endregion
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