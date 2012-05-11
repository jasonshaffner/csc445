/**
 * @author Nicholas Ibarluzea
 */

package failover.packets;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class SlaveHBPacket extends Packet {
	
	/**
	 * Heartbeat request sent from slave.
	 */
	public static final byte REQUEST	=	0x01;
	/**
	 * Heartbeat response sent from master.
	 */
	public static final byte RESPONSE	=	0x02;
	
	private byte subType;
	
	protected SlaveHBPacket(DatagramPacket dPacket) {
		super(dPacket);
		
		// Parse packet data
		this.subType = data[1];
	}
	
	public SlaveHBPacket(byte subType, InetAddress receiver) {
		super(Packet.SLAVE_HB, receiver);
		this.subType = subType;
	}
	
	@Override
	protected byte[] getData() {
		return new byte[] { SLAVE_HB, subType };
	}
	
	public byte getSubType() {
		return subType;
	}

}
