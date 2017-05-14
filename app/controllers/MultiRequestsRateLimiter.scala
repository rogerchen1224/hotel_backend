package controllers

import scala.collection.mutable

/**
 * This rate limiter controls n requests in 10 seconds.
 *
 * Configured apiKey can have its own rate limit.
 * non-configurede apiKey will share the same rate limit controlled by the global apiKey
 *
 * @author roger1224@gmail.com
 *
 *         $$DateTime$$
 *         $$Change$$
 *         $$Author$$
 */
class MultiRequestsRateLimiter() {

  /**
   * Default global apiKey
   */
  val DefaultGlobalApiKey: String = "global-api-key"

  /**
   * Default rate limit period: requests per 10 seconds
   */
  var _rateLimitPeriodInMS: Int = 10 * 1000

  /**
   * Default suspended time in seconds
   */
  var _suspendedTimeInMS: Int = 5 * 60 * 1000

  /**
   * Default requests per period of time
   */
  var _defaultRequestsPerPeriodTime: Int = 50


  /**
   * Rate limit configurations
   * apiKey -> requests per 10 seconds
   */
  val rateLimitSettings: mutable.HashMap[String, Int] = new mutable.HashMap[String, Int]()

  /**
   * Suspended records
   * apiKey -> next available Epoch timestamp in ms
   */
  val nextAvailableTimes: mutable.HashMap[String, Long] = new mutable.HashMap[String, Long]()

  /**
   * Rate limit call records for configured apiKeys
   * apiKey -> call record times
   */
  val rateLimitCalls: mutable.HashMap[String, mutable.PriorityQueue[Long]] =
    new mutable.HashMap[String, mutable.PriorityQueue[Long]]()


  def setDefaultRequestsPerPeriodTime(value: Int) = {
    _defaultRequestsPerPeriodTime = value
  }

  def setSuspendedTimeInSec(value: Int) = {
    _suspendedTimeInMS = value * 1000
  }

  def setRateLimitPeriodInSec(value: Int) = {
    _rateLimitPeriodInMS = value * 1000
  }

  def addRateLimitSetting(apiKey: String, requestsAllowedInPeriod: Int) = {
    rateLimitSettings.put(apiKey, requestsAllowedInPeriod)
    printf("added apiKey setting. (%s=%s)\n", apiKey, requestsAllowedInPeriod)
  }

  /**
   * Check if the API call is allowed for the apiKey
   *
   * @param requestApiKey the apiKey to be checked
   * @return true if it is allowed, false if not
   */
  def acceptCall(requestApiKey: String): Boolean = {
    val now: Long = System.currentTimeMillis
    var apiKey: String = requestApiKey

    var requestsPerPeriodTime: Int = rateLimitSettings.getOrElse(apiKey, 0)
    if (requestsPerPeriodTime == 0) {
      apiKey = DefaultGlobalApiKey
      requestsPerPeriodTime = _defaultRequestsPerPeriodTime
    }

    val nextAvailableTime: Option[Long] = nextAvailableTimes.get(apiKey)
    if (!nextAvailableTime.isEmpty && nextAvailableTime.get > now) {
      printf("[%s] still suspended. timeLeft=%ss\n", apiKey, (nextAvailableTime.get - now) / 1000)
      return false
    }

    val callRecords: mutable.PriorityQueue[Long] = rateLimitCalls.getOrElseUpdate(apiKey, new mutable.PriorityQueue[Long]())

    if (callRecords.isEmpty || callRecords.last < (now - _rateLimitPeriodInMS)) {
      callRecords.clear
      callRecords.enqueue(now)
      printf("[%s] count=%s\n", apiKey, callRecords.length)
      return true
    }

    while (!callRecords.isEmpty && callRecords.head < (now - _rateLimitPeriodInMS)) {
      callRecords.dequeue
    }

    if (callRecords.length >= requestsPerPeriodTime) {
      nextAvailableTimes.put(apiKey, now + _suspendedTimeInMS)
      printf("[%s] suspended\n", apiKey)
      return false
    }

    callRecords.enqueue(now)
    printf("[%s] count=%s\n", apiKey, callRecords.length)
    return true
  }
}
