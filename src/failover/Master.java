/**
 * @author Nicholas Ibarluzea
 */

package failover;

import failover.packets.*;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Master extends Node {
	
	ConcurrentHashMap<InetAddress, Integer> missCount;
	Timer timer;
	
	private final long delay;
	private boolean running;
	
	protected Master(long hbDelay) {
		// Initialize
		this.delay = hbDelay;
		this.missCount = new ConcurrentHashMap<InetAddress, Integer>();
		this.timer = new Timer("Master Heartbeat", true);
		log("This node is now the master.");
	}
	
	@Override
	protected void startHeartbeat() {
		if(!running) {
			// Schedule all heartbeat tasks for current node list
			for(InetAddress addr : Node.getSlaveList()) {
				timer.scheduleAtFixedRate(new Task(addr), 0, delay);
			}

			running = true;
			log("Heartbeat messaging started.");
		}
	}
	
	@Override
	protected void process(Packet packet) {
		// Master heartbeat
		if(packet.getType() == Packet.MASTER_HB) {
			MasterHBPacket pack = (MasterHBPacket)packet;
			if(pack.getSubType() == MasterHBPacket.RESPONSE) {
				missCount.put(pack.getSender(), 0);
				log("Heartbeat received from "+pack.getSender()+"."); // Missed response count: "+missCount.get(pack.getSender()));
			}
		
		// Slave heartbeat
		} else if(packet.getType() == Packet.SLAVE_HB) {
			SlaveHBPacket pack = (SlaveHBPacket)packet;
			if(pack.getSubType() == SlaveHBPacket.REQUEST) {
				Node.send(new SlaveHBPacket(SlaveHBPacket.RESPONSE, pack.getSender()));
				log("Heartbeat response sent to "+pack.getSender()+".");
			}
			
		// Master negotiation
		} else if(packet.getType() == Packet.MASTER_NEG) {
			MasterNegPacket pack = (MasterNegPacket)packet;
			if(pack.getSubType() == MasterNegPacket.PROPOSE) {
				Node.send(new MasterNegPacket(MasterNegPacket.DECLINE, pack.getSender()));
				log("Master proposal from "+pack.getSender()+" denied.");
			}
		
		// Node addition
		} else if(packet.getType() == Packet.CLUSTER_UP) {
			ClusterUpdatePacket pack = (ClusterUpdatePacket)packet;
			if(pack.getSubType() == ClusterUpdatePacket.JOIN) {
				Node.addSlave(pack.getSender());
				Node.sendClusterUpdate();
				timer.scheduleAtFixedRate(new Task(pack.getSender()), 0, delay);
				log("New slave added: "+pack.getSender());
			}
			
		}
	}
	
	
	class Task extends TimerTask {
		
		private InetAddress addr;
		
		Task(InetAddress addr) {
			this.addr = addr;
			missCount.put(addr, 0);
		}
		
		@Override
		public void run() {
			if(missCount.get(addr) >= 3) {
				nodeDown();
				cancel();
				return;
			}
			Packet pack = new MasterHBPacket(MasterHBPacket.REQUEST, addr);
			Node.send(pack);
			missCount.put(addr, missCount.get(addr)+1);
			log("Heartbeat sent to "+addr+". Missed response count: "+missCount.get(addr));
		}
		
		private void nodeDown() {
			Node.removeSlave(addr);
			Node.sendClusterUpdate();
		}
	}
	
}
