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
	private ElevatorSubsystem sysRef;
	private int destination = 0;
	private int floor = 1;
	private boolean hasPeople = false;
	
	
	/**
	 * Constructor for Elevator. Starts an Elevator thread.
	 * 
	 * @param id
	 * @param netRef
	 */
	public Elevator(int id, ElevatorSubsystem sysRef) {
		this.id=id;
		this.sysRef=sysRef;
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
		String temp = "";
		switch (state) {
		
			case Waiting:
				destination = sysRef.send(id);
				
				if(destination > floor) {//Going up
					temp = id+"|"+floor+"|0|0|0";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
				}
				else {//Going Down
					temp = id+"|"+floor+"|0|0|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
				}
				
				sysRef.updateData(id, temp); //update that elevator is moving
				state = ElevatorState.Moving;
				break;
				
			case Stopped:
				//Sleep while customers are getting on/off, then switch to waiting.
				System.out.println("\nElevator "+id+" is stopped, people are boarding/disembarking.\n");
				
				if(hasPeople) {//Getting off
					temp = id+"|"+floor+"|1|0|0";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
				}
				else {//Getting on
					temp = id+"|"+floor+"|0|0|0";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
				}
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				sysRef.updateData(id, temp); //Update that elevator has stopped
				state = ElevatorState.Waiting;
				break;
				
			case Moving:
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.out.println("Elevator: " +id);
				state = ElevatorState.Stopped;
				break;
		}
	}
	
}
