package xyz.cssxsh.mirai.hibernate.http

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.*
import net.mamoe.mirai.api.http.context.session.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.hibernate.*
import xyz.cssxsh.mirai.hibernate.entry.*
import java.io.File

public class HibernateAdapter : MahKtorAdapter("hibernate") {

    internal val setting: HibernateAdapterSetting by lazy {
        getSetting() ?: HibernateAdapterSetting(port = 8081)
    }

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
                        val record = FaceRecord.random()
                        Json.encodeToString(record)
                    }
                }
                put("/face/disable") {
                    val md5 = call.parameters["md5"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = FaceRecord.disable(md5 = md5)
                        Json.encodeToString(records)
                    }
                }
                get("/face/tag") {
                    val md5 = call.parameters["md5"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = FaceTagRecord.get(md5 = md5)
                        Json.encodeToString(records)
                    }
                }
                put("/face/tag") {
                    val md5 = call.parameters["md5"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val tag = call.parameters["tag"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = FaceTagRecord.set(md5 = md5, tag = tag)
                        Json.encodeToString(records)
                    }
                }
                delete("/face/tag") {
                    val md5 = call.parameters["md5"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    val tag = call.parameters["tag"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = FaceTagRecord.remove(md5 = md5, tag = tag)
                        Json.encodeToString(records)
                    }
                }
                // message
                get("/message/bot") {
                    val bot = call.parameters["bot"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val start = call.parameters["start"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val end = call.parameters["end"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = MiraiHibernateRecorder.get(
                            bot = Bot.getInstance(bot),
                            start = start,
                            end = end
                        )
                        Json.encodeToString(records)
                    }
                }
                get("/message/group") {
                    val bot = call.parameters["bot"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val group = call.parameters["group"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val start = call.parameters["start"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val end = call.parameters["end"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = MiraiHibernateRecorder.get(
                            group = Bot.getInstance(bot).getGroupOrFail(group),
                            start = start,
                            end = end
                        )
                        Json.encodeToString(records)
                    }
                }
                get("/message/friend") {
                    val bot = call.parameters["bot"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val friend = call.parameters["friend"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val start = call.parameters["start"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val end = call.parameters["end"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = MiraiHibernateRecorder.get(
                            friend = Bot.getInstance(bot).getFriendOrFail(friend),
                            start = start,
                            end = end
                        )
                        Json.encodeToString(records)
                    }
                }
                get("/message/member") {
                    val bot = call.parameters["bot"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val group = call.parameters["group"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val member = call.parameters["member"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val start = call.parameters["start"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val end = call.parameters["end"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = MiraiHibernateRecorder.get(
                            member = Bot.getInstance(bot).getGroupOrFail(group).getOrFail(member),
                            start = start,
                            end = end
                        )
                        Json.encodeToString(records)
                    }
                }
                get("/message/stranger") {
                    val bot = call.parameters["bot"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val stranger = call.parameters["stranger"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val start = call.parameters["start"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val end = call.parameters["end"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = MiraiHibernateRecorder.get(
                            stranger = Bot.getInstance(bot).getStrangerOrFail(stranger),
                            start = start,
                            end = end
                        )
                        Json.encodeToString(records)
                    }
                }
                get("/message/kind") {
                    val kind = call.parameters["kind"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val start = call.parameters["start"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val end = call.parameters["end"]?.toInt()  ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respondText(status = HttpStatusCode.OK, contentType = ContentType.Application.Json) {
                        val records = MiraiHibernateRecorder.get(
                            kind = MessageSourceKind.valueOf(kind),
                            start = start,
                            end = end
                        )
                        Json.encodeToString(records)
                    }
                }
            }
        }
    }

    public companion object {
        @JvmStatic
        internal val STATIC_KEY: String = "xyz.cssxsh.mirai.hibernate.http.static"
    }
}