/**
 * @author Nicholas Ibarluzea
 */

package failover.packets;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class MasterHBPacket extends Packet {
	
	/**
	 * Heartbeat request sent from master. Includes list of all current nodes.
	 */
	public static final byte REQUEST	=	0x01;
	/**
	 * Heartbeat response sent from slave.
	 */
	public static final byte RESPONSE	=	0x02;
	
	private byte subType;
	
	protected MasterHBPacket(DatagramPacket dPacket) {
		super(dPacket);
		
		// Parse packet data
		subType = data[1];
	}
	
	public MasterHBPacket(byte subType, InetAddress receiver) {
		super(Packet.MASTER_HB, receiver);
		this.subType = subType;
	}
	
	@Override
	protected byte[] getData() {
		return new byte[] { MASTER_HB, subType };
	}
	
	public byte getSubType() {
		return subType;
	}

}