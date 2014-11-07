package actors

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
  client ! Json.obj("msg" -> initMessage)
  val userName = "user_" + path.toStringWithoutAddress.replaceAll("[a-z/]", "")
  ChatManager.ref ! ChatRoom.Join(userName)
  log.debug(s"user $userName joined")


  override def receive: Receive = {

    case message: JsValue =>
      val chatRoomId = (message \ "roomId").as[Int]
      val messageText = (message \ "text").as[String]
      ChatManager.ref ! ChatRoom.Text(chatRoomId, userName, messageText)


    case ChatRoom.Leave => ???

    case ChatRoom.Text(roomId, userName, text) =>
      val message = Json.obj(
        "type" -> "chatmessage",
        "sender" -> userName,
        "msg" -> text
      )
      client ! message

  }
}
