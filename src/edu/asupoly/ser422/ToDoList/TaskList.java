package edu.asupoly.ser422.ToDoList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;


/*
 * Singleton Class of a task list to be used with Java Servlets 
 * serving a list of tasks
 */
public final class TaskList {
	private static volatile TaskList instance = null;
	private LinkedList<ToDoItem> list = null;
	private static final String DEFAULT_FILENAME = "ToDoList.txt";
	private final String RESOURCE_FILENAME = System.getProperty("catalina.base") + "/webapps/lab1_tcotta/WEB-INF/classes/resources/lab1data.txt";
	
	// Private Constructor
	private TaskList() {
		list = new LinkedList<ToDoItem>();
		System.out.println("catalina base is: " + System.getProperty("catalina.base"));
		File file = new File(RESOURCE_FILENAME);
		try {
			boolean isNew = file.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null){
				String taskName = str;
				String taskDesc = br.readLine();
				String days = br.readLine();
				String duration = br.readLine();
				String name = br.readLine();
				list.add(new ToDoItem(taskName, taskDesc, days, duration, name));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error creating or reading the file");
		}
	}
	
	/*
	 * Gets a reference to this singleton with lazy instantiation.
	 * @return a reference to the TaskList singleton
	 */
	public static TaskList getInstance(){
		if (instance == null){
			synchronized (TaskList.class){
				if (instance == null){
					instance = new TaskList();
				}
			}
		}
		return instance;
	}
	
	/*
	 * This is the method used to add a To-Do Item to the list AND
	 * used for updating a record in the list. 
	 * @return -1 = Not added or updated, something went wrong, 0 = Updated, 1 = Added
	 */
	public synchronized int tryAdd(ToDoItem item, String _filename){
		int ret = -1;
		boolean updated = false;
		for (int i = 0; i < list.size(); i++){
			ToDoItem listItem = list.get(i);
			if (listItem.getTaskName().equalsIgnoreCase(item.getTaskName())){
				this.update(item, (_filename == null) ? DEFAULT_FILENAME : _filename);
				ret = 0;
				updated = true;
			}
		}
		if (!updated){
			this.add(item, (_filename == null) ? DEFAULT_FILENAME : _filename);
			ret = 1;
		}
		return ret;
	}
	
