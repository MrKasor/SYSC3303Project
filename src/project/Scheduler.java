/**
 * 
 */
package project;

import java.util.ArrayList;

/**
 * @author Ryan, Colton
 * 
 * Class Scheduler represents the scheduler of the elevator system.
 *
 */
public class Scheduler implements Runnable{
	private Network netRef;
	private ArrayList<String> data;
	private int id;
	private boolean toFloor = false, hasData = false, go = true;
	
	public Scheduler(int id, Network netRef)
    {
		data = new ArrayList<String>();
    	this.id = id;
		this.netRef = netRef;
    }
	
	public enum SchedulingState {
		Waiting,
		Sending;
	};
	
  
    public void run()
    {
    	SchedulingState state = SchedulingState.Waiting;
    	while(true)
    	{
    		//Waiting for instructions
    		while(state == SchedulingState.Waiting)
    		{
    			System.out.println("Scheduler is waiting for instructions...");
    			//While we are waiting for instructions
    			data = netRef.recieve(id);
    			
    			//Set has data to true and go to the next state
    			hasData = true;
    			state = SchedulingState.Sending;
    		}
    		
    		//Performing instructions
    		while(state == SchedulingState.Sending)
    		{
    			System.out.println("Scheduler sending information...");
    			if(toFloor)
    			{
        			netRef.transfer(data, 1, id);
        			toFloor = false;
    			}
    			//If not being sent to the floor, we send to the elevator.
    			if(!toFloor) 
    			{
    				netRef.transfer(data, 3, id);
					toFloor = true;
					
					String temp = "";
					for(String line: data) {
						temp = temp + line + " ";
					}
					System.out.println("Schedule: " +temp);
    			}
    			
    			//Transition to waiting state and set has data to false
    			hasData = false;
    			state = SchedulingState.Waiting;
    		}
    		
    	}
    }
    
}