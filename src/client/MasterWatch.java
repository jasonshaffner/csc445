/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.*;
import java.io.*;
import java.util.*;
import database.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//  this class creates a new thread so the client can listen on port 2690 for the new master
//  It then takes the address for the new master and uses it for the host vairable in the client class
//////////////////////////////////////////////////////////////////////////////////////////////////////////
public class MasterWatch extends Thread {
    private InetAddress masterAddress;

    @Override
    public void run() {
        DatagramSocket socket = null;
        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
                System.out.println("created socket");
                socket = new DatagramSocket(2690);

            } catch (SocketException ex) {
                System.out.println("error, socket wasnt created");
            }
        for (;;) {

            
            
            try {
                System.out.println("recieving packet");
                socket.receive(packet);
                System.out.println("packet found: " + packet.getAddress().toString());
                this.masterAddress = packet.getAddress();
               
            } catch (IOException ex) {
                System.out.println("error, stupid packet");
                Logger.getLogger(MasterWatch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }
    public InetAddress getMaster(){
        return
                this.masterAddress;
    }
}
