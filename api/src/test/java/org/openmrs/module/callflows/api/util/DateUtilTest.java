package org.openmrs.module.callflows.api.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DateUtil.class})
public class DateUtilTest {

	@Test
	public void shouldSuccessfullyParseDateTime() {
		Date expected = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "Asia/Almaty");
		Date actual = DateUtil.parse("2010-11-16T15:43:59.000+06:00");
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void shouldsuccessfullyConvertDateToUtcTimeZone() {
		Date expectedUtc = createDate(2010, Calendar.NOVEMBER, 16, 14, 43, 59, "UTC");
		Date dateCet = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "CET");
		Date actual = DateUtil.getDateWithDefaultTimeZone(dateCet);
		assertThat(actual, equalTo(expectedUtc));
	}

	@Test
	public void shouldReturnDateWithLocalTimeZone() throws Exception {
		Date date = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "Asia/Almaty");
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Almaty");
		PowerMockito.mockStatic(TimeZone.class);
		BDDMockito.given(TimeZone.getDefault()).willReturn(timeZone);

		String expected = "2010-11-16T15:43:59.000+06:00";
		String actual = DateUtil.getDateWithLocalTimeZone(date);
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void shouldReturnConversionResultAsExpected() {
		String expectedDateAsString = "2012-01-10T00:00:00.000+06:00";
		Date actual = DateUtil.parse(expectedDateAsString);
		assertThat(DateUtil.dateToString(actual, "Asia/Almaty"), equalTo(expectedDateAsString));
	}

	@Test
	public void shouldSuccessfullyAddSecondsToDate() {
		Date expected = createDate(2010, Calendar.MARCH, 2, 15, 43, 53, "CET");
		Date date = createDate(2010, Calendar.MARCH, 2, 15, 43, 58, "CET");
		Date actual = DateUtil.plusSeconds(expected, 5);
		assertThat(actual, equalTo(date));
	}

	private Date createDate(int year, int month, int day, int hour, int minute, int second, String timezone) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.setTimeZone(TimeZone.getTimeZone(timezone));
		return calendar.getTime();
	}
}
