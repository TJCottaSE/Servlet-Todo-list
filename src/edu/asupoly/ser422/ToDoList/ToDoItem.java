package edu.asupoly.ser422.ToDoList;

public class ToDoItem {
	
	private String taskName;
	private String taskDesc;
	private String days;
	private String duration;
	private String name;
	
	// Default Constructor
	public ToDoItem() {
		taskName = "";
		taskDesc = "";
		days = "";
		duration = "";
		name = "";
	}
	
	/* Constructor with params for a To-Do Item
	 * @param taskName the name of the task
	 * @param taskDesc the task description
	 * @param days an integer representation of the days that the 
	 * item occurs on. Example Monday = 1 Sunday = 7 Monday and Sunday = 17
	 * @param duration a string representation of the estimated duration
	 * @param name the name of the person assigned to the item
	 */
	public ToDoItem(String task, String desc, String days, String duration, String name){
		this.taskName = task;
		this.taskDesc = desc;
		this.days = days;
		this.duration = duration;
		this.name = name;
	}
	
	public String toString() {
		return taskName + "\n" + taskDesc + "\n" + days + "\n" + duration + "\n" + name + "\n";
	}
	
	public String toFormattedString() {
		return taskName + "\t" + taskDesc + "\t" + days + "\t" + duration + "\t" + name;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}