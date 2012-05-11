package client;

import java.io.*;
import java.net.*;
import java.util.*;
import database.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    final int port = 2372;
    private InetAddress host = null;
    private MasterWatch watch;

    public Client() {
////////////////////////modified by Ben/////////////////////////////////////////////////        
        watch = new MasterWatch();
        watch.start();
        System.out.println("start watcher");
////////////////////////////////////////////////////////////////////////////////////////        
    }

    public boolean sendDataSet(DataSet d) {
        host = watch.getMaster();
        try {
            Byte reply;
            int i = 0;
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (socket.isConnected()) {
                try {
                    System.out.println("Client sending data");
                    oos.writeObject("dataset");
                    oos.writeObject(d);
                    System.out.println("Client data sent");
                    reply = (Byte) ois.readObject();
                    if (reply == 0) {
                        if (i++ < 10) {
                            continue;
                        } else {
                            return false;
                        }
                    } else {
                        System.out.println("Client closing");
                        oos.flush();
                        ois.close();
                        oos.close();
                        socket.close();
                        return true;
                    }
                } catch (ClassNotFoundException cnf) {
                    System.out.println(cnf);
                }
            }
        } catch (IOException io) {
            System.out.println(io);
        }
        return false;
    }

    public boolean sendFile(File f) {
        host = watch.getMaster();
        try {
            Byte reply;
            int i = 0;
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (socket.isConnected()) {
                try {
                    System.out.println("Client sending file");
                    oos.writeObject("file");
                    oos.writeObject(f);
                    System.out.println("Client file sent");
                    reply = (Byte) ois.readObject();
                    if (reply == 0) {
                        if (i++ < 10) {
                            continue;
                        } else {
                            return false;
                        }
                    } else {
                        System.out.println("Client closing");
                        oos.flush();
                        ois.close();
                        oos.close();
                        socket.close();
                        return true;
                    }
                } catch (ClassNotFoundException cnf) {
                    System.out.println(cnf);
                }
            }
        } catch (IOException io) {
            System.out.println(io);
        }
        return false;
    }

    public boolean removeRecord(String key) {
        host = watch.getMaster();
        try {
            Byte reply;
            int i = 0;
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (socket.isConnected()) {
                try {
                    System.out.println("Client requesting removal");
                    oos.writeObject("remove");
                    oos.writeObject(key);
                    System.out.println("Client sent request");
                    reply = (Byte) ois.readObject();
                    if (reply == 0) {
                        System.out.println("Not removed");
                        if (i++ < 10) {
                            continue;
                        } else {
                            return false;
                        }
                    } else {
                        System.out.println("Client closing");
                        oos.flush();
                        ois.close();
                        oos.close();
                        socket.close();
                        return true;
                    }
                } catch (ClassNotFoundException cnf) {
                    System.out.println(cnf);
                }
            }
        } catch (IOException io) {
            System.out.println(io);
        }
        return false;
    }

    public String requestData(String query) {
////////////////////////modified by Ben/////////////////////////////////////////////////
        
        if(watch.getMaster() != null){
        host = watch.getMaster();
            System.out.println("host found:   " + host);
        }else{
            try {
                host = InetAddress.getByName("gee.cs.oswego.edu");
            } catch (UnknownHostException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("localhost");
            //System.exit(0);
        }
////////////////////////////////////////////////////////////////////////////////////////////////        
        try {
            int i = 0;
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (socket.isConnected()) {
                try {
                    System.out.println("Client requesting data");
                    oos.writeObject("request");
                    oos.writeObject(query);
                    System.out.println("Client request sent");
                    String d = (String) ois.readObject();
                    System.out.println("Client closing");
                    ois.close();
                    oos.close();
                    socket.close();
                    return d;
                } catch (ClassNotFoundException cnf) {
                    System.out.println(cnf);
                }
            }
        } catch (IOException io) {
            System.out.println(io);
        }
        return null;
    }
   
}
