import java.util.concurrent.TimeUnit

import actors.{ChatRoom, User}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.{AfterExample, Scope}
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import play.api.test.WithApplication

import scala.concurrent.duration.Duration


/**
 * Created by tudor on 10/11/14.
 */


@RunWith(classOf[JUnitRunner])
class ActorSpec extends Specification {

  class Actors extends TestKit(ActorSystem("test")) with Scope with ImplicitSender with AfterExample {
    override protected def after {
      system.shutdown
      system.awaitTermination
    }
  }

  sequential

  "User actor" should {

    "message to all in room" in new Actors {
      new WithApplication {
        Logger.logger.debug("running scenario - message to all in room")
        val clientProbe = new TestProbe(system)
        val client = clientProbe.ref
        val user = TestActorRef[User](User.props(client), "100")

        val client2Probe = new TestProbe(system)
        val client2 = client2Probe.ref
        val user2 = TestActorRef[User](User.props(client2), "101")

        val txt = "test message"
        val username = "user_100"
        val roomId = 0
        val msg = Json.obj("roomId" -> roomId, "message" -> txt)
        val expectedJson = Json.obj("type" -> "chatmessage", "sender" -> username , "message" -> txt)

        user.tell(msg, client)

        client2Probe.fishForMessage(Duration(3, TimeUnit.SECONDS)) {
          case msg: JsObject if msg == expectedJson => true
          case other => {
            Logger.logger.debug("user2Probe got message: " + other)
            false
          }
        }

      }
    }

    "reply with connection greetings and nickname" in new Actors {
      new WithApplication {
        Logger.logger.debug("running scenario - reply with connection greetings and nickname")

        val clientProbe = new TestProbe(system)
        val client = clientProbe.ref
        val user = TestActorRef[User](User.props(client), "100")

        val txt = "test message"
        val username = "user_100"
        val roomId = 0

        val wsInitMessage = Json.obj(
          "msg" -> "WebSocket connection has been established for user akka://test/user/100"
        )
        val joinMessage = Json.obj(
          "type" -> "chatmessage",
          "sender" -> ChatRoom.SENDER_SYS,
          "message" -> s"Welcome to Akka Chat. Your nickname is $username"
        )
        val nickMessage = Json.obj(
          "type" -> "setnickname",
          "nickname" -> username
        )
        clientProbe.expectMsg(wsInitMessage)
        clientProbe.expectMsg(joinMessage)
        clientProbe.expectMsg(nickMessage)
      }
    }
  }

}