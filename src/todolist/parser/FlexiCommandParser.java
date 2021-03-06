//@@author A0130620B
package todolist.parser;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import todolist.model.TokenizedCommand;

public class FlexiCommandParser {

	public FlexiCommandParser() {

	}

	/**
	 * This method takes in flexible input and parse it then translates it into
	 * tokenized command
	 *
	 * @param input
	 *            raw user input
	 * @return tokenized add command
	 */
	public TokenizedCommand parse(String input) {

		input = input.replaceAll("\"", "");
		input = input.trim();
		String temp[] = input.split(" ");

		if (input.equals("")) {
			return new TokenizedCommand("invalid", temp);
		}

		Parser parser = new Parser(TimeZone.getTimeZone(ZoneOffset.systemDefault()));
		List<DateGroup> groups = parser.parse(input);
		List<Date> dates = null;

		int[] column = new int[] { 0, 0 };
		int[] length = new int[] { 0, 0 };
		int counter = 0;

		for (DateGroup group : groups) {
			dates = group.getDates();
			column[counter] = group.getPosition();
			length[counter] = group.getText().length();
			counter++;
		}

		String result = null;

		if (dates == null || dates.size() == 0) {
			return addTask(input);
		} else {

			result = removeTimeInfo(input, column, length);

			if (dates.size() == 1) {
				return addDeadline(input, result, dates);
			} else if (dates.size() == 2){
				return addEvent(result, dates);
			} else {
				return addTask(input);
			}
		}
	}

	private TokenizedCommand addTask(String input) {
		if (input.equals("")) {
			return new TokenizedCommand("invalid", null);
		}
		return new TokenizedCommand("add", new String[] { "task", input });
	}

	private TokenizedCommand addDeadline(String original, String input, List<Date> dates) {
		String result = input;

		Date deadline = dates.get(0);

		Instant instant = Instant.ofEpochMilli(deadline.getTime());
		LocalDateTime end = LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());

		DecimalFormat decimalFormatter = new DecimalFormat("00");
		String deadlineDate = end.getYear() + "-" + decimalFormatter.format(end.getMonthValue()) + "-"
				+ decimalFormatter.format(end.getDayOfMonth());

		String deadlineTime = decimalFormatter.format(end.getHour()) + ":" + decimalFormatter.format(end.getMinute());

		deadlineTime = searchKeywordToday(original, end, deadlineTime);

		result = keywordFilter(result);

		deadlineTime = searchKeywordTime(result, deadlineTime);

		if (result.equals("")) {
			return new TokenizedCommand("invalid", null);
		}
		
		return new TokenizedCommand("add", new String[] { "deadline", result, deadlineDate, deadlineTime });
	}

	private TokenizedCommand addEvent(String input, List<Date> dates) {
		String result = input;

		DecimalFormat decimalFormatter = new DecimalFormat("00");

		Date startTimeOriginal = dates.get(0);
		Date endTimeOriginal = dates.get(1);

		Instant startInstant = Instant.ofEpochMilli(startTimeOriginal.getTime());
		LocalDateTime start = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());

		String startDate = start.getYear() + "-" + decimalFormatter.format(start.getMonthValue()) + "-"
				+ decimalFormatter.format(start.getDayOfMonth());

		String startTime = decimalFormatter.format(start.getHour()) + ":" + decimalFormatter.format(start.getMinute());

		int interval = (int) getDateDiff(startTimeOriginal, endTimeOriginal) / 1000 / 60;

		result = keywordFilter(result);

		if (result.equals("")) {
			return new TokenizedCommand("invalid", null);
		}

		return new TokenizedCommand("add",
				new String[] { "event", result, startDate, startTime, Integer.toString(interval), "minute" });
	}

	private String removeTimeInfo(String input, int[] column, int[] length) {
		String result = input;
		if (column[1] == 0) {
			result = input.substring(0, column[0] - 1) + input.substring(column[0] + length[0] - 1);
		} else {
			result = input.substring(0, column[0] - 1) + input.substring(column[0] + length[0], column[1] - 1)
					+ input.substring(column[1] + length[1] - 1);
		}
		return result;
	}

	private String searchKeywordToday(String input, LocalDateTime end, String originalDeadline) {
		String deadlineTime = originalDeadline;
		if (end.isBefore(LocalDateTime.now().plus(10, ChronoUnit.SECONDS))
				&& end.plus(10, ChronoUnit.SECONDS).isAfter(LocalDateTime.now())
				&& input.toLowerCase().contains("today")) {
			deadlineTime = "23:59";
		}
		return deadlineTime;
	}

	private String searchKeywordTime(String input, String originalDeadline) {
		String deadlineTime = originalDeadline;

		if (input.toLowerCase().contains("breakfast")) {
			deadlineTime = "09:00";
		}

		if (input.toLowerCase().contains("lunch")) {
			deadlineTime = "13:00";
		}

		if (input.toLowerCase().contains("dinner")) {
			deadlineTime = "19:00";
		}
		if (input.toLowerCase().contains("supper")) {
			deadlineTime = "23:00";
		}

		return deadlineTime;
	}

	private String keywordFilter(String input) {
		String result = input;
		if (result.endsWith(" by ")) {
			result = result.substring(0, result.length() - 3);
		}

		if (result.endsWith(" from ")) {
			result = result.substring(0, result.length() - 5);
		}

		if (result.startsWith("From ") || result.startsWith("from ")) {
			result = result.substring(6);
		}

		return result.trim();
	}

	private long getDateDiff(Date date1, Date date2) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return diffInMillies;
	}
}