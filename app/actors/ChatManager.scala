package actors

import akka.actor.{ActorLogging, Actor, Props, ActorRef}
import play.api.libs.concurrent.Akka
import play.api.Play.current

/**
 * Created by tudor on 06/11/14.
 */

object ChatManager {
  val ref: ActorRef = Akka.system.actorOf(Props(new ChatManager))
}

class ChatManager extends Actor with ActorLogging {
  val roomPrefix = "room"
  var currentRoomId = 0

  val mainChatRoom = Akka.system.actorOf(ChatRoom.props(0), roomPrefix + 0)
  var chatRooms: Map[Int, ActorRef] = Map(0 -> mainChatRoom)

  def receive = {
    case joinChat @ ChatRoom.Join(userName) =>
      mainChatRoom forward joinChat

    case leaveChat @ ChatRoom.Leave(userName) =>
      mainChatRoom forward leaveChat

    case sendMsg @ ChatRoom.Text(roomId, userName, txt) =>
      log.debug(s"trying to forward msg from $userName to room $roomId")
      chatRooms.get(roomId).getOrElse {
        currentRoomId = currentRoomId + 1
        val roomActor = context.actorOf(ChatRoom.props(currentRoomId), roomPrefix + currentRoomId)
        chatRooms = chatRooms + (currentRoomId -> roomActor)
        roomActor
      } forward sendMsg

  }
}
