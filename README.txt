ASU SER422 Spring 2018 Online B
Kevin Gary

Lab 1
Tony Cotta - tcotta
3/18/18

	This is the submission for lab 1. This is a simple web application that handles a To-Do List.
The base HTML page can be found as index.html, and provides a basic form for submitting post 
requests. The custom attribute that I chose to implement was the name of the person responsible
for the To-Do Item. The handling of get requests is also taken care of by this servlet. 

To get all tasks
http://localhost:8080/lab1_tcotta/tasks

Supported GET parameters
description
custom
days
Example might be http://localhost:8080/lab1_tcotta/tasks?description=wash&custom=john&days=17

	The return of queries like this will return a table containing all of the items that match the 
search string parameters. It should be noted, that for To-Do Items that occur on multiple days,
a table entry will be added for each day in which it occurs. This means that if John washes 
his laundry on days 1 and 2 there will be two of the same entry for John to wash clothes
representing one for each of the days, since they are considered to be independent tasks.

Data Persistence
	Upon initial load of the first serviced request by the servlet, the server will go and read
data stored in the lab1data.txt file. This loads the stored events into memory and can be 
immediately viewed with a get request. However, Subsequent post requests will not be written
to this file. The stored and updated out put can be found in tomcat-{ver.num}-{port}/bin/ToDoList.txt
The reason for this choice is that once the server is live and many users are posting to it,
writing back to the pre-loaded file, while possible, would create a situation where updates
to the webapp would wipe out any existing user entered data upon re-deployment. This separates
the data from the servicer, meaning subsequent re-deployments would not overwrite data. In this 
case that data would have to be manually migrated back into the deployment if it was desired to
keep the existing data. With small modifications, this could be changed. 

WARNING:
	Just because the implementation allows for data preservation, if you re-deploy and make a GET 
or POST request, the stored data will be overwritten. You must re-name the data file found at
tomcat-{ver.num}-{port}/bin/ToDoList.txt before the re-deployment should go live, to prevent
data loss.

Error Response
	I have chosen to utilize a secondary servlet to handle error conditions such as 404, 500 etc.
The actual servlet was not written by me, but found on TutorialsPoint and is labeled here,
and in the servlet file as such. I have integrated this into my web.xml, and it should return
a page that prints out the error status code to the screen, as well as prevents stack traces
from appearing on screen. 