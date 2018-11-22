package ru.kappers.util;

import lombok.extern.log4j.Log4j;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Log4j
public class DateUtil {

	public static Timestamp getCurrentTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Получить Timestamp из строки
	 * @param date строка в формате {@link DateTimeFormatter#BASIC_ISO_DATE}
	 * @return новый Timestamp
	 */
	public static Timestamp convertDate(String date) {
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
		return Timestamp.valueOf(localDate.atStartOfDay());
	}

	public static Date getSqlDateFromTimeStamp(String formated) throws ParseException {
		String dateString = formated;
		dateString = dateString.substring(0, dateString.indexOf("+"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		java.util.Date date = format.parse(dateString);
		//java.util.Date date = format.parse("2018-11-21T12:00:00");
		Date sqlDate = new Date(date.getTime());
		return sqlDate;
	}


}
