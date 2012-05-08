/**
 * @author Nicholas Ibarluzea
 */

package failover.packets;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;

public class MasterNegPacket extends Packet {
	
	/**
	 * Proposal for sender to become master.
	 */
	public static final byte PROPOSE	=	0x01;
	/**
	 * Acceptance of sender becoming master.
	 */
	public static final byte ACCEPT	=	0x02;
	/**
	 * Declination of sender becoming master.
	 */
	public static final byte DECLINE	=	0x03;
	/**
	 * Declaration of sender being master.
	 */
	public static final byte DECLARE	=	0x04;
	
	private byte subType;
	private long timestamp;
	
	protected MasterNegPacket(DatagramPacket dPacket) {
		super(dPacket);
		
		// Parse packet data
		subType = data[1];
		if(subType == PROPOSE)
			timestamp = bytes2long(copyOfRange(data, 2, 10));
	}
	
	public MasterNegPacket(byte subType, InetAddress receiver) {
		super(Packet.MASTER_NEG, receiver);
		this.subType = subType;
		this.timestamp = new Date().getTime();
	}
	
	@Override
	protected byte[] getData() {
		byte[] ret = new byte[10];
		ret[0] = type;
		ret[1] = subType;
		if(subType == PROPOSE) {
			byte[] ts = long2bytes(timestamp);
			System.arraycopy(ts, 0, ret, 2, 8);
		}
		return ret;
	}
	
	public byte getSubType() {
		return subType;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
