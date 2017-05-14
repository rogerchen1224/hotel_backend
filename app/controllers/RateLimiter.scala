package controllers

import scala.collection.mutable

/**
 * This rate limiter only controls one request in n seconds
 *
 * @author roger1224@gmail.com
 *
 *         $$DateTime$$
 *         $$Change$$
 *         $$Author$$
 */
class RateLimiter() {

  /**
   * Default rate limit: request per 10 seconds
   */
  var _defaultRateLimit: Int = 10

  /**
   * Default suspended time in seconds
   */
  var _suspendedTime: Int = 5 * 60

  /**
   * Rate limit configurations
   * apiKey -> request per n seconds
   */
  val rateLimitSettings: mutable.HashMap[String, Int] = new mutable.HashMap[String, Int]()

  /**
   * Rate limit records
   * apiKey -> next available Epoch timestamp in ms
   */
  val rateLimitRecords: mutable.HashMap[String, Long] = new mutable.HashMap[String, Long]()

  /**
   * Suspended api Keys
   */
  val suspendedRecords: mutable.TreeSet[String] = new mutable.TreeSet[String]()

  def setDefaultRateLimit(value: Int): Unit = {
    _defaultRateLimit = value
  }

  def setSuspendedTime(value: Int): Unit = {
    _suspendedTime = value
  }


  def addRateLimitSetting(apiKey: String, requestDelayInSec: Int) = {
    rateLimitSettings.put(apiKey, requestDelayInSec)
    printf("added apiKey setting. (%s=%ss)\n", apiKey, requestDelayInSec)
  }

  /**
   * Check if the API call is allowed for the apiKey
   *
   * @param apiKey the apiKey to be checked
   * @return true if it is allowed, false if not
   */
  def acceptCall(apiKey: String): Boolean = {
    val now: Long = System.currentTimeMillis
    val rateLimitSettingInSec: Int = rateLimitSettings.getOrElse(apiKey, _defaultRateLimit)

    val nextAvailableTime: Option[Long] = rateLimitRecords.get(apiKey)
    if (nextAvailableTime.isEmpty || nextAvailableTime.get <= now) {
      rateLimitRecords.put(apiKey, now + rateLimitSettingInSec * 1000)
      suspendedRecords -= apiKey
      return true
    }

    if (suspendedRecords.contains(apiKey)) {
      printf("[%s] still suspended. timeLeft=%ss\n", apiKey, (nextAvailableTime.get - now) / 1000)
      return false
    }

    rateLimitRecords.put(apiKey, now + _suspendedTime * 1000)
    suspendedRecords += apiKey
    printf("[%s] suspended for %ss\n", apiKey, _suspendedTime)
    return false
  }
}
