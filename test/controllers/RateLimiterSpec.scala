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
class RateLimiterSpec extends PlaySpec {
  "A RateLimiter" should {
    "have default settings" in {
      val rateLimiter: RateLimiter = new RateLimiter()

      assert(rateLimiter._defaultRateLimit == 10)
      assert(rateLimiter._suspendedTime == 300)
    }

    "be able to set default settings" in {
      val rateLimiter: RateLimiter = new RateLimiter()

      assert(rateLimiter._defaultRateLimit == 10)
      assert(rateLimiter._suspendedTime == 300)

      rateLimiter.setDefaultRateLimit(30)
      rateLimiter.setSuspendedTime(500)

      assert(rateLimiter._defaultRateLimit == 30)
      assert(rateLimiter._suspendedTime == 500)
    }

    "be able to add setting for apiKey" in {
      val rateLimiter: RateLimiter = new RateLimiter()

      assert(rateLimiter.rateLimitSettings.size == 0)

      rateLimiter.addRateLimitSetting("12345", 20)

      assert(rateLimiter.rateLimitSettings.size == 1)
      assert(rateLimiter.rateLimitSettings.get("12345").get == 20)
    }

    "return false for the 2nd request of the same apiKey" in {
      val rateLimiter: RateLimiter = new RateLimiter()
      val apiKey = "12345"
      rateLimiter.setDefaultRateLimit(1)
      rateLimiter.setSuspendedTime(3)

      assert(rateLimiter.acceptCall(apiKey))
      assert(math.abs(rateLimiter.rateLimitRecords.get(apiKey).get - (System.currentTimeMillis + 1000)) < 100)
      assert(!rateLimiter.suspendedRecords.contains(apiKey))

      Thread.sleep(1100)

      assert(rateLimiter.acceptCall(apiKey))
      assert(math.abs(rateLimiter.rateLimitRecords.get(apiKey).get - (System.currentTimeMillis + 1000)) < 100)
      assert(!rateLimiter.acceptCall(apiKey))
      assert(rateLimiter.suspendedRecords.contains(apiKey))

      Thread.sleep(3100)
      assert(rateLimiter.acceptCall(apiKey))
      assert(!rateLimiter.suspendedRecords.contains(apiKey))
      assert(math.abs(rateLimiter.rateLimitRecords.get(apiKey).get - (System.currentTimeMillis + 1000)) < 100)
    }
  }
}