	/*
	 * Method is used to add a To-Do Item to the list in memory, as well
	 * as saving the addition to the persistent store.
	 * @return true if the item added successfully
	 */
	private synchronized boolean add(ToDoItem item, String _filename){
		System.out.println("Adding Record");
		boolean added = false;
		// Add the item to the in memory list
		list.add(item);
		
		
		// READING FILE IN MAY NOT BE NECESSARY
		// Add the item to the persistent store
		System.out.println("Seeing if file exists");
		File newFile = new File(_filename);
		
		boolean isCreated = false;
		try {
			isCreated = newFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("File was newly created: " + isCreated);		
		
		// Write data out to same file
		try {
			System.out.println("Trying to write out....");
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
			for (int i = 0; i < list.size(); i++){
				ToDoItem thisItem = list.get(i);
				bw.write(thisItem.toString());
			}
			bw.close();
			System.out.println("Write out successful");
			added = true;
		} catch (IOException e){
			e.printStackTrace();
			System.out.println("There was an error trying to write out the file while trying to add.");
		}

		return added;
	}
	
	/*
	 * Method used to update a record in the in memory list AND
	 * updates the persistent store.
	 * @return true if updated successfully
	 */
	private synchronized boolean update(ToDoItem item, String _filename){
		System.out.println("Updating Record");
		boolean updated = false;
		// Update the in memory store
		for (int i = 0; i < list.size(); i++){
			ToDoItem thisItem = list.get(i);
			if (thisItem.getTaskName().equalsIgnoreCase(item.getTaskName())){
				thisItem.setTaskName(item.getTaskName());
				thisItem.setTaskDesc(item.getTaskDesc());
				thisItem.setDays(item.getDays());
				thisItem.setDuration(item.getDuration());
				thisItem.setName(item.getName());
			}
		}
		
		// READING FILE IN MAY NOT BE NECESSARY
		// Update the persistent store
		System.out.println("Seeing if file exists");
		File newFile = new File(_filename);
		
		boolean isCreated = false;
		try {
			isCreated = newFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("File was newly created: " + isCreated);
		
		// Write data out to same file
		try {
			System.out.println("Trying to write out....");
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
			for (int i = 0; i < list.size(); i++){
				ToDoItem thisItem = list.get(i);
				bw.write(thisItem.toString());
			}
			bw.close();
			System.out.println("Write out successful");
			updated = true;
		} catch (IOException e){
			e.printStackTrace();
			System.out.println("There was an error trying to write out the file while trying to add.");
		}
		
		return updated;
	}
	
	/*
	 * Gets the current size of the in memory list of To-Do Items
	 * @return int representation of the size
	 */
	public synchronized int size() {
		return list.size();
	}
	
	/*
	 * Takes the input parameters and searches through the list of current ToDoItems.
	 * @param desc Substring to search description for
	 * @param custom Substring to search custom field for (Usually a name)
	 * @param days String representation of days to search for.
	 * multiple numbers in the days field indicate which days and represent and OR condition
	 * @return an html encoded string representing a table with the results
	 */
	public synchronized String findParamsHTML(String desc, String custom, String days){
		StringBuilder sb = new StringBuilder();
		Iterator<ToDoItem> it = list.iterator();

		// Build table headers
		sb.append("<h3>Results</h3><br>"
				+ "<table>"
					+ "<thead>"
						+ "<th>#</th>"
						+ "<th>Item</th>"
						+ "<th>Description</th>"
						+ "<th>Days</th>"
						+ "<th>Estimate</th>"
						+ "<th>Name</th>"
					+ "</thead>");
		
		// Set null values
		if (desc.equalsIgnoreCase("null")){
			desc = null;
		}
		if (custom.equalsIgnoreCase("null")){
			custom = null;
		}
		if (days.equalsIgnoreCase("null")){
			days = null;
		}
		
		// Get all items
		if (desc == null && custom == null && days == null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				count++;
				sb.append("<tr><td>" + count + "</td>"
						+ "<td>" + item.getTaskName() + "</td>"
						+ "<td>" + item.getTaskDesc() + "</td>"
						+ "<td>" + item.getDays() + "</td>"
						+ "<td>" + item.getDuration() + "</td>"
						+ "<td>" + item.getName() + "</td></tr>");
			}
			sb.append("</tbody></table>");	
		}
		
		// if only description substring searched for
		if (custom == null && days == null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				if (item.getTaskDesc().contains(desc)){
					count++;
					sb.append("<tr><td>" + count + "</td>"
								+ "<td>" + item.getTaskName() + "</td>"
								+ "<td>" + item.getTaskDesc() + "</td>"
								+ "<td>" + item.getDays() + "</td>"
								+ "<td>" + item.getDuration() + "</td>"
								+ "<td>" + item.getName() + "</td></tr>");
				}
			}
			sb.append("</tbody></table>");
		}
		
		// if only custom substring searched for
		if (desc == null && days == null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				if (item.getName().contains(custom)){
					count++;
					sb.append("<tr><td>" + count + "</td>"
								+ "<td>" + item.getTaskName() + "</td>"
								+ "<td>" + item.getTaskDesc() + "</td>"
								+ "<td>" + item.getDays() + "</td>"
								+ "<td>" + item.getDuration() + "</td>"
								+ "<td>" + item.getName() + "</td></tr>");
				}
			}
			sb.append("</tbody></table>");
		}
		// if only days searched for
		if (desc == null && custom == null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				// Handle OR condition
				String daysList[] = days.split("");			
				for (int i = 0; i < daysList.length; i++){
					if (item.getDays().contains(daysList[i])){
						count++;
						sb.append("<tr><td>" + count + "</td>"
									+ "<td>" + item.getTaskName() + "</td>"
									+ "<td>" + item.getTaskDesc() + "</td>"
									+ "<td>" + item.getDays() + "</td>"
									+ "<td>" + item.getDuration() + "</td>"
									+ "<td>" + item.getName() + "</td></tr>");
					}
				}
			}
			sb.append("</tbody></table>");
		}
		
		// if description and custom searched for
		if (days == null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				if (item.getTaskDesc().contains(desc) && item.getName().contains(custom)){
					count++;
					sb.append("<tr><td>" + count + "</td>"
								+ "<td>" + item.getTaskName() + "</td>"
								+ "<td>" + item.getTaskDesc() + "</td>"
								+ "<td>" + item.getDays() + "</td>"
								+ "<td>" + item.getDuration() + "</td>"
								+ "<td>" + item.getName() + "</td></tr>");
				}
			}
			sb.append("</tbody></table>");
		}
		// if description and days searched for
		if (custom == null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				// Handle OR condition
				String daysList[] = days.split("");			
				for (int i = 0; i < daysList.length; i++){
					if (item.getTaskDesc().contains(desc) && item.getDays().contains(daysList[i])){
						count++;
						sb.append("<tr><td>" + count + "</td>"
								+ "<td>" + item.getTaskName() + "</td>"
								+ "<td>" + item.getTaskDesc() + "</td>"
								+ "<td>" + item.getDays() + "</td>"
								+ "<td>" + item.getDuration() + "</td>"
								+ "<td>" + item.getName() + "</td></tr>");
					}
				}
			}
			sb.append("</tbody></table>");
		}
		// if custom and days searched for
		if (desc == null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				// Handle OR condition
				String daysList[] = days.split("");			
				for (int i = 0; i < daysList.length; i++){
					if (item.getName().contains(custom) && item.getDays().contains(daysList[i])){
						count++;
						sb.append("<tr><td>" + count + "</td>"
								+ "<td>" + item.getTaskName() + "</td>"
								+ "<td>" + item.getTaskDesc() + "</td>"
								+ "<td>" + item.getDays() + "</td>"
								+ "<td>" + item.getDuration() + "</td>"
								+ "<td>" + item.getName() + "</td></tr>");
					}
				}
			}
			sb.append("</tbody></table>");
		}
		
		// if description, custom, and days searched for
		if (desc != null && custom != null && days != null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				// Handle OR condition
				String daysList[] = days.split("");			
				for (int i = 0; i < daysList.length; i++){
					if (item.getTaskDesc().contains(desc) && item.getName().contains(custom) && item.getDays().contains(daysList[i])){
						count++;
						sb.append("<tr><td>" + count + "</td>"
								+ "<td>" + item.getTaskName() + "</td>"
								+ "<td>" + item.getTaskDesc() + "</td>"
								+ "<td>" + item.getDays() + "</td>"
								+ "<td>" + item.getDuration() + "</td>"
								+ "<td>" + item.getName() + "</td></tr>");
					}
				}
			}
			sb.append("</tbody></table>");
		}
		sb.append("<br>");
		return sb.toString();
	}
	/*
	 * Takes the input parameters and searches through the list of current ToDoItems.
	 * @param desc Substring to search description for
	 * @param custom Substring to search custom field for (Usually a name)
	 * @param days String representation of days to search for.
	 * multiple numbers in the days field indicate which days and represent and OR condition
	 * @return a plain text string representing a table with the results
	 */
	public synchronized String findParams(String desc, String custom, String days){
		Iterator<ToDoItem> it = list.iterator();
		StringBuilder sb = new StringBuilder();
		// Build table headers
		sb.append("Results\n\n"
						+ "#\t"
						+ "Item\t"
						+ "Description\t"
						+ "Days\t"
						+ "Estimate\t"
						+ "Name\n");
		
		// Set null values
		if (desc.equalsIgnoreCase("null")){
			desc = null;
		}
		if (custom.equalsIgnoreCase("null")){
			custom = null;
		}
		if (days.equalsIgnoreCase("null")){
			days = null;
		}
		
		// Get all items
		if (desc == null && custom == null && days == null){
			int count = 0;
			while (it.hasNext()){
				ToDoItem item = it.next();
				count++;
				sb.append(count + "\t"
						+ item.getTaskName() + "\t"
						+ item.getTaskDesc() + "\t"
						+ item.getDays() + "\t"
						+ item.getDuration() + "\t"
						+ item.getName() + "\n");
			}	
		}
		
		// if only description substring searched for
		if (custom == null && days == null){
			int count = 0;
			while (it.hasNext()){
				ToDoItem item = it.next();
				if (item.getTaskDesc().contains(desc)){
					count++;
					sb.append(count + "\t"
							+ item.getTaskName() + "\t"
							+ item.getTaskDesc() + "\t"
							+ item.getDays() + "\t"
							+ item.getDuration() + "\t"
							+ item.getName() + "\n");
				}	
			}
		}
		
		// if only custom substring searched for
		if (desc == null && days == null){
			int count = 0;
			sb.append("<tbody>");
			while (it.hasNext()){
				ToDoItem item = it.next();
				if (item.getName().contains(custom)){
					count++;
					sb.append(count + "\t"
							+ item.getTaskName() + "\t"
							+ item.getTaskDesc() + "\t"
							+ item.getDays() + "\t"
							+ item.getDuration() + "\t"
							+ item.getName() + "\n");
				}	
			}
		}
		// if only days searched for
		if (desc == null && custom == null){
			int count = 0;
			while (it.hasNext()){
				ToDoItem item = it.next();
				// Handle OR condition
				String daysList[] = days.split("");			
				for (int i = 0; i < daysList.length; i++){
					if (item.getDays().contains(daysList[i])){
						count++;
						sb.append(count + "\t"
								+ item.getTaskName() + "\t"
								+ item.getTaskDesc() + "\t"
								+ item.getDays() + "\t"
								+ item.getDuration() + "\t"
								+ item.getName() + "\n");
					}	
				}
			}
		}
		
		// if description and custom searched for
		if (days == null){
			int count = 0;
			while (it.hasNext()){
				ToDoItem item = it.next();
				if (item.getTaskDesc().contains(desc) && item.getName().contains(custom)){
					count++;
					sb.append(count + "\t"
							+ item.getTaskName() + "\t"
							+ item.getTaskDesc() + "\t"
							+ item.getDays() + "\t"
							+ item.getDuration() + "\t"
							+ item.getName() + "\n");
				}	
			}
		}
		// if description and days searched for
		if (custom == null){
			int count = 0;
			while (it.hasNext()){
				ToDoItem item = it.next();
				// Handle OR condition
				String daysList[] = days.split("");			
				for (int i = 0; i < daysList.length; i++){
					if (item.getTaskDesc().contains(desc) && item.getDays().contains(daysList[i])){
						count++;
						sb.append(count + "\t"
								+ item.getTaskName() + "\t"
								+ item.getTaskDesc() + "\t"
								+ item.getDays() + "\t"
								+ item.getDuration() + "\t"
								+ item.getName() + "\n");
					}	
				}
			}
		}
		// if custom and days searched for
		if (desc == null){
			int count = 0;
			while (it.hasNext()){
				ToDoItem item = it.next();
				// Handle OR condition
				String daysList[] = days.split("");			
				for (int i = 0; i < daysList.length; i++){
					if (item.getName().contains(custom) && item.getDays().contains(daysList[i])){
						count++;
						sb.append(count + "\t"
								+ item.getTaskName() + "\t"
								+ item.getTaskDesc() + "\t"
								+ item.getDays() + "\t"
								+ item.getDuration() + "\t"
								+ item.getName() + "\n");
					}	
				}
			}
		}
		
		// if description, custom, and days searched for
		if (desc != null && custom != null && days != null){
			int count = 0;
			while (it.hasNext()){
				ToDoItem item = it.next();
				// Handle OR condition
				String daysList[] = days.split("");			
				for (int i = 0; i < daysList.length; i++){
					if (item.getTaskDesc().contains(desc) && item.getName().contains(custom) && item.getDays().contains(daysList[i])){
						count++;
						sb.append(count + "\t"
								+ item.getTaskName() + "\t"
								+ item.getTaskDesc() + "\t"
								+ item.getDays() + "\t"
								+ item.getDuration() + "\t"
								+ item.getName() + "\n");
					}	
				}
			}
		}
		return sb.toString();
	}
}
