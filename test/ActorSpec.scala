import actors.{ChatRoom, User}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.{AfterExample, Scope}
import play.api.Logger
import play.api.libs.json.Json
import play.api.test.WithApplication


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