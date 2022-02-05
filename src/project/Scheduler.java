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
public class Scheduler extends Thread{
	private Network netRef;
	private ArrayList<String> data;
	private int id;
	private boolean toFloor = false, hasData = false, go = true;
	
	public Scheduler(int id, Network netRef) {
		data = new ArrayList<String>();
		this.id = id;
		this.netRef = netRef;
		start();
	}
	
	/**
	 * Method which takes place when the Scheduler thread has started.
	 */
	public void run() {	
		while(true) {
			if(hasData) {
				if(toFloor == false) {
					netRef.transfer(data, 3, id);
					toFloor = true;
					hasData = false;
					
					String temp = "";
					for(String line: data) {
						temp = temp + line + " ";
					}
					System.out.println("Schedule: " +temp);
					
				}else {
					netRef.transfer(data, 1, id);
					toFloor = false;
					hasData = false;
				}
				
			}
			else {
				data = netRef.recieve(id);
				hasData = true;
			}
		} 
	}
}
