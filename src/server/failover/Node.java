/**
 * @author Nicholas Ibarluzea
 */

package failover;

import failover.packets.ClusterUpdatePacket;
import failover.packets.MasterNegPacket;
import failover.packets.Packet;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Arrays;
import java.util.List;

abstract class Node extends Thread {
	
	public static final long MASTER_HEARTBEAT_DELAY = 2000;
	public static final long SLAVE_HEARTBEAT_DELAY = 5000;
	
	private static Node _this;
	private static DatagramSocket sock;
	private static List<InetAddress> slaveList;
	private static PrintStream out;
	private static InetAddress master, client;
	
	
	protected abstract void process(Packet packet);
	protected abstract void startHeartbeat();
	
	
	/*	==================================================================	*/
	/*	=====================	 Public methods		======================	*/
	/*	==================================================================	*/
	
	/**
	 * If not already running, this method initializes the Node singleton as a
	 * slave and begins running.
	 */
	public static void startSlave() {
		if(_this == null) {
			try {
				sock = new DatagramSocket(2690);
				_this = new Slave(SLAVE_HEARTBEAT_DELAY);
				_this.start();
			} catch (SocketException ex) {
				System.out.println("Failover couldn't bind to port 2690");
				System.exit(-1);
			}
		}
		
	}
	
	/**
	 * If not already running, this method initializes the Node singleton as a
	 * master and begins running.
	 * 
	 * @param slaves	the list of slaves to attempt to be master of
	 */
	public static void startMaster(List<InetAddress> slaves) {
		if(_this == null) {
			slaveList = slaves;
			
			try {
				if(sock == null)
					sock = new DatagramSocket(2690);
				if(client == null)
					client = Inet4Address.getByAddress(new byte[] { 0x00, 0x00, 0x00, 0x00 });
				
				declareMaster();
				
				_this.start();
				Node.sendClusterUpdate();
				_this.startHeartbeat();
				
			} catch (UnknownHostException ex) {
				System.exit(-1);
			} catch (SocketException ex) {
				System.out.println("Failover couldn't bind to port 2690");
				System.exit(-1);
			}
		}
	}
	
	/**
	 * Returns this node's current list of all slaves in the network.
	 * @return	list of nodes
	 */
	synchronized public static List<InetAddress> getSlaveList() {
		return slaveList;
	}
	
	/**
	 * Returns the address of the believed master node.
	 * @return	address of master
	 */
	public static InetAddress getMasterAddress() {
		return master;
	}
	
	/**
	 * Returns the client's address.
	 * @return	address of client
	 */
	public static InetAddress getClientAddress() {
		return client;
	}
	
	/**
	 * Sets the client's address.
	 */
	public static void setClientAddress(InetAddress addr) {
		client = addr;
	}
	
	/**
	 * Returns true if this server is the cluster's master node.
	 * 
	 * @return	whether the node is master
	 */
	public static boolean isMaster() {
		return (_this instanceof Master);
	}
	
	/**
	 * Sets this server to log all major activity with the supplied PrintStream.
	 * <p />
	 * Note that by inputting System.out, the activity will simply be printed
	 * to the console's standard output.
	 * @param out	the PrintStream to log to
	 */
	public static void setVerbose(PrintStream out) {
		Node.out = out;
	}
	
	
	/*	==================================================================	*/
	/*	=====================	Non-public methods	======================	*/
	/*	==================================================================	*/
	
	/**
	 * Since no outside classes can obtain a reference to the Node singleton,
	 * this method is basically private.
	 */
	@Override
	public void run() {
		for(;;) {
			if(_this == null) {
				startMaster();
			}
			try {
				DatagramPacket dPack = new DatagramPacket(new byte[100], 100);
				sock.receive(dPack);
				Packet pack = Packet.packetFactory(dPack);
				//System.out.println("PROCESSING: "+pack.getType());
				_this.process(pack);
			} catch (IOException ex) {
				log("Socket closed.");
			}
		}
	}
	
	protected static boolean startMaster() {
		_this = null;
		Node.startMaster(Node.getSlaveList());
		if(isMaster()) return true; else return false;
	}
	
	protected static void masterDown() {
		try {
			_this = null;
			sock.close();
			sock = new DatagramSocket(2690);
		} catch (SocketException ex) {
			System.out.println("Trouble reopening socket.");
			System.exit(-1);
		}
	}
	
	protected synchronized static List<InetAddress> setSlaveList(List<InetAddress> nodeList) {
		Node.slaveList = nodeList;
		return nodeList;
	}
	
	protected synchronized static List<InetAddress> removeSlave(InetAddress addr) {
		Node.slaveList.remove(addr);
		return slaveList;
	}
	
	protected static void sendClusterUpdate() {
		List<InetAddress> list = Node.getSlaveList();
		for(InetAddress node : list) {
			Node.send(new ClusterUpdatePacket(Node.getClientAddress(), slaveList, node));
		}
	}
	
	protected static void send(Packet pack) {
		try {
			sock.send(pack.getDatagramPacket());
		} catch (IOException ex) {
			System.out.println("Error sending "+pack.getType());
			System.exit(-1);
		}
	}
	
	protected static void declareMaster() {
		try {
			MasterNegPacket decPack = new MasterNegPacket(MasterNegPacket.DECLARE, null);
			List<InetAddress> slaves = getSlaveList();
			Integer toRemove = null;
			
			for(int i=0; i<slaves.size(); i++) {
				if(Arrays.equals(slaves.get(i).getAddress(), InetAddress.getLocalHost().getAddress())) {
					toRemove = i;
					continue;
				}
				decPack.setReceiver(slaves.get(i));
				Node.send(decPack);
			}
			
			if(toRemove != null) {
				slaveList.remove(toRemove.intValue());
			}
			_this = new Master(MASTER_HEARTBEAT_DELAY);
			_this.startHeartbeat();
			sendClusterUpdate();
			
		} catch (UnknownHostException ex) {
			System.out.println("Node.declareMaster.UnknownHostException");
			System.exit(-1);
		}
	}
	
	protected void setMaster(InetAddress addr) {
		Node.master = addr;
	}
	
	protected void log(String s) {
		if(out != null)
			out.println(s);
	}

}
