package actors

import actors.ChatRoom._
import akka.actor.{ActorLogging, Props, ActorRef, Actor}
import play.api.libs.json.{JsValue, Json}

/**
 * Created by tudor on 06/11/14.
 */

object User {
  def props(client: ActorRef): Props = Props(new User(client))
}

class User(client: ActorRef) extends Actor with ActorLogging {
  val path = self.path
  val initMessage = s"WebSocket connection has been established for user $path"
  val userName = "user_" + path.toStringWithoutAddress.replaceAll("[a-z/]", "")
  doWelcome

  def doWelcome = {
    client ! Json.obj("msg" -> initMessage)
    val welcomeMessage = Json.obj(
      "type" -> "chatmessage",
      "sender" -> ChatRoom.SENDER_SYS,
      "message" -> s"Welcome to Akka Chat. Your nickname is $userName"
    )
    client ! welcomeMessage
    val nickMessage = Json.obj(
      "type" -> "setnickname",
      "nickname" -> userName
    )
    client ! nickMessage

    ChatManager.ref ! Join(userName)
    log.debug(s"user $userName joined")
  }

  override def receive: Receive = {

    case message: JsValue =>
      val chatRoomId = (message \ "roomId").as[Int]
      val messageText = (message \ "message").as[String]
      ChatManager.ref ! ChatRoom.Text(chatRoomId, userName, messageText)

    case Text(roomId, userName, text) =>
      val message = Json.obj(
        "type" -> "chatmessage",
        "sender" -> userName,
        "message" -> text
      )
      client ! message

    case UserList(users) =>
      val message = Json.obj(
        "type" -> "userlist",
        "users" -> users
      )
      client ! message

  }

  override def postStop(): Unit = {
    super.postStop()
    ChatManager.ref ! ChatRoom.Leave(userName)
  }
}
