package project;

import java.util.ArrayList;


/**
 * @author Colton, Ryan
 * 
 * Class Elevator represents a single elevator inside a building.
 *
 */
public class Elevator implements Runnable{
	private int id;
	private Network netRef;
	private ArrayList<String> data = new ArrayList<String>();
	private boolean hasData = false;
	
	
	/**
	 * Constructor for Elevator. Starts an Elevator thread.
	 * 
	 * @param id
	 * @param netRef
	 */
	public Elevator(int id, Network netRef) {
		this.id=id;
		this.netRef=netRef;
	}
	
	public enum ElevatorState
	{
		Waiting,
		Stopped,
		Moving;
	}
	ElevatorState state = ElevatorState.Waiting;
	
	
	public void run()
	{
		while(true)
		{
			while(state == ElevatorState.Waiting)
			{
				System.out.println("\nElevator is waiting for information...\n");
				data = netRef.recieve(id);
				hasData = true;
				state = ElevatorState.Moving;
			}
			while(state == ElevatorState.Stopped)
			{
				//Sleep while customers are boarding/disembarking, then switch to waiting.
				System.out.println("\nElevator is stopped, people are boarding/disembarking...\n");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				state = ElevatorState.Waiting;
			}
			while(state == ElevatorState.Moving)
			{
				if(hasData) 
				{
					System.out.println("\nElevator is moving...\n");
					netRef.transfer(data, 2, id);
					hasData = false;
					String temp = "";
					for(String line: data) 
					{
						temp = temp + line + " ";
					}
					System.out.println("Elevator: " +temp);
				}
				state = ElevatorState.Stopped;
			}
		}
	}
	
}
