package controllers

import org.scalatestplus.play.PlaySpec
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

/**
 *
 * @author roger1224@gmail.com
 *
 *         $$DateTime$$
 *         $$Change$$
 *         $$Author$$
 */
class HotelControllerSpec extends PlaySpec with Results {
  "A HotelController" should {
    "return something" in {
      val controller = new HotelControllerTester

      val result: Future[Result] = controller.findByCityId("").apply(FakeRequest())


      val bodyText: String = contentAsString(result)
      bodyText mustBe "111"
    }


  }

  class HotelControllerTester extends HotelController {
    override def loadFromFile: Unit = {}
  }

}
