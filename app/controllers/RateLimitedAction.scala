package controllers

import controllers.Application._
import play.api.mvc.{ActionBuilder, Request, Result}
import play.api.{Play, mvc}

import scala.concurrent.Future

/**
 *
 * @author roger1224@gmail.com
 *
 *         $$DateTime$$
 *         $$Change$$
 *         $$Author$$
 */
object RateLimitedAction extends ActionBuilder[Request] {

  val rateLimiter: RateLimiter = new RateLimiter()

  initRateLimiter

  private def initRateLimiter = {
    val lines = scala.io.Source.fromFile(Play.current.getFile("conf/api-rate-limit.conf")).getLines

    for (line <- lines) {
      val fields: Array[String] = line.split("=")

      if (fields.length >= 2)
        rateLimiter.addRateLimitSetting(fields(0), fields(1).toInt)
    }

    rateLimiter.setDefaultRateLimit(Play.current.configuration.getInt("api.rateLimit.defaultRateLimitInSec").getOrElse(10))
    rateLimiter.setSuspendedTime(Play.current.configuration.getInt("api.rateLimit.suspendedTimeInSec").getOrElse(300))

    println("Init RateLimiter.")
  }

  override def invokeBlock[A](request: mvc.Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    val apiKey: Option[String] = request.getQueryString("apiKey")
    if (apiKey.isEmpty) return Future.successful(Forbidden("apiKey is absent"))

    if (!rateLimiter.acceptCall(apiKey.get))
      return Future.successful(Forbidden("apiKey is rate limited"))

    block(request)
  }
}
