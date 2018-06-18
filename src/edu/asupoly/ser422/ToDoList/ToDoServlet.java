package edu.asupoly.ser422.ToDoList;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.Map;


@SuppressWarnings("serial")
public class ToDoServlet extends HttpServlet {
	
	private static String _filename = null;
	
	public void init(ServletConfig config) throws ServletException {
		// if you forget this your getServletContext() will get a NPE! 
		super.init(config);
		System.out.println("\n\nToDoList Servlet Starting Up");
		
		_filename = config.getInitParameter("ToDoList");
		if (_filename == null || _filename.length() == 0) {
			throw new ServletException();
		}
		System.out.println("Loaded init param ToDoList with value " + _filename);
	}

	// Post method responder
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("\nProcessing POST:");
		// Process Headers
		String referer = request.getContextPath();
		String userAgent = request.getHeader("User-Agent");

		// Process Request Parameters
		String taskName = request.getParameter("taskName");
		String taskDesc = request.getParameter("taskDesc");
		String duration = request.getParameter("duration");
		String custom = request.getParameter("custom");
		
		String monday = request.getParameter("monday");
		String tuesday = request.getParameter("tuesday");
		String wednesday = request.getParameter("wednesday");
		String thursday = request.getParameter("thursday");
		String friday = request.getParameter("friday");
		String saturday = request.getParameter("saturday");
		String sunday = request.getParameter("sunday");
		
		// Perform Processing
		TaskList list = TaskList.getInstance();
		StringBuilder sb = new StringBuilder("<HTML><TITLE>ToDo List</TITLE>\n<BODY ");
		if (userAgent != null && userAgent.contains("AppleWebKit")){   // Change Background based on user agent
			sb.append("bgcolor='gray'>\n");
		}
		else {
			sb.append("bgcolor='FDF5E6'>\n");
		}
		
		if ((taskName.equalsIgnoreCase("") ||     // Check for missing input values
				taskDesc.equalsIgnoreCase("") || 
				duration.equalsIgnoreCase("") || 
				custom.equalsIgnoreCase("") || 
				daysChecked(monday, tuesday, wednesday, thursday, friday, saturday, sunday).equalsIgnoreCase("null"))){
			sb.append("<p>It looks like missed some information on entry. The Todo item you attempted to add has NOT been added to the list of items</p>");
		}
		else {
			StringBuilder dayBuilder = new StringBuilder();
			countDays(monday, tuesday, wednesday, thursday, friday, saturday, sunday, dayBuilder);
			ToDoItem thisItem = new ToDoItem(taskName, taskDesc, dayBuilder.toString(), duration, custom);
			int result = list.tryAdd(thisItem, _filename);
			
			if (result == 0) { sb.append("<p>" + thisItem.getTaskName() + " was updated.</p>");}
			else if (result == 1){ sb.append("<p>" + thisItem.getTaskName() + " added successfully.</p>"); }
			else { sb.append("<p>Something went wrong adding this task</p>"); }
		}
		
		sb.append("<p>The current number of tasks in the data store is " + list.size() + ".</p>");
		sb.append("<a href=" + referer + "><button type='button'>Back</button></a>"); // Add the return button
		sb.append("</BODY></HTML>");
		
		// Assign Response Headers
		response.setContentType("text/html");
		response.setStatus(response.SC_OK);
		
