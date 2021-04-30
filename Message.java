//Message Component

/*The attributes of the Message class include:
 *  
 *  the message sender
 *  the message receiver
 *  the primary message (MessageStyle)
 *  the custom secondary message String
 *  the date the message was created
 *  
 *  */

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

	// attributes
	User sender;
	User receiver;
	MessageStyle message;
	String customMessage;
	Date created;
	
	Message(User s, User r, MessageStyle m, String cm) {
		sender = s;
		receiver = r;
		message = m;		
		customMessage = cm;
		created = new Date();
	} // end constructor
	
	public User getSender() {
		return sender;
	}
	
	public User getReceiver() {
		return receiver;
	}
	
	public MessageStyle getMessage() {
		return message;
				
	}
	
	public String getCustomMessage() {
		return customMessage;
	}
	
	public Date getDateCreated() {
		return created;
	}
	
	public void setSender(User s) {
		sender = s;
	}
	
	public void setReceiver(User r) {
		receiver = r;
	}
	
	public void setMessage(MessageStyle m) {
		message = m;
	}
	
	public void setCustomMessage(String cm) {
		customMessage = cm;
	}
	
	@Override
	public String toString() {
		
		return "From: " + sender + ", To: " + receiver + 
			"\nType: " + message + ", Custom message: " + customMessage +
			"\nDate created: " + created;
	} // end toString override
	
} // end class Message