package uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicLong

interface TestClock {
  val clock: Clock
  val instant: Instant get() = clock.instant()

  companion object {
    internal val defaultCurrentTime: Instant = Instant.parse("2024-01-01T00:00:00Z")

    fun defaultClock() = BasicTestClock.defaultClock()
    fun timeslotClock() = TimeslotClock.defaultClock()

    fun fixedClock(fixedInstant: Instant) = BasicTestClock.fixedClock(fixedInstant)
    fun timeslotClock(startTime: Instant) = TimeslotClock.timeslotToClock(startTime)
    fun timeslotClock(startTime: Instant, timeslotLength: Duration) = TimeslotClock(startTime, timeslotLength)
  }

  open class BasicTestClock(
    private val testClock: Clock,
  ) : TestClock {
    override val clock: Clock get() = testClock

    internal companion object {
      val defaultCurrentTime = TestClock.defaultCurrentTime

      fun fixedClock(fixedInstant: Instant) = FixedTestClock(fixedInstant)
      fun defaultClock() = FixedTestClock(defaultCurrentTime)
    }
  }

  class FixedTestClock(fixedInstant: Instant) : BasicTestClock(Clock.fixed(fixedInstant, ZoneOffset.UTC))

  class TimeslotClock(
    private val startTime: Instant,
    private val timeslotLength: Duration,
  ) : TestClock {
    private val zoneId = ZoneOffset.UTC as ZoneId
    private val baseClock: Clock get() = Clock.fixed(startTime, zoneId)

    val timeslot: AtomicLong = AtomicLong(0L)

    override val clock: Clock get() = timeslotToClock(timeslot.toLong())

    internal companion object {
      val defaultDuration = Duration.ofDays(1)

      fun defaultClock() = incrementDailyCLock()
      fun incrementDailyCLock() = TimeslotClock(defaultCurrentTime, defaultDuration)
      fun timeslotToClock(startTime: Instant, timeslotLength: Duration = defaultDuration) = TimeslotClock(startTime, timeslotLength)
    }

    private fun timeslotToClock(timeslot: Long): Clock = Clock.offset(baseClock, timeslotLength.multipliedBy(timeslot))
  }
}
