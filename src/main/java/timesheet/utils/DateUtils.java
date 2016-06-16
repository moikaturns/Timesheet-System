package timesheet.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	public static Date firstDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, 2);
		return calendar.getTime();
	}

	public static Date firstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		return calendar.getTime();
	}
}
