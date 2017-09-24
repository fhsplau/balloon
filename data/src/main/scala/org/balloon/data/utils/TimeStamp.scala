package org.balloon.data.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeStamp {
  val pattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

  def now: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
}
