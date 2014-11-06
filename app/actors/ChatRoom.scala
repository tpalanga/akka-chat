package actors

import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Actor, Props}

/**
 * Created by tudor on 06/11/14.
 */

object ChatRoom {
  def props(id: Int) = Props(new ChatRoom(id))

  // Message Protocol
  trait ChatMessage
  case object JoinChat
  case object LeaveChat
  case class Text(text: String) extends ChatMessage
}

class ChatRoom(id: Int) extends Actor {

  import ChatRoom._

  var users: Set[ActorRef] = Set.empty

  override def receive = {
    case _ => ???
  }
}