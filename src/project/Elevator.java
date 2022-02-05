package project;

import java.util.ArrayList;

public class Elevator extends Thread{
	private int id;
	private Network netRef;
	private ArrayList<String> data = new ArrayList<String>();
	private boolean hasData = false;
	
	public Elevator(int id, Network netRef) {
		this.id=id;
		this.netRef=netRef;
		start();
	}
	
	public void run() {	
		while(true) {
			if(hasData) {
				netRef.transfer(data, 2, id);
				hasData = false;
				System.out.println("Elevator");
				for(String line: data) {
					System.out.print(line+" ");
				}
				System.out.println("");
			}
			else {
				data = netRef.recieve(id);
				hasData = true;
			}
		} 
	}
	
}
