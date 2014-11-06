package actors

import akka.actor.{Props, ActorRef, Actor}
import play.api.libs.json.Json

/**
 * Created by tudor on 06/11/14.
 */

object User {
  def props(client: ActorRef): Props = Props(new User(client))
}

class User(client: ActorRef) extends Actor {
  val path = self.path
  val initMessage = s"WebSocket connection has been established for user $path"
  client ! Json.obj("msg" -> initMessage)


  override def receive: Receive = {

    case ChatRoom.JoinChat => ???

    case ChatRoom.LeaveChat => ???

    case ChatRoom.Text(message) => ???

  }
}
