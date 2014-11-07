package actors

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
  case class UserList(users: Set[String])

  val SENDER_SYS = "[SYSTEM]"
}

class ChatRoom(id: Int) extends Actor with ActorLogging {

  import ChatRoom._
  log.debug(s"Created room $id")

  var users: Map[String, ActorRef] = Map.empty

  override def receive = {
    case Join(userName) =>
      log.debug(s"User $userName has joined the chat")
      users = users + (userName -> sender())
      users.values.foreach( user => {
        user ! Text(id, SENDER_SYS, s"User $userName has joined the chat")
        user ! UserList(users.keys.toSet)
      })

    case Leave(userName) =>
      users = users - userName
      users.values.foreach(_ ! Text(id, SENDER_SYS, s"User $userName has left the chat"))

    case textMessage @ Text(roomId, user, msg) =>
      users.filterKeys(_ != user)
        .values
        .foreach(_ ! textMessage)

  }
}