package project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class GUI extends JFrame{
	private DatagramSocket socket;
	private DatagramPacket receivePacket, sendPacket;
	private PacketHelper helper = new PacketHelper();
	private JPanel contentPane;
	private List<JTextPane> eleFloor = new ArrayList<JTextPane>();
	private List<JTextPane> eleText = new ArrayList<JTextPane>();
	private List<JTextPane> floorDirection = new ArrayList<JTextPane>();
	private List<JPanel> elePanel = new ArrayList<JPanel>();
	private List<JPanel> floorLamp = new ArrayList<JPanel>();
	private int elePort;
	private JTextPane scheduler;

	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		GUI frame = new GUI();
		frame.setVisible(true);
		frame.setTitle("Elevator System - Group 8");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
					frame.updateEle(1);
					frame.repaint();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public GUI() throws IOException {
		Config config = new Config();
		
		elePort = config.getIntProperty("elePort");
		
		//Socket
		try {
			socket = new DatagramSocket(elePort);
		} catch(SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//INIT
		int width = config.getIntProperty("eleGUI") * (config.getIntProperty("numEle")) + 120;
		int height = config.getIntProperty("floorGUI") * config.getIntProperty("numFloors") + 300;
		int eleStart = (config.getIntProperty("floorGUI") * config.getIntProperty("numFloors")) - 15;
		int eleLocX = 150;
		int floorLocY = 0;
		
		//PANE INIT
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, width, height);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//INIT Elevator Image at 180
		eleLocX = 180;
		for(int i = 0; i < config.getIntProperty("numEle"); i++) {
			elePanel.add(new JPanel());
			elePanel.get(i).setBounds(eleLocX + (config.getIntProperty("eleGUI") * i ), eleStart, 25, 25);
			elePanel.get(i).setBackground(Color.BLACK);
			contentPane.add(elePanel.get(i));
			
		}
		
		//INIT Floors at 0
		floorLocY = 0;
		for(int i = 0; i < config.getIntProperty("numFloors"); i++) {
			floorLamp.add(new JPanel());
			floorLamp.get(i).setBounds(5, floorLocY + (config.getIntProperty("floorGUI") * i ) + 15, 20, 20);
			floorLamp.get(i).setBackground(Color.WHITE);
			contentPane.add(floorLamp.get(i));
			
			floorDirection.add(new JTextPane());
			floorDirection.get(i).setBounds(30, floorLocY + (config.getIntProperty("floorGUI") * i )+ 15, 40, 20);
			floorDirection.get(i).setText("");
			floorDirection.get(i).setEditable(false);
			contentPane.add(floorDirection.get(i));
			
			JLabel lblNewLabel = new JLabel("Floor "+(config.getIntProperty("numFloors")-i));
			lblNewLabel.setBounds(75, floorLocY + (config.getIntProperty("floorGUI") * i ), 50, 50);
			contentPane.add(lblNewLabel);
		}
		
		//INIT Elevator Labels at 160
		eleLocX = 160;
		for(int i = 0; i < config.getIntProperty("numEle"); i++) {
			JLabel lblNewLabel_1 = new JLabel("Elevator "+(i+1));
			lblNewLabel_1.setBounds(eleLocX + (config.getIntProperty("eleGUI") * i ), config.getIntProperty("floorGUI") * config.getIntProperty("numFloors"), 80, 50);
			contentPane.add(lblNewLabel_1);
		}
		
		//INIT Elevator Text Panes at 100
		eleLocX = 100;
		for(int i = 0; i < config.getIntProperty("numEle"); i++) {
			eleText.add(new JTextPane());
			eleText.get(i).setBounds(eleLocX + (config.getIntProperty("eleGUI") * i ), (height - 220), 180, 60);
			eleText.get(i).setText("Elevator "+(i+1)+" is waiting...");
			eleText.get(i).setEditable(false);
			contentPane.add(eleText.get(i));
			
			eleFloor.add(new JTextPane());
			eleFloor.get(i).setBounds(eleLocX + (config.getIntProperty("eleGUI") * i ), (height - 250), 180, 20);
			eleFloor.get(i).setText("Floor 1");
			eleFloor.get(i).setEditable(false);
			contentPane.add(eleFloor.get(i));
		}
		
		//INIT Scheduler Text Pane
		scheduler = new JTextPane();
		scheduler.setBounds(100, height - 150, width-140, 80);
		scheduler.setText("Scheduler is waiting...");
		scheduler.setEditable(false);
		contentPane.add(scheduler);
	}
	
	//When a floor needs a elevator
	public void receiveEle() {
        receivePacket = helper.receivePacket(socket);
        //helper.print(receivePacket, "Elevator Subsystem", "received from Scheduler");
        
        System.out.println("\nContaining: " + new String(receivePacket.getData()) +"\n");
    }
	
	public void updateEle(int id) throws IOException {
		Config config = new Config();
		
		int eleNumber = id-1;
		int tempFloor = 4;
		int eleLocX = 180;
		int eleLocY = (config.getIntProperty("floorGUI") * (config.getIntProperty("numFloors") - (tempFloor-1))) - 15;
		
		eleFloor.get(eleNumber).setText("Floor " + tempFloor);
		eleText.get(eleNumber).setText("Elevator is unloading");
		elePanel.get(eleNumber).setBounds(eleLocX + (config.getIntProperty("eleGUI") * eleNumber ), eleLocY, 25, 25);
		elePanel.get(eleNumber).setBackground(Color.BLACK);
		contentPane.add(elePanel.get(eleNumber));
	}
}
