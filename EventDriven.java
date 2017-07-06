package PcsnProj1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class EventDriven {
	
	static double seed = 1111.0;


	public static void main(String[] args) throws FileNotFoundException{
    
		
		Scanner scan;
        if (args.length > 0) { // if the input file is specified
            File inputFile = new File(args[0]); // read the file
            scan = new Scanner(inputFile); // get a scanner of the input file
        } else { // the input file must be provided
            System.out.println("provide the location of the config file as the first input argument.");
            scan = null;
            System.exit(1);
        }
       
	
		double delta; //production rate of Machine1
		double lamda; //production rate of Machine2
		double mu = 0; //processing rate of a single worker
		
		int m = 0; // number of Servers
		int K = 0; // capacity of the queue
	
		//Read the inputs from the config File
		delta = scan.nextDouble();
		mu = scan.nextDouble();
		m = scan.nextInt();
		K = scan.nextInt();
		int typeArrivalDelta = 1; //type 1 for arrival of component of machine1
		int typeArrivalLamda = 2; //type 2 for arrival of component of machine2 
		int typeDeparture = 0; //type 0 for departure
		double rho = 0.1; //initialization of rho
		
		//For each value of rho until 1
		while(rho <=1){
		lamda = (rho*m*mu); //calculate the production rate of machine 2
	   
	    System.out.println("\n For System Parameters rho = " + rho + " delta(Machine 1 Rate) = " + delta + " lamda(Machine 2 Rate) = " + lamda + " m = " + m + " k = " + K);
	    rho += 0.1;
	    double Rate;
		int noOfComponents = 0; 
		int noOfDepartures = 0;
		
	    int systemSize = 0 ; // components in service + wait Queue
	    int queueSize = 0; // components waiting for service 
	    double sysClock = 0.0; 
	    int noOfServerAvailable = 2; // No of workers available for packaging
		Event currentEvnt ;
		
		double timeStamp = exponentialRv(delta); //Production by machine 1
		EventList.listInsert(timeStamp, typeArrivalDelta); //Arrival of component from machine1
		
		timeStamp = exponentialRv(lamda);//Production by machine 2
		EventList.listInsert(timeStamp, typeArrivalLamda); // Arrival of component from machine2
		
		//Initialization of System parameters
		int iter = 0;
		int totalArrival = 0;
		int totalEntered = 0;
		int blocked = 0;
		double E_N = 0.0;
		double Utilization = 0;

		//For 100000 Departures
		while(noOfDepartures < 100000){
		 
			
			currentEvnt = EventList.listGetEvent(); // get event from the event List
			double prevClock = sysClock; //set the prev clock
			sysClock = currentEvnt.timeStamp; //update the system clock
			
			 //Calculate Utilization
			Utilization = calcUtilization(Utilization,systemSize,sysClock,prevClock);
			
			// If Event type is Arrival from machine 1
			if(currentEvnt.type == typeArrivalDelta){
				
				E_N += (systemSize*(sysClock-prevClock));
			 
				if(systemSize < 2){ // checking if the system size is less than 2 
			 	     ++systemSize ; //incrementing the system size
			 	     ++totalEntered ; //incrementing the arrival
			 	    
			 	    /*checking if the server is available for processing*/
			 	    if(noOfServerAvailable >0 && systemSize>0){
	             		EventList.listInsert(sysClock+exponentialRv(mu), typeDeparture);/*Creating a departure Event if server is available */
				 		--noOfServerAvailable ;// setting server to busy
				 	}
			 	     
			 	    //if server is not available
	             	else{ 
	             		queueSize++ ;//component added to queue for processing
	             	}
			 	 /*Generating Arrival Event*/    
			 	     Rate = delta ; //Machine 1 generates components
	            	 timeStamp = sysClock + exponentialRv(Rate); 
	            	 EventList.listInsert(timeStamp, typeArrivalDelta);// Event added to the list
				}
				
				else{
					 Rate = delta ; 
		             timeStamp = sysClock + exponentialRv(Rate); 
		             EventList.listInsert(timeStamp, typeArrivalDelta);
				}
				
				
		 }
		
		// If Event type is Arrival from Machine 2
	     if(currentEvnt.type == typeArrivalLamda){
		    
		    E_N += (systemSize*(sysClock-prevClock));
			
		    /*checking if the system capacity is full*/
	        if(systemSize < K){
			   ++systemSize ; // updating the system size
			   ++totalEntered ; // updating the total arrival
			   
			   /*checking if the server is available for processing*/
			   if(noOfServerAvailable >0 && systemSize >0){
            		EventList.listInsert(sysClock+exponentialRv(mu), typeDeparture);
			 		--noOfServerAvailable ;
			 	}
			 //if server is not available
            	else{
            		queueSize++ ;//component added to queue for processing
            	}
			   
			   Rate = lamda ; //Machine 2 generates components
          	   timeStamp = sysClock + exponentialRv(Rate); 
          	   EventList.listInsert(timeStamp, typeArrivalLamda);
			   
	        }
	        // If the system capacity is full the event is blocked
	        else{
	        	blocked++;
	        	Rate = lamda ; //Machine 2 generating components 
		        timeStamp = sysClock + exponentialRv(Rate);
		        EventList.listInsert(timeStamp, typeArrivalLamda);// Event added to the list
	        }
	        
	        
	     }				 
			
		
		    //If Event Type is Departure
			if(currentEvnt.type == typeDeparture){
				
				E_N += (systemSize*(sysClock-prevClock));
		    	--systemSize ; // Updating the system size - Decrementing it by 1
		    	++noOfDepartures ; // incrementing the number of departures by 1
		        ++noOfServerAvailable; // making a server available
		       
		        //checking if any component is waiting in the queue for service
		        if(queueSize>0){
		        	
		        	//Checking if the Server is available
                    if(noOfServerAvailable >0){
                    timeStamp = sysClock + exponentialRv(mu); 
			 	    EventList.listInsert(timeStamp, typeDeparture); //Generating departure Event
			         --noOfServerAvailable ; //making the server busy
			         --queueSize; // updating the queue
		            }
		        }
			}
         //  System.out.println("System SIze : " + systemSize + " Total Arrivals : " + (totalArrival) + " Total Entered = " + totalEntered  + " Departures : " + noOfDepartures );
	        
		}

		double AvgN = E_N/sysClock; // calculating the Average Number of Components
		double AvgT = E_N/(totalEntered) ; //calculating the Average Time Spent in system by Components
		double Blocked = (double)blocked /totalEntered; //calculating the blocking probability
		double util = Utilization/sysClock; // calculating the utilization
		/*Display System Parameters*/
		
		System.out.println("\n Program Values ");
		System.out.println("E[N] = " + AvgN );
		System.out.println("Blocking Prob = " + Blocked);
		System.out.println("E[T] = " + AvgT);
		System.out.println("Utilization = " + util);
		calcTheory(lamda,delta,mu,K); // function call to calculate theoretical values
	}	
}		
	public static void calcTheory(double lamda, double delta, double mu ,int K){	
		
		int k = K+1;// number of states
		double p[] = new double[k+1]; //probability of each state
		
		p[0]= 1; // Inital p0 set to 1 for computaion
		p[1] = ((delta+lamda)/mu)*p[0] ; //calculate p1 using formula
		p[2] = ((delta+lamda)/(2*mu))*p[1]; // calculate p2 using formula
		
		/*for every state compute p */
		for(int i=3 ;i<k ;++i){
		  p[i] = p[i-1]* (lamda/(2*mu));
		}  
		  double temp = 1;
		  
		for(int i=1; i<k; ++i){
			 temp += p[i];
		}
		 
		/* Compute the probability of states*/
		
		  p[0] = 1/temp; 
		  p[1] = ((delta+lamda)/mu)*p[0] ;
		  p[2] = ((delta+lamda)/(2*mu))*p[1];
		  
		  for(int i=3 ;i<k ;++i){
			  p[i] = p[i-1]* (lamda/(2*mu));
		  }
		  
		  double AvgN =0; //theoretical value of average number of components
		  double lambdaEff; //theoretical value of lamda effective
		  double AvgT =0; //theoretical value of average time spent in system by components
		  double Blocking;// theoretical value of blocking probability
		  double lambdaEffective = p[0]*(lamda+delta)+p[1]*(lamda+delta);	// compute lamda Effective 
		 
		  for(int i=2; i<k ;++i){
			  lambdaEffective += p[i]*(lamda) ;
		  }
		  
		  for(int i=0; i<=4 ;++i){
		  AvgN += i*p[i];// compute theoretical Expected number of customers
		  }
		  
		  AvgT = AvgN/lambdaEffective; //calculate theoretical value of average time spent in system by components									// Theoretical Time Spent in the system
		  
		  Blocking = (lamda*p[K]*AvgT)/ (AvgN+(lamda*p[K]*AvgT));// calculate theoretical Blocking Probability 
		  
		  double utilization = 0.5*p[1]; //Initialization of Util with p1
				  
		  for(int i=2 ;i<k ;++i){
			  utilization += p[i] ;// calculate utilization
		  }
		  
		  /*Display the theoretical Values */
		  
		  System.out.println("\n Theoretical Calculations : ");
		  System.out.println("E[N] = " + AvgN );
		  System.out.println("Blocking Prob = " + Blocking);
		  System.out.println("E[T] = " + AvgT);
		  System.out.println("Utilization = " + utilization);
			
}
	/*function to generate uniform random variable */
	static double uniformrv(){
	
    int k = 16807;
    int m = 2147483647;
    seed = ((k*seed) % m);
	double r = seed / m;
	return r;      
	}
	
	/*function to generate exponential random variable */
	static double exponentialRv(double rate)
	{
	    double expRV;
	    expRV = ((-1) / rate) * Math.log(uniformrv());
	    return(expRV);
	}
    
	/*function to calculate Utilization*/
	static double calcUtilization(double util,int systemSize ,double sysClock,double prevClock){
		double Utilization = util ;
		if(systemSize == 1){
			Utilization += 0.5 * (sysClock-prevClock);
		}
		else if(systemSize >= 2){
			Utilization += 1 * (sysClock-prevClock);
		}
	    return Utilization;
	}
}

