package actors

import akka.actor.Actor.Receive
import akka.actor.{ActorLogging, ActorRef, Actor, Props}

/**
 * Created by tudor on 06/11/14.
 */

object ChatRoom {
  def props(id: Int) = Props(new ChatRoom(id))

  // Message Protocol
  trait ChatMessage
  case class Join(user: String) extends ChatMessage
  case class Leave(user: String) extends ChatMessage
  case class JoinRoom(roomId: Int) extends ChatMessage
  case class Text(roomId: Int, sender: String, text: String) extends ChatMessage

  val SENDER_SYS = "[SYSTEM]"
}

class ChatRoom(id: Int) extends Actor with ActorLogging {

  import ChatRoom._
  log.debug(s"Created room $id")

  var users: Set[ActorRef] = Set.empty

  override def receive = {
    case Join(userName) =>
      log.debug(s"User $userName has joined the chat")
      users = users + sender()
      users.foreach(_ ! Text(id, SENDER_SYS, s"User $userName has joined the chat"))

    case Leave(userName) =>
      users = users - sender()
      users.foreach(_ ! Text(id, SENDER_SYS, s"User $userName has left the chat"))

    case textMessage @ Text(roomId, user, msg) =>
      users = users - sender()
      users.filter(_ != sender())
        .foreach(_ ! textMessage)
  }
}