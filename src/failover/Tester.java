/**
 * @author Nicholas Ibarluzea
 */

package failover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Tester {
	
	public Tester () throws UnknownHostException{
		
		Scanner sc = new Scanner(System.in);
		
		// Make all operations verbose and print to standard out
		Node.setVerbose(System.out);
		
		for(;;) {
			String input = null, master = null;
//			if(args.length > 0) {
//				if(args[0].equals("-m"))
//					input = "m";
//				else if(args[0].equals("-s")) {
//					input = "s";
//					if(args.length > 1)
//						master = args[1];
//				}
//			} 
 
				System.out.println("Initialize this node as a slave (s) or master (m)?");
				input = sc.next();
			
		
			// If just starting a slave
			if(input.equalsIgnoreCase("s")) {
				if(master == null) {
					Node.startSlave();
					System.out.println("Waiting on master...");
				} else {
					InetAddress host = InetAddress.getByName(master);
					Node.joinCluster(host);
				}
				break;
			// If starting the master, must get other slaves
			} else if(input.equalsIgnoreCase("m")) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					ArrayList<InetAddress> slaves = new ArrayList<InetAddress>();
					int i = 1;
					
					System.out.println("Enter the hostnames of the other nodes (an empty line will finish):");
					System.out.print(i+": ");
					input = br.readLine();
					while(!input.equals("")) {
						try {
							slaves.add(InetAddress.getByName(input));
							i++;
						} catch (UnknownHostException ex) {
							System.out.println("Unknown host!");
						}
						System.out.print(i+": ");
						input = br.readLine();
					}
					
					System.out.println("Starting...");
					Node.startMaster(slaves);
					break;
				} catch (IOException ex) {
					System.out.println("IOException...");
					System.exit(-1);
				}
				
			}
		}
                
	
	}
////////////////////////modified by Ben///////////////////////////////////////////////// 
//              this code sets the currents nodes client address then sends to the others
        
        public void setClientLocation(InetAddress location){
            Node.setClientAddress(location);
            Node.sendClusterUpdate();
        }
////////////////////////modified by Ben/////////////////////////////////////////////////        
}
