package PcsnProj1;

public class Event {

	int type; /* Type of Event 1-Arrival of machine1 comp 
	                           2-Arrival of machine2 comp
	                           0-Departure */
	double timeStamp; // Timestamp of Event
	
	Event(double time, int t){	
		timeStamp = time; // Initialization
		type = t;
	}
}