		// Write out Response
		PrintWriter out = response.getWriter();
		out.print(sb.toString());
		
	}
	
	// Get method responder
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException	{
		System.out.println("\nProcessing Get:");
		// Process Headers
		String referer = request.getContextPath();
		String userAgent = request.getHeader("User-Agent");
		String accept = request.getHeader("Accept");

		// Process Request Parameters		
		Map<String, String[]> map = request.getParameterMap();
		String searchDesc[] = new String[10];
		String searchCustom[] = new String[10];
		String searchDays[] = new String[10];
		if (map.containsKey("description")){
			searchDesc = map.get("description");
			searchDesc[0] = searchDesc[0].replace("\"", "");
		}
		else {
			searchDesc[0] = "null";
		}
		if (map.containsKey("custom")){
			searchCustom = map.get("custom");
			searchCustom[0] = searchCustom[0].replace("\"", "");
		}
		else {
			searchCustom[0] = "null";
		}
		if (map.containsKey("days")){
			searchDays = map.get("days");
			searchDays[0] = searchDays[0].replace("\"", "");
		}
		else {
			searchDays[0] = "null";
		}
		
		// Perform Processing
		StringBuilder sb = new StringBuilder();
		
		if (accept.equalsIgnoreCase("text/plain")){
			// Render plain text
			sb.append("ToDo List\n");
			findParams(searchDesc[0], searchCustom[0], searchDays[0], sb);
			response.setContentType("text/plain");
		}
		else {
			// Render html
			sb.append("<HTML><TITLE>ToDo List</TITLE>\n<BODY ");
			if (userAgent != null && userAgent.contains("AppleWebKit")){
				sb.append("bgcolor='gray'>\n");
			}
			else {
				sb.append("bgcolor='FDF5E6'>\n");
			}
			findParamsHTML(searchDesc[0], searchCustom[0], searchDays[0], sb);
			
			sb.append("<a href=" + referer + "><button type='button'>Back</button></a>"); // Add the return button
			sb.append("</BODY></HTML>");
			
			response.setContentType("text/html");
		}
		
		// Assign Response Headers
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Cache-Control", "no-transform");
		response.setStatus(response.SC_OK);
				
		// Write out Response
		PrintWriter out = response.getWriter();
		out.print(sb.toString());
	}
	
	
	/* Checks to see if at least one of the days has been marked
	 * @param mon value of Monday check box
	 * @param tue value of Tuesday check box
	 * @param wed value of Wednesday check box
	 * @param thur value of Thursday check box
	 * @param fri value of Friday check box
	 * @param sat value of Saturday check box
	 * @param sun value of Sunday check box
	 * @return "null" if no day have been checked.
	 */
	private String daysChecked(String mon, String tue, String wed, String thur, String fri, String sat, String sun) {
		String ret = "null";
		if (mon != null ||
				tue != null ||
				wed != null ||
				thur != null ||
				fri != null ||
				sat != null ||
				sun != null){
			
			ret = "somethingElse";
		}		
		return ret;
	}
	
	/* Adds the days that were checked to the list as strings
	 * @param mon value of Monday check box
	 * @param tue value of Tuesday check box
	 * @param wed value of Wednesday check box
	 * @param thur value of Thursday check box
	 * @param fri value of Friday check box
	 * @param sat value of Saturday check box
	 * @param sun value of Sunday check box
	 * @param dayBuilder StringBuilder that turns parameter values into a string
	 */
	private void countDays(String mon, String tue, String wed, String thur, String fri, String sat, String sun, StringBuilder dayBuilder){
		if (mon != null){
			dayBuilder.append("1");
		}
		if (tue != null){
			dayBuilder.append("2");
		}
		if (wed != null){
			dayBuilder.append("3");
		}
		if (thur != null){
			dayBuilder.append("4");
		}
		if (fri != null){
			dayBuilder.append("5");
		}
		if (sat != null){
			dayBuilder.append("6");
		}
		if (sun != null){
			dayBuilder.append("7");
		}
	}
	
	/*
	 * Takes the input parameters and searches through the list of current ToDoItems.
	 * @param desc Substring to search description for
	 * @param custom Substring to search custom field for (Usually a name)
	 * @param days String representation of days to search for.
	 * multiple numbers in the days field indicate which days and represent and OR condition
	 * @param sb StringBuilder used to generate web-view content
	 */
	private void findParamsHTML(String desc, String custom, String days, StringBuilder sb){
		System.out.println("Sending HTML");
		TaskList list = TaskList.getInstance();
		sb.append(list.findParamsHTML(desc, custom, days));
	}
	
	/*
	 * Takes the input parameters and searches through the list of current ToDoItems.
	 * @param desc Substring to search description for
	 * @param custom Substring to search custom field for (Usually a name)
	 * @param days String representation of days to search for.
	 * multiple numbers in the days field indicate which days and represent and OR condition
	 * @return a plain text string representing a table with the results
	 */
	private void findParams(String desc, String custom, String days, StringBuilder sb){
		System.out.println("Sending Pain Text");
		TaskList list = TaskList.getInstance();
		sb.append(list.findParams(desc, custom, days));
	}
	
	
}
