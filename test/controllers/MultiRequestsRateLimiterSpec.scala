package controllers

import org.scalatestplus.play.PlaySpec

/**
 *
 * @author roger1224@gmail.com
 *
 *         $$DateTime$$
 *         $$Change$$
 *         $$Author$$
 */
class MultiRequestsRateLimiterSpec extends PlaySpec {
  "A RateLimiter" should {
    "have default settings" in {
      val rateLimiter: MultiRequestsRateLimiter = new MultiRequestsRateLimiter()

      assert(rateLimiter._defaultRequestsPerPeriodTime == 50)
      assert(rateLimiter._suspendedTimeInMS == 300000)
      assert(rateLimiter._rateLimitPeriodInMS == 10000)
    }

    "be able to set default settings" in {
      val rateLimiter: MultiRequestsRateLimiter = new MultiRequestsRateLimiter()

      assert(rateLimiter._defaultRequestsPerPeriodTime == 50)
      assert(rateLimiter._suspendedTimeInMS == 300000)
      assert(rateLimiter._rateLimitPeriodInMS == 10000)

      rateLimiter.setDefaultRequestsPerPeriodTime(30)
      rateLimiter.setSuspendedTimeInSec(50)
      rateLimiter.setRateLimitPeriodInSec(20)

      assert(rateLimiter._defaultRequestsPerPeriodTime == 30)
      assert(rateLimiter._suspendedTimeInMS == 50000)
      assert(rateLimiter._rateLimitPeriodInMS == 20000)
    }

    "be able to add setting for apiKey" in {
      val rateLimiter: MultiRequestsRateLimiter = new MultiRequestsRateLimiter()

      assert(rateLimiter.rateLimitSettings.size == 0)

      rateLimiter.addRateLimitSetting("12345", 20)

      assert(rateLimiter.rateLimitSettings.size == 1)
      assert(rateLimiter.rateLimitSettings.get("12345").get == 20)
    }

    "return false when over the rate limit" in {
      val rateLimiter: MultiRequestsRateLimiter = new MultiRequestsRateLimiter()
      val apiKey = "12345"
      rateLimiter.setDefaultRequestsPerPeriodTime(5)
      rateLimiter.setSuspendedTimeInSec(3)
      rateLimiter.setRateLimitPeriodInSec(1)

      for (i <- 1 to 5) {
        assert(rateLimiter.acceptCall(apiKey))
      }
      assert(!rateLimiter.acceptCall(apiKey))
      assert(!rateLimiter.acceptCall(apiKey))

      Thread.sleep(3100)

      for (i <- 1 to 4) {
        assert(rateLimiter.acceptCall(apiKey))
      }

      Thread.sleep(500)
      assert(rateLimiter.acceptCall(apiKey))
      assert(!rateLimiter.acceptCall(apiKey))
    }
  }
}
