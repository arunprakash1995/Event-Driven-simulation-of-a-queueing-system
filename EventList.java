package PcsnProj1;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class EventList {

	public static LinkedList<Event> eventList = new LinkedList<Event>();
	
	public static void listInsert( double timeStamp, int type){
		
		
	//	System.out.println(timeStamp + "  " + type);
		int eventAdded = 0; // to indicate if event is added to list
		Event evnt = new Event(timeStamp, type); //creating a Event
		
		if(eventList.isEmpty()){ // if the event list is empty
			eventList.add(evnt);
			//System.out.println("Arrival Event Added to List");
		}
		else // Insert event in the correct chronological position
		{
			int iterator = 0;
			while(iterator < eventList.size()){
				
				Event temp = eventList.get(iterator);
				if(temp.timeStamp < evnt.timeStamp){ //if arrived event has greater time stamp
					++iterator ;
				}
				else{
					eventList.add(iterator, evnt); //if current event has smaller timestamp it is added to the list
					eventAdded = 1; // set the event added status to success
					break;
				}
			}
		    if(eventAdded != 1) // checking if the event the event is added
		    	eventList.addLast(evnt); /* the event has larger timestamps than 
		    	                         all the available events in the list*/
		      //  System.out.println("Arrival Event Added to List");
		    
		}
		
	}
	
	public static Event listGetEvent(){
		
		if(eventList.isEmpty()) // checking if the list is empty
			return null;
		else
		{
			Event evnt = eventList.getFirst(); //Remove the element with the smallest timeStamp
			eventList.removeFirst();
			return evnt; //return event
		}
		
	}
	
	
}
