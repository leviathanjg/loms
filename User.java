//User Component

/*The attributes of the User class include:
 *  
 *  the local user's assigned username
 *  the local user's display name shown in chat
 *  the IP address of the computer being used
 *  the state of user being online / offline
 *  
 *  */

import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable {
	
	// attributes
	private String displayName;
	private String username;
	private InetAddress address;
	private Boolean online;
	
	User(String dn, String un) {
		displayName = dn;
		username = un;
		online = false;
	} // end no address constructor
	
	User (String dn, String un, InetAddress a) {
		displayName = dn;
		username = un;
		address = a;
		online = true;
	} // end with address constructor
	
	@Override
	public String toString() {
		// show display name, username, and address (if not null)
		return displayName + " (" + username + ")";
	} // end tostring override
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getUsername() {
		return username;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public Boolean getStatus() {
		return online;
	}
	
	public void setDisplayName(String dn) {
		displayName = dn;
	}
	
	public void setUsername(String un) {
		username = un;
	}
	
	public void setAddress(InetAddress a) {
		address = a;
	}
	
	public void setStatus(Boolean s) {
		online = s;
	}

	public int equals(User u) {
		if (username == u.getUsername()) return 0;
		else return -1;
	}
	
	
} // end class User