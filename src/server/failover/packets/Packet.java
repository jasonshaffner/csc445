/**
 * @author Nicholas Ibarluzea
 */

package failover.packets;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class Packet {
	
	/**
	 * Master negotiation packet.
	 */
	public static final byte MASTER_NEG	=	0x01;
	/**
	 * Heartbeat message originating from master and sent to slave.
	 */
	public static final byte MASTER_HB	=	0x02;
	/**
	 * Heartbeat message originating from slave and sent to master.
	 */
	public static final byte SLAVE_HB	=	0x03;
	/**
	 * Updated list of current nodes in cluster network.
	 */
	public static final byte CLUSTER_UP	=	0x04;
	/**
	 * New client address sent to master or new master address sent to client.
	 */
	public static final byte CLIENT_UP	=	0x05;
	
	
	protected byte type;
	protected byte[] data;
	protected int length;
	protected InetAddress sender;
	protected InetAddress receiver;
	
	protected abstract byte[] getData();

	protected Packet(byte type, InetAddress receiver) {
		this.type = type;
		this.receiver = receiver;
	}
	
	public Packet(DatagramPacket dPacket) {
		this.data = dPacket.getData();
		this.length = dPacket.getLength();
		this.type = data[0];
		this.sender = dPacket.getAddress();
	}
	
	public static Packet packetFactory(DatagramPacket dPacket) {
		byte type = dPacket.getData()[0];
		if(type == MASTER_NEG)
			return new MasterNegPacket(dPacket);
		else if(type == MASTER_HB)
			return new MasterHBPacket(dPacket);
		else if(type == SLAVE_HB)
			return new SlaveHBPacket(dPacket);
		else if(type == CLUSTER_UP)
			return new ClusterUpdatePacket(dPacket);
		else
			return null;
	}
	
	public DatagramPacket getDatagramPacket() {
		data = getData();
		DatagramPacket dPacket = new DatagramPacket(data, data.length);
		//dPacket.setAddress(receiver);
		dPacket.setSocketAddress(new InetSocketAddress(receiver, 2690));
		return dPacket;
	}
	
	public byte getType() {
		return type;
	}
	
	public InetAddress getSender() {
		return sender;
	}
	
	public void setReceiver(InetAddress addr) {
		receiver = addr;
	}
	
	
	
	// Helper methods
	
	static byte[] long2bytes(long l) {
		byte[] b = new byte[8];
		int index = 0;
		for(int i=0; i<8; i++) {
			b[index++] = (byte) (l & 0xFF);
			l >>>= 8;
		}
		return b;
	}
	
	static long bytes2long(byte[] b) {
		long l = 0;
		for(int i=0; i<8; i++)
			l |= (long) ((long)b[i] << (i*8));
		return l;
	}
	
	// Arrays.copyOfRange isn't supported in JDK 1.5 so this is my own implementation
	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}
	
}