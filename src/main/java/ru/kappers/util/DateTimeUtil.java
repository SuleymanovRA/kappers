package ru.kappers.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Утилитный класс даты и времени
 */
@Slf4j
public final class DateTimeUtil {

	/** миллисекунд в сутках */
	public static final int MILLISECONDS_IN_DAY = 24 * 3600 * 1000;
	/** миллисекунд в неделе */
	public static final int MILLISECONDS_IN_WEEK = MILLISECONDS_IN_DAY * 7;

	/**
	 * Получить экземпляр {@link Timestamp} с текущей датой и временем
	 * @return экземпляр {@link Timestamp}
	 */
	public static Timestamp getCurrentTime() {
		log.debug("getCurrentTime()...");
		final Timestamp result = Timestamp.valueOf(LocalDateTime.now());
		log.debug("getCurrentTime() return result: {}", result);
		return result;
	}

	/**
	 * Получить экземпляр {@link Timestamp} из строки
	 * @param date строка с датой в формате {@link DateTimeFormatter#ISO_OFFSET_DATE}
	 * @return экземпляр {@link Timestamp}
	 */
	public static Timestamp parseTimestampFromDate(String date) {
		log.debug("parseTimestampFromDate(date: {})...", date);
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_OFFSET_DATE);
		final Timestamp result = Timestamp.valueOf(localDate.atStartOfDay());
		log.debug("parseTimestampFromDate(date: {}) return result: {}", date, result);
		return result;
	}

	/**
	 * Получить экземпляр {@link Timestamp} из строки
	 * @param dateTime строка с датой и временем в формате {@link DateTimeFormatter#ISO_ZONED_DATE_TIME}
	 * @return экземпляр {@link Timestamp}
	 */
	public static Timestamp parseTimestampFromZonedDateTime(String dateTime) {
		log.debug("parseTimestampFromZonedDateTime(dateTime: {})...", dateTime);
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		final Timestamp result = Timestamp.valueOf(zonedDateTime.toLocalDateTime());
		log.debug("parseTimestampFromZonedDateTime(dateTime: {}) return result: {}", dateTime, result);
		return result;
	}

	/**
	 * Получить экземпляр {@link Date} из строки
	 * @param dateTime строка с датой и временем в формате {@link DateTimeFormatter#ISO_ZONED_DATE_TIME}
	 * @return экземпляр {@link Date}
	 */
	public static Date parseSqlDateFromZonedDateTime(String dateTime) {
		log.debug("parseSqlDateFromZonedDateTime(dateTime: {})...", dateTime);
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		final Date result = Date.valueOf(zonedDateTime.toLocalDate());
		log.debug("parseSqlDateFromZonedDateTime(dateTime: {}) return result: {}", dateTime, result);
		return result;
	}

}