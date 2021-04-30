// MessageHandler Component
// Accepts messages sent to local user, and sends messages from local user to recipient. 

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class MessageHandler {
	
  private static final int PORT = 61801;
  
//Accepts and packets Message object, then attempts to send packet to recipient.
  public static void sendMessage(Message msg, ArrayList<Message> outbox, ArrayList<Message> sent) throws UnknownHostException{
    InetAddress aHost = msg.getReceiver().getAddress();
    (new Thread() {
        @Override
        public void run() {
            try {
            	
				Socket socket = new Socket(aHost, PORT);
				OutputStream output = socket.getOutputStream();
				ObjectOutputStream objectOutput = new ObjectOutputStream(output);
				int tries = 0;
				while (true) {
					try {
						objectOutput.writeObject(msg);
						sent.add(msg);
						break;
					} catch (IOException e){
						if(tries <= 5) {
							tries++;
						}
						else {
							
							outbox.add(msg);
						}
					}
				}
				socket.close();
				
            } catch(IOException e) {
            	e.printStackTrace();
            }
       }
    }).start();
   }


	// Listens for inbound messages to local user and writes them to inbox.
  public static void getMessage(ArrayList<Message> inbox) {
    (new Thread() {
        @Override
        public void run() {
        	
        		try {
        			
					ServerSocket listener = new ServerSocket(PORT);
					while(true) {
						Socket client = listener.accept();
						InputStream input = client.getInputStream();
						ObjectInputStream objectInput = new ObjectInputStream(input);
						Message msg = (Message) objectInput.readObject();
						inbox.add(msg);
						Platform.runLater(() -> LOMS.makeNotif(msg));
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
        		
            }

    }).start();
 }
  
}


