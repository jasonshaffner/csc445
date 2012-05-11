/**
 * @author Nicholas Ibarluzea
 */

package failover.packets;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ClusterUpdatePacket extends Packet {
	
	/**
	 * Synchronization message sent from master.
	 * Includes list of all current nodes. The first address will always be the
	 * client and the second address the master.
	 */
	public static final byte SYNC	=	0x01;
	/**
	 * Synchronization response sent from slave.
	 */
	public static final byte ACK	=	0x02;
	/**
	 * Request to join the cluster network.
	 * A slave which was started after the original master's initialization
	 * must send this packet to the master in order to request entry into the
	 * cluster. It will be added as a slave and must immediately start the
	 * slave process in order to receive the cluster update.
	 */
	public static final byte JOIN	=	0x03;
	/**
	 * Notification of new master.
	 * When a new master is appointed, this packet will be sent from the new
	 * master to the client in order to inform it of the change.
	 */
	public static final byte NEW_MASTER	=	0x04;
	
	private byte subType;
	private InetAddress clientAddress, masterAddress;
	private List<InetAddress> slaveList;
	
	protected ClusterUpdatePacket(DatagramPacket dPacket) {
		super(dPacket);
		
		// Parse packet data
		subType = data[1];
		
		// SYNC
		if(subType == SYNC) {
			slaveList = new ArrayList<InetAddress>();
			try {
				int i = 2;
				clientAddress = InetAddress.getByAddress(copyOfRange(data, i, i+4));
				i+=4;
				masterAddress = InetAddress.getByAddress(copyOfRange(data, i, i+4));
				i+=4;
				for(; i<length; i+=4)
					slaveList.add(InetAddress.getByAddress(copyOfRange(data, i, i+4)));
			} catch (UnknownHostException ex) {
				System.out.println("ClusterUpdatePacket.ClusterUpdatePacket.UnknownHostException");
				System.exit(-1);
			}
		// NEW_MASTER
		} else if(subType == NEW_MASTER) {
			masterAddress = sender;
			clientAddress = receiver;
		}
	}
	
	public ClusterUpdatePacket(byte subType, InetAddress receiver) {
		super(Packet.CLUSTER_UP, receiver);
		this.subType = subType;
	}
	
	public ClusterUpdatePacket(InetAddress clientAddress, List<InetAddress> nodeList, InetAddress receiver) {
		super(Packet.CLUSTER_UP, receiver);
		try {
			this.subType = SYNC;
			this.clientAddress = clientAddress;
			this.masterAddress = InetAddress.getLocalHost();
			this.slaveList = nodeList;
		} catch (UnknownHostException ex) {
			System.out.println("ClusterUpdatePacket.ClusterUpdatePacket.UnknownHostException");
		}
	}
	
	@Override
	protected byte[] getData() {
		byte[] ret;
		if(subType == SYNC) {
			ret = new byte[(slaveList.size()*4)+8+2];
			int i = 2;
			System.arraycopy(clientAddress.getAddress(), 0, ret, i, 4);
			i+=4;
			System.out.println(masterAddress.getAddress());
			System.arraycopy(masterAddress.getAddress(), 0, ret, i, 4);
			i+=4;
			for(InetAddress addr : slaveList) {
				System.arraycopy(addr.getAddress(), 0, ret, i, 4);
				i+=4;
			}
		} else
			ret = new byte[2];
		
		ret[0] = type;
		ret[1] = subType;
		
		return ret;
	}
	
	public byte getSubType() {
		return subType;
	}
	
	public InetAddress getClient() {
		return clientAddress;
	}
	
	public InetAddress getMaster() {
		return masterAddress;
	}
	
	public List<InetAddress> getSlaveList() {
		return slaveList;
	}

}