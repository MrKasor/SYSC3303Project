/**
 * 
 */
package project;

import java.util.ArrayList;

/**
 * @author Ryan
 *
 */
public class Network {
	ArrayList<String> netData = new ArrayList<String>();
	boolean hasSomething = false;
	int n = 1;
	
	public synchronized void transfer(ArrayList<String> data, int destination, int id) {
		while(hasSomething) {
			try{
				wait();
			} catch (InterruptedException e) {}
		}
		
		if(id == n) {
			n = destination;
			netData = data;
			hasSomething = true;
			System.out.println("Network recieved data from "+id+" to be sent to "+n);
		}
		
		notifyAll();
	}
	
	public synchronized ArrayList<String> recieve(int destination) {	
		while(n != destination) {
			try{
				wait();
			} catch (InterruptedException e) {}
		}
		
		System.out.println("Recieving data at "+n);
		hasSomething = false;
		notifyAll();
		return netData;
	}

}
