/**
 * 
 */
package project;

/**
 * @author Ryan, Colton
 * 
 * Class Test is used to verify that data is being
 * transferred and received by the various classes.
 *
 */
public class Test {

	/**
	 * Creates threads of all the Classes, which start() 
	 * when their constructor is called.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Network net = new Network();
		net.createHashTable();
		Thread floor1 = new Thread(new Floor(1, net), "Floor Thread");
		Thread Sche1 = new Thread(new Scheduler(2, net), "Scheduler Thread");
		Thread ele1 = new Thread(new Elevator(3, net), "Elevator Thread");
		
		floor1.start();
		Sche1.start();
		ele1.start();
	}

}
