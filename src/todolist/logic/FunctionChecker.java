//@@author A0130620B
package todolist.logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import todolist.model.InputException;
import todolist.model.SearchCommand;
import todolist.model.Task;
import todolist.storage.DataBase;

public class FunctionChecker {
	private Logic logic;
	private DataBase dataBase;

	protected FunctionChecker(Logic logic, DataBase dataBase) {
		this.logic = logic;
		this.dataBase = dataBase;
	}

	protected InputException addTaskChecker(String title) {
		title = title.trim();
		if (!title.equals("")) {
			if (noRepeat(title)) {
				return new InputException();
			} else {
				return new InputException("ADD", "REPEAT TITLE");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}
	}

	protected InputException addEventChecker(String title, String startDate, String startTime, String quantity,
			String timeUnit) {
		title = title.trim();
		if (!title.equals("")) {
			if (noRepeat(title)) {
				if (validFuzzyDate(startDate)) {
					if (validTime(startTime)) {
						if (validQuantity(quantity)) {
							if (validUnit(timeUnit)) {
								return new InputException();
							} else {
								return new InputException("ADD EVENT", "INVALID TIME UNIT");
							}
						} else {
							return new InputException("ADD EVENT", "INVALID QUANTITY");
						}
					} else {
						return new InputException("ADD EVENT", "INVALID START TIME");
					}
				} else {
					return new InputException("ADD EVENT", "INVALID START DATE");
				}
			} else {
				return new InputException("ADD", "REPEAT TITLE");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}

	}

	protected InputException addEventLessChecker(String title, String fuzzyTime, String quantity, String timeUnit) {
		title = title.trim();
		if (!title.equals("")) {
			if (noRepeat(title)) {
				if (validFuzzyTime(fuzzyTime)) {
					if (validQuantity(quantity)) {
						if (validUnit(timeUnit)) {
							return new InputException();
						} else {
							return new InputException("ADD EVENT", "INVALID TIME UNIT");
						}
					} else {
						return new InputException("ADD EVENT", "INVALID QUANTITY");
					}
				} else {
					return new InputException("ADD EVENT", "INVALID START TIME");
				}
			} else {
				return new InputException("ADD EVENT", "INVLAID TIME");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}

	}

	protected InputException addDeadlineChecker(String title, String endDate, String endTime) {
		title = title.trim();
		if (!title.equals("")) {
			if (noRepeat(title)) {
				if (validFuzzyDate(endDate)) {
					if (validTime(endTime)) {
						return new InputException();
					} else {
						return new InputException("ADD DEADLINE", "INVALID END TIME");
					}
				} else {
					return new InputException("ADD DEADLINE", "INVLAID END DATE");
				}
			} else {
				return new InputException("ADD", "REPEAT TITLE");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}
	}

	protected InputException addDeadlineLessChecker(String title, String fuzzyTime) {
		title = title.trim();
		if (!title.equals("")) {
			if (noRepeat(title)) {
				if (validFuzzyTime(fuzzyTime)) {
					return new InputException();
				} else {
					return new InputException("ADD DEADLINE", "INVALID END TIME");
				}
			} else {
				return new InputException("ADD", "REPEAT TITLE");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}
	}

	protected InputException addRecurringEventChecker(String interval, String title, String startDate, String startTime,
			String quantity, String timeUnit) {
		title = title.trim();
		if (!title.equals("")) {
			if (validInterval(interval)) {
				return addEventChecker(title, startDate, startTime, quantity, timeUnit);
			} else {
				return new InputException("ADD RECURRING EVENT", "INVALID INTERVAL");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}
	}

	protected InputException addRecurringEventLessChecker(String interval, String title, String fuzzyTime,
			String quantity, String timeUnit) {
		title = title.trim();
		if (!title.equals("")) {
			if (validInterval(interval)) {
				return addEventLessChecker(title, fuzzyTime, quantity, timeUnit);
			} else {
				return new InputException("ADD RECURRING EVENT", "INVALID INTERVAL");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}
	}

	protected InputException addRecurringDeadlineChecker(String interval, String title, String endDate,
			String endTime) {
		title = title.trim();
		if (!title.equals("")) {
			if (validInterval(interval)) {
				return addDeadlineChecker(title, endDate, endTime);
			} else {
				return new InputException("ADD RECURRING DEADLINE", "INVALID INTERVAL");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}
	}

	protected InputException addRecurringDeadlineLessChecker(String interval, String title, String fuzzyTime) {
		title = title.trim();
		if (!title.equals("")) {
			if (validInterval(interval)) {
				return addDeadlineLessChecker(title, fuzzyTime);
			} else {
				return new InputException("ADD RECURRING DEADLINE", "INVALID INTERVAL");
			}
		} else {
			return new InputException("ADD", "EMPTY TITLE");
		}
	}

	protected InputException remindBefChecker(String title, String quantity, String timeUnit) {
		title = title.trim();
		if (!noRepeat(title)) {
			if (validQuantity(quantity)) {
				if (validUnit(timeUnit)) {
					if (isFloating(title)) {
						return new InputException("REMIND BEF", "FLOATING TASK");
					} else {
						return new InputException();
					}
				} else {
					return new InputException("REMIND BEF", "INVALID TIME UNIT");
				}
			} else {
				return new InputException("REMIND BEF", "INVALID QUANTITY");
			}
		} else {
			return new InputException("REMIND BEF", "TASK NOT EXIST");
		}
	}

	protected InputException addRemindBefChecker(String quantity, String timeUnit, String[] arg) {
		if (validQuantity(quantity)) {
			if (validUnit(timeUnit)) {
				String type = arg[0];
				switch (type) {
				case "event":
					return addEventChecker(arg[1], arg[2], arg[3], arg[4], arg[5]);
				case "deadline":
					return addDeadlineChecker(arg[1], arg[2], arg[3]);
				case "task":
					return addTaskChecker(arg[1]);
				default:
					return new InputException("ADD REMIND BEF", "INVALID TYPE");
				}
			} else {
				return new InputException("ADD REMIND BEF", "INVALID TIME UNIT");
			}
		} else {
			return new InputException("ADD REMIND BEF", "INVALID QUANTITY");
		}
	}

	protected InputException remindChecker(String title) {
		if (!noRepeat(title)) {
			if (isFloating(title)) {
				return new InputException("REMIND", "FLOATING TASK");
			} else {
				return new InputException();
			}
		} else {
			return new InputException("REMIND", "TASK NOT EXIST");
		}
	}

	protected InputException forwardChecker(String title, String quantity, String timeUnit) {
		if (!noRepeat(title)) {
			if (validQuantity(quantity)) {
				if (validUnit(timeUnit)) {
					if (isFloating(title)) {
						return new InputException("FORWARD", "FLOATING TASK");
					} else {
						return new InputException();
					}
				} else {
					return new InputException("FORWARD", "INVALID TIME UNIT");
				}
			} else {
				return new InputException("FORWARD", "INVALID QUANTITY");
			}
		} else {
			return new InputException("FORWARD", "NOT EXIST");
		}
	}

	protected InputException postponeChecker(String title, String quantity, String timeUnit) {
		if (!noRepeat(title)) {
			if (validQuantity(quantity)) {
				if (validUnit(timeUnit)) {
					if (isFloating(title)) {
						return new InputException("POSTPONE", "FLOATING TASK");
					} else {
						return new InputException();
					}
				} else {
					return new InputException("POSTPONE", "INVALID TIME UNIT");
				}
			} else {
				return new InputException("POSTPONE", "INVALID QUANTITY");
			}
		} else {
			return new InputException("POSTPONE", "NOT EXIST");
		}
	}

	protected InputException redoChecker(String redostep) {
		if (isInteger(redostep)) {
			if (Integer.parseInt(redostep) > 0) {
				if (logic.getSnapshot()[logic.checkStep() + Integer.parseInt(redostep)] == null) {
					return new InputException("REDO", "NO ACTION TO REDO");
				} else {
					return new InputException();
				}
			} else {
				return new InputException("REDO", "STEP NOT POSITIVE");
			}
		} else {
			return new InputException("REDO", "AUGUMENT NOT INTEGER");
		}
	}

	protected InputException undoChecker(String undostep) {
		if (isInteger(undostep)) {
			if (Integer.parseInt(undostep) > 0) {
				if (logic.checkStep() - Integer.parseInt(undostep) < 0) {
					return new InputException("UNDO", "NO ACTION TO UNDO");
				} else {
					return new InputException();
				}
			} else {
				return new InputException("UNDO", "STEP NOT POSITIVE");
			}
		} else {
			return new InputException("UNDO", "AUGUMENT NOT INTEGER");
		}
	}

	protected InputException undoneChecker(String title) {
		if (!noRepeat(title)) {
			return new InputException();
		} else {
			return new InputException("UNDONE", "NOT EXIST");
		}
	}

	protected InputException removeRemindChecker(String title) {
		if (!noRepeat(title)) {
			return new InputException();
		} else {
			return new InputException("REMOVE REMIND", "NOT EXIST");
		}
	}

	protected InputException doneChecker(String title) {
		if (!noRepeat(title)) {
			return new InputException();
		} else {
			return new InputException("DONE", "NOT EXIST");
		}
	}

	protected InputException openChecker(String path) {
		return new InputException();
	}

	protected InputException saveChecker(String path) {
		return new InputException();
	}

	protected InputException invalidChecker() {
		return new InputException("PARSER", "INVALID INPUT");
	}

	protected InputException tabChecker(String workplace) {
		switch (workplace) {
		case "all":
			return new InputException();
		case "expired":
			return new InputException();
		case "today":
			return new InputException();
		case "week":
			return new InputException();
		case "done":
			return new InputException();
		case "options":
			return new InputException();
		case "help":
			return new InputException();
		default:
			return new InputException("TAB", "WORDPLACE NOT EXIST");
		}
	}

	protected InputException setRecurringChecker(String title, Boolean status, String interval) {
		if (!noRepeat(title)) {
			if ((status && validInterval(interval)) || (!status)) {
				if (isFloating(title)) {
					return new InputException("SET RECURRING", "FLOATING TASK");
				} else {
					return new InputException();
				}
			} else {
				return new InputException("SET RECURRING", "INVALID INTERVAL");
			}
		} else {
			return new InputException("SET RECURRING", "TASK NOT EXIST");
		}
	}

	protected InputException labelChecker(String title, String category) {
		if (!noRepeat(title)) {
			return new InputException();
		} else {
			return new InputException("LABEL", "TASK NOT EXIST");
		}
	}

	protected InputException sortChecker(String fieldName, String order) {
		if (validOrder(order)) {
			switch (fieldName) {
			case "title":
				return new InputException();
			case "category":
				return new InputException();
			case "start":
				return new InputException();
			case "end":
				return new InputException();
			default:
				return new InputException("SORT", "INVALID FIELDNAME");
			}
		} else {
			return new InputException("SORT", "INVALID ORDER");
		}
	}

	protected InputException filterChecker(String[] arg) {
		return new InputException();
	}

	protected InputException searchChecker(String[] arg) {
		return new InputException();
	}

	protected InputException editChecker(String title, String fieldName, String newValue) {
		newValue = newValue.trim();
		if (!noRepeat(title)) {
			switch (fieldName) {
			case "title":
				return addTaskChecker(newValue);
			case "start-time":
				if(validDateTime(newValue)) {
					return new InputException();
				} else {
					return new InputException("EDIT", "WRONG DATE TIME FORMAT");
				}	
			case "end-time":
				if(validDateTime(newValue)) {
					return new InputException();
				} else {
					return new InputException("EDIT", "WRONG DATE TIME FORMAT");
				}
			default:
				return new InputException("EDIT", "FIELD NOT EXIST");
			}
		} else {
			return new InputException("EDIT", "TASK NOT EXIST");
		}
	}

	protected InputException deleteChecker(String title) {
		if (!noRepeat(title)) {
			return new InputException();
		} else {
			return new InputException("DELETE", "TASK NOT EXIST");
		}
	}

	protected InputException resetChecker() {
		return new InputException();
	}

	protected InputException exitChecker(String[] arg) {
		return new InputException();
	}

	protected InputException addRemindChecker(String[] arg) {
		String type = arg[0];
		switch (type) {
		case "event":
			return addEventChecker(arg[1], arg[2], arg[3], arg[4], arg[5]);
		case "deadline":
			return addDeadlineChecker(arg[1], arg[2], arg[3]);
		case "task":
			return addTaskChecker(arg[1]);
		default:
			return new InputException("ADD REMIND", "INVALID TYPE");
		}
	}

	protected InputException indexChecker(String type, String[] arg) {
		String temp[] = arg[0].split(",");
		int[] index = new int[temp.length];
		Boolean flag = true;

		for (int i = 0; i < temp.length; i++) {
			if (isInteger(temp[i])) {
				index[i] = Integer.parseInt(temp[i]);
				for(int j = 0; j < i; j++) {
					if(index[j] == index[i]) {
						return new InputException("INDEX", "REPEAT");
					}
				}
			} else {
				flag = false;
			}
		}

		
//		System.out.println(Arrays.toString(index));		
		
		if (flag) {
			return checkInView(index, type, arg);
		} else {
			return typeCaseSwitcher(type, arg[0], arg);
		}
	}

	private InputException checkInView(int[] index, String type, String[] arg) {
		for (int i = 0; i < index.length; i++) {
			Task task = logic.getMainApp().getTaskAt(index[i]);
			if (task == null) {
				return new InputException(type, "TASK NOT EXIST");
			} else {
				String taskname = logic.getMainApp().getTaskAt(index[i]).getName().getName();
				
//				System.out.println(taskname);
				
				InputException tempException = typeCaseSwitcher(type, taskname, arg);
				if (!tempException.getCorrectness()) {
					return tempException;
				}
			}
		}
		return new InputException();
	}

	private InputException typeCaseSwitcher(String type, String taskname, String[] arg) {
		switch (type) {
		case "EDIT":
			return editChecker(taskname, arg[1], arg[2]);
		case "DELETE":
			return deleteChecker(taskname);
		case "LABEL":
			return labelChecker(taskname, arg[1]);
		case "SET-RECURRING":
			return setRecurringChecker(taskname, true, arg[1]);
		case "REMOVE-RECURRING":
			return setRecurringChecker(taskname, false, null);
		case "REMOVE-REMIND":
			return removeRemindChecker(taskname);
		case "POSTPONE":
			return postponeChecker(taskname, arg[1], arg[2]);
		case "FORWARD":
			return forwardChecker(taskname, arg[1], arg[2]);
		case "REMIND":
			return remindChecker(taskname);
		case "REMIND-BEF":
			return remindBefChecker(taskname, arg[1], arg[2]);
		case "DONE":
			return doneChecker(taskname);
		case "UNDONE":
			return undoneChecker(taskname);
		default:
			return new InputException("UNKNOWN", "UNKNOWN");
		}
	}

	private Boolean noRepeat(String title) {
		ArrayList<Task> tempTaskList = dataBase.retrieve(new SearchCommand("NAME", title));
		if (tempTaskList.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private Boolean validFuzzyDate(String fuzzyDate) {
		int count = fuzzyDate.length() - fuzzyDate.replace("-", "").length();
		if (count == 1) {
			return validFuzzyDateTwo(fuzzyDate);
		} else {
			if (count == 2) {
				return validFuzzyDateThree(fuzzyDate);
			}
			return false;
		}
	}

	private Boolean validFuzzyDateThree(String fuzzyDate) {
//		System.out.println(fuzzyDate);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");		
		try {
			@SuppressWarnings("unused")
			LocalDateTime newFuzzyDate = LocalDateTime.parse(fuzzyDate + " 00:00", formatter);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Boolean validFuzzyDateTwo(String fuzzyDate) {
//		System.out.println(fuzzyDate);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");		
		try {
			@SuppressWarnings("unused")
			LocalDateTime newFuzzyDate = LocalDateTime.parse("2000-"+fuzzyDate+" 00:00", formatter);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private Boolean validDateTime(String dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");		
		try {
			@SuppressWarnings("unused")
			LocalDateTime newDateTime = LocalDateTime.parse(dateTime, formatter);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Boolean validFuzzyTime(String fuzzyTime) {
		if (fuzzyTime.contains("-")) {
			return validFuzzyTime(fuzzyTime);
		} else {
			if (fuzzyTime.contains(":")) {
				return validTime(fuzzyTime);
			} else {
				return false;
			}
		}
	}

	private Boolean validTime(String time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");		
		try {
			@SuppressWarnings("unused")
			LocalDateTime newTime = LocalDateTime.parse("2000-01-01 " + time, formatter);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Boolean validInterval(String interval) {
		String temp[] = interval.split("-");
		String length = temp[0];
		String unit = temp[1];
		if (isInteger(length) && Integer.parseInt(length) > 0) {
			return validUnit(unit);
		} else {
			return false;
		}
	}

	private boolean validUnit(String unit) {
		switch (unit) {
		case "day":
			return true;
		case "hour":
			return true;
		case "minute":
			return true;
		case "week":
			return true;
		case "month":
			return true;
		case "year":
			return true;
		default:
			return false;
		}
	}

	private Boolean validQuantity(String quantity) {
		if (isInteger(quantity) && Integer.parseInt(quantity) > 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isInteger(String s) {
		try {
			@SuppressWarnings("unused")
			int i = Integer.parseInt(s);
			return true;
		} catch (NumberFormatException er) {
			return false;
		}
	}

	private boolean isFloating(String title) {
		Task tempTask = dataBase.retrieve(new SearchCommand("NAME", title)).get(0);
		if (tempTask.getEndTime() == null) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validOrder(String order) {
		if (order.equalsIgnoreCase("ascending") || order.equalsIgnoreCase("descending")) {
			return true;
		} else {
			return false;
		}
	}
}
