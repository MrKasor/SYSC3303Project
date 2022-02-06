package project;

import java.util.ArrayList;


/**
 * @author Colton, Ryan
 * 
 * Class Elevator represents a single elevator inside a building.
 *
 */
public class Elevator extends Thread{
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
		start();
	}
	
	/**
	 * Method which takes place when an Elevator thread has started.
	 */
	public void run() {	
		while(true) {
			if(hasData) {
				netRef.transfer(data, 2, id);
				hasData = false;
				String temp = "";
				for(String line: data) {
					temp = temp + line + " ";
				}
				System.out.println("Elevator: " +temp);
			}
			else {
				data = netRef.recieve(id);
				hasData = true;
			}
		} 
	}
	
}
