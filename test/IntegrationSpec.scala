import java.util.concurrent.TimeUnit

import org.fluentlenium.core.filter.Filter
import org.junit.runner._
import org.specs2.runner._
import play.api.test._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends PlaySpecification {

  "Application" should {

    "work from within a browser" in new WithBrowser(FIREFOX.newInstance()) {
      browser.goTo("http://localhost:" + port)
      browser.pageSource must contain("Akka Chat")
    }

    "connect websocket" in new WithBrowser(FIREFOX.newInstance()) {
      browser.goTo("http://localhost:" + port)
      browser.waitUntil(10, TimeUnit.SECONDS) {
        browser.find("#wsStatus", new Filter("connection", "open")).size() > 0
      }
    }

  }
}