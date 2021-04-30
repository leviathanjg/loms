import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

public class BroadcastHandler {
	
  private static final int PORT = 61801;
  
	/* sendBroadcast */
	// updates local user's IP address
	// local network broadcast socket created and configured
	// broadcast packet is created to send user info to recipient
	// message and user info are sent over local network
  
  public static void sendBroadcast(User usr) throws UnknownHostException{
	  
    (new Thread() {
        @Override
        public void run() {
        	try {
        		DatagramSocket socket = new DatagramSocket();
        		socket.setBroadcast(true);
        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
        		while(true) {
	                oos.writeObject(usr);
	                byte[] data = baos.toByteArray();
	                DatagramPacket packet = new DatagramPacket(data, data.length, usr.getAddress(), PORT);
	                socket.send(packet);
	                Thread.sleep(3000);
        		}
                
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        }
     }).start();
    }

	/* getBroadcast */
	// local network broadcast socket created to accept incoming packets
	// listens for and accepts new broadcast messages
  public static void getBroadcast(ArrayList<User> contacts) {
	    (new Thread() {
	    	
	    	@Override
	        public void run() {
	    		
	    		DatagramSocket socket = null;
	    		
	    		try {
	    			socket = new DatagramSocket(PORT);
	    		} catch (SocketException e) {
	    			e.printStackTrace();
	    		}
	    		
	    		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
	    		while(true) {
	    			try {
	    				socket.receive(packet);  				
	        			ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
	    				ObjectInputStream ois = new ObjectInputStream(bais);
	    				User newContact = (User)ois.readObject();
	    				FileIOHandler.updateContacts(contacts, newContact);
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
	    		}
	    	}
	    }).start();
  }
}
