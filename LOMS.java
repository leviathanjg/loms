/* AUTHOUR: Leviathan Guillena, Lee Maines, Avinaash Vasudaven
 * LAST MODIFIED: April 9 2021
 * PURPOSE: COSC 3506 Sets up graphical user interface for LOMS.
 * ICON SOURCE: Mario del Valle Guijarro, https://mariodelvalle.github.io/CaptainIconWeb/ */

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


// sets up enum for message style
enum MessageStyle {
	ASSIST,
	URGENT,
	MEETING,
	CLIENT_UPDATE,
	DEADLINE,
	DOWNTIME,
	MAINTENANCE,
	FOLLOW_UP,
	GENERIC
} // end enum MessageType


public class LOMS extends Application {	
		
	
	// starts program
    public static void main (String[] args) {
        launch(args);
    } // end main method
    

    // creates a notification to display when message received
    public static void makeNotif(Message msg) {
    	Alert notif = new Alert(AlertType.CONFIRMATION);
    	notif.setTitle("New Message");
    	notif.setContentText(msg.toString());
    	notif.show();
    }
    

	@Override
    public void start(Stage primaryStage) throws UnknownHostException, FileNotFoundException {
		
		//////////////////////////
		// 0. STARTUP FUNCTIONS //
		//////////////////////////
		
		// create inboxes locally

		// if files exist, import them; otherwise empty arrays
    	ArrayList<Message> inbox = new ArrayList<Message>();
    	ArrayList<Message> outbox = new ArrayList<Message>();
    	ArrayList<Message> sent = new ArrayList<Message>();
    	ArrayList<User> contacts = new ArrayList<User>();
    	FileIOHandler.onStartup(inbox, outbox, sent, contacts);
    	    	
		// make self and boxes
    	User sender = new User("The Cooler Levi", System.getProperty("user.name"), InetAddress.getLocalHost());

    	// broadcast self to the world
    	BroadcastHandler.sendBroadcast(sender);
    	
    	// run message receiver apps
    	MessageHandler.getMessage(inbox);
    	BroadcastHandler.getBroadcast(contacts);
    	
    	//////////////////////////
    	// END 0. STARTUP FUNCT //
    	//////////////////////////
    	
    	/* STRUCTURE:
    	 * 1. CONTACT LIST 
    	 * 2. MESSAGE BOXES (IN, OUT, SENT) 
    	 * 3. OPTIONS
    	 * 4. TABS & OTHER GENERAL SETUPS */
    	
    	/////////////////////
    	// 1. CONTACT LIST //
    	/////////////////////
    	
    	// prepare icons for screen
    	GridPane messageIcons = new GridPane();
    	
		VBox assistIcon = new VBox(new ImageView(new Image(new File("icons/ASSIST.png").toURI().toString())), new Label("Need Assistance"));			
		VBox clientIcon = new VBox(new ImageView(new Image(new File("icons/CLIENT.png").toURI().toString())), new Label("Client Update"));
		VBox deadlineIcon = new VBox(new ImageView(new Image(new File("icons/DEADLINE.png").toURI().toString())), new Label("Upcoming Deadline"));
		VBox downtimeIcon =	new VBox(new ImageView(new Image(new File("icons/DOWNTIME.png").toURI().toString())), new Label("Downtime Notice"));
		VBox followUpIcon = new VBox(new ImageView(new Image(new File("icons/FOLLOWUP.png").toURI().toString())), new Label("Follow-Up"));
		VBox genericIcon = new VBox(new ImageView(new Image(new File("icons/GENERIC.png").toURI().toString())), new Label("Generic Message"));
		VBox maintenanceIcon = new VBox (new ImageView(new Image(new File("icons/MAINTENANCE.png").toURI().toString())), new Label("Maintenance Notice"));
		VBox meetingIcon = new VBox(new ImageView(new Image(new File("icons/MEETING.png").toURI().toString())), new Label("Upcoming Meeting"));
		VBox urgentIcon = new VBox (new ImageView(new Image(new File("icons/URGENT.png").toURI().toString())), new Label("Urgent"));
		
		assistIcon.setAlignment(Pos.CENTER);
		clientIcon.setAlignment(Pos.CENTER);
		deadlineIcon.setAlignment(Pos.CENTER);
		downtimeIcon.setAlignment(Pos.CENTER);
		followUpIcon.setAlignment(Pos.CENTER);
		genericIcon.setAlignment(Pos.CENTER);
		maintenanceIcon.setAlignment(Pos.CENTER);
		meetingIcon.setAlignment(Pos.CENTER);
		urgentIcon.setAlignment(Pos.CENTER);
		
		messageIcons.setAlignment(Pos.CENTER);
		messageIcons.setHgap(10.0);
		messageIcons.setVgap(5.0);
		
		// align gridpane
		GridPane.setConstraints(assistIcon, 0, 0);
		GridPane.setConstraints(clientIcon, 1, 0);
		GridPane.setConstraints(deadlineIcon, 2, 0);
		GridPane.setConstraints(downtimeIcon, 0, 1);
		GridPane.setConstraints(followUpIcon, 1, 1);
		GridPane.setConstraints(genericIcon, 2, 1);
		GridPane.setConstraints(maintenanceIcon, 0, 2);
		GridPane.setConstraints(meetingIcon, 1, 2);
		GridPane.setConstraints(urgentIcon, 2, 2);
		
		// add all message types to icons box
		messageIcons.getChildren().addAll(assistIcon, clientIcon, deadlineIcon, downtimeIcon, followUpIcon, genericIcon,
				maintenanceIcon, meetingIcon, urgentIcon);
		
		// message menu
    	Button cancelButton = new Button("Cancel");
    	Button goBackButton = new Button("Go Back");
    	Button refreshButton = new Button("Refresh");
    	
		VBox messageMakerBox = new VBox(new Label("Select your message type: "), messageIcons, cancelButton);
		messageMakerBox.setAlignment(Pos.CENTER);
		messageMakerBox.setVisible(false);
		messageMakerBox.setDisable(true);
		
    	// contact list

		//ObservableList<User> observableContacts = (ObservableList<User>)contacts;
    	ListView<User> contactList = new ListView<User>();
    	ScrollPane contactScrollList = new ScrollPane(contactList);
		VBox contactPage = new VBox(contactScrollList, messageMakerBox, refreshButton);
    	
		// update contacts list
		refreshButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				contactList.getItems().clear();
				for (int i = 0; i < contacts.size(); i++) {
					contactList.getItems().add(contacts.get(i));
				}
				contactList.refresh();
			} // override handle
			
		});
    	
    	// shared elements in secondary message box
    	TextField customMessage = new TextField();
    	Button submitMessage = new Button("Submit Message");
    	
    	// ** //
    	
    	// event handling: display and enable message box when contact clicked
    	// disable contact selection to prevent changing
    	contactList.setOnMouseClicked(new EventHandler<MouseEvent>() {
    		
			@Override
			public void handle(MouseEvent e) {
				
				if (contactList.getItems().isEmpty()) return;
				
				messageMakerBox.setVisible(true);	
				messageMakerBox.setDisable(false);
				contactList.setDisable(true);
				

				// event handling: if cancelled, return to prior state
				cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent e) {

						messageMakerBox.setVisible(false);
						messageMakerBox.setDisable(true);
						contactList.setDisable(false);
						
					} // end handle override (cancel button)
				}); // end onclick handler for cancel

			} // end handle override
    	}); // end onclick handler for contact list
    	
    	
    	// event handling: on box mouse click
    	// 1. get currently selected contact
    	// 2. determine currently hovered icon
    	// 3. display secondary message menu
    	messageMakerBox.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				
				User receiver = contactList.getSelectionModel().getSelectedItem();
				Label recipientLabel = new Label("Recipient: " + receiver.toString());
				
				if (assistIcon.isHover()) {

					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), assistIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.ASSIST, customMessage.getText());
							
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
													
							
							
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(assistIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(assistIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
					
				} else if (clientIcon.isHover()) {
					
					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), clientIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.CLIENT_UPDATE, customMessage.getText());
							
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
													
							
							
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(clientIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(clientIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
					
				} else if (deadlineIcon.isHover()) {
					
					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), deadlineIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.DEADLINE, customMessage.getText());
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
													
							
							
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(deadlineIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(deadlineIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
				} else if (downtimeIcon.isHover()) {
					
					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), downtimeIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.DOWNTIME, customMessage.getText());
							
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
													
							
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(downtimeIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(downtimeIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
				} else if (followUpIcon.isHover()) {
					
					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), followUpIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.FOLLOW_UP, customMessage.getText());
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
														
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(followUpIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(followUpIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
				} else if (genericIcon.isHover()) {
					
					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), genericIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.GENERIC, customMessage.getText());
							
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
													
							
							
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(genericIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(genericIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
				} else if (maintenanceIcon.isHover()) {
					
					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), maintenanceIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.MAINTENANCE, customMessage.getText());
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
													
							
							
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(maintenanceIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(maintenanceIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
				} else if (meetingIcon.isHover()) {
					
					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), meetingIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.MEETING, customMessage.getText());
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
													
							
							
							
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(meetingIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(meetingIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
				} else if (urgentIcon.isHover()) {
					
					messageMakerBox.getChildren().clear();
					messageMakerBox.getChildren().addAll(new Label("Selected message type: "), urgentIcon,
							recipientLabel, customMessage, submitMessage, goBackButton);
					
					
					// event handling: on submit click
					// 1. create new message with specified information
					// 2. attempt to send message
					// 3. if success: show message, send copy to sent box, wait, then reset state
					// 4. if fail: show message, send copy to outbox, wait, then reset state
					submitMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							Message msg = new Message(sender, receiver, MessageStyle.URGENT, customMessage.getText());
							
							
							try {
								MessageHandler.sendMessage(msg, outbox, sent);
								// success state!
								messageMakerBox.getChildren().add(new Text("Success!"));
								
							} catch (UnknownHostException ex) {
								
							}
													
							
							// reset state							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(urgentIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
							
							messageMakerBox.setVisible(false);
							messageMakerBox.setDisable(true);
							contactList.setDisable(false);
							
						} // end handle override (submit button)
					}); // end submit clicked
					
					// event handling: on back click
					// return to previous state					
					goBackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent e) {
							
							messageMakerBox.getChildren().clear();
							messageIcons.getChildren().add(urgentIcon);
							messageMakerBox.getChildren().addAll(new Label("Select your message type: "), messageIcons, cancelButton);
						
						} // end handle override (cancel button)
					}); // end goBack clicked
					
				}
			} // end handler override (menu box)
			
    	}); // end menu box clicked

    	/* END CONTACT LIST */
    	
    	
		
		/* INBOX/OUTBOX/SENT BOX */
		
		// prepare tables to show message boxes
		TableView<Message> inboxList = new TableView<Message>();
		TableView<Message> outboxList = new TableView<Message>();
		TableView<Message> sentList = new TableView<Message>();
		
		// create columns for each message attribute
		TableColumn<Message, User> fromColumn = new TableColumn<>("Sender");
		fromColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
		TableColumn<Message, User> toColumn = new TableColumn<>("Receiver");
		toColumn.setCellValueFactory(new PropertyValueFactory<>("receiver"));
		TableColumn<Message, MessageStyle> typeColumn = new TableColumn<>("Message Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
		TableColumn<Message, String> msgColumn = new TableColumn<>("Custom Message");
		msgColumn.setCellValueFactory(new PropertyValueFactory<>("customMessage"));
		TableColumn<Message, Date> dateColumn = new TableColumn<>("Date Created");
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("created"));
		
		inboxList.getColumns().addAll(fromColumn, typeColumn, msgColumn, dateColumn);
		outboxList.getColumns().addAll(toColumn, typeColumn, msgColumn, dateColumn);
		sentList.getColumns().addAll(toColumn, typeColumn, msgColumn, dateColumn);
		

    	Button deleteMessageIn = new Button("Delete");
    	Button deleteMessageOut = new Button("Delete");
    	Button deleteMessageSent = new Button("Delete");
    	Button resendMessageOut = new Button("Resend");
    	Button resendMessageSent = new Button("Resend");
    	Button cancelIn = new Button("Cancel");
    	Button cancelOut = new Button("Cancel");
    	Button cancelSent = new Button("Cancel");
    	
		HBox manageMenuIn = new HBox(new Label("Message actions: "), deleteMessageIn, cancelIn);
		HBox manageMenuOut = new HBox(new Label("Message actions: "), 
				deleteMessageOut, resendMessageOut, cancelOut);
		HBox manageMenuSent = new HBox(new Label("Message actions: "), 
				deleteMessageSent, resendMessageSent, cancelSent);
		
		manageMenuIn.setDisable(true);
		manageMenuIn.setVisible(false);
		manageMenuOut.setDisable(true);
		manageMenuOut.setVisible(false);
		manageMenuSent.setDisable(true);
		manageMenuSent.setVisible(false);
		
		
		// put lists in scroll panes
		ScrollPane inboxScroller = new ScrollPane(inboxList);
    	ScrollPane outboxScroller = new ScrollPane(outboxList);
    	ScrollPane sentScroller = new ScrollPane(sentList);
    	
    	Button refreshInbox = new Button("Refresh");
    	Button refreshOutbox = new Button("Refresh");
    	Button refreshSent = new Button("Refresh");
    	    	
    	VBox inboxPage = new VBox(inboxScroller, manageMenuIn, refreshInbox);
    	VBox outboxPage = new VBox(outboxScroller, manageMenuOut, refreshOutbox);
    	VBox sentPage = new VBox(sentScroller, manageMenuSent, refreshSent);
    	
    	// onclicks
    	refreshInbox.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				inboxList.getItems().clear();
				
				for (int i = 0; i < inbox.size(); i++) {
					inboxList.getItems().add(inbox.get(i));
				}				
				inboxList.refresh();
				
			} // override handler
    		
    	}); // end refresh onclick
    	
    	refreshOutbox.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				outboxList.getItems().clear();
				
				for (int i = 0; i < outbox.size(); i++) {
					outboxList.getItems().add(outbox.get(i));
				}				
				outboxList.refresh();
				
			} // override handler
    		
    	});// end refresh onclick
    	
    	refreshSent.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				sentList.getItems().clear();
				
				for (int i = 0; i < sent.size(); i++) {
					sentList.getItems().add(sent.get(i));
				}				
				sentList.refresh();
				
			} // override handler
    		
    	});// end refresh onclick
        	
    	inboxList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if (inboxList.getSelectionModel().getSelectedItem() == null) return;
				manageMenuIn.setDisable(false);
				manageMenuIn.setVisible(true);	
				
				Message msg = inboxList.getSelectionModel().getSelectedItem();
				inboxList.setDisable(true);
				deleteMessageIn.setOnMouseClicked(new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {
						
						int i = 0;
						while (i < inbox.size()) {
							if (msg.equals(inbox.get(i))) {
								inbox.remove(i);
							} else i++;
						}
						
						inboxList.getItems().clear();
						
						for (i = 0; i < inbox.size(); i++) {
							inboxList.getItems().add(inbox.get(i));
						}				
						inboxList.refresh();
																		
						inboxList.setDisable(false);
						manageMenuIn.setDisable(true);
						manageMenuIn.setVisible(false);
						
					} // end override deletemessage handle
					
				}); // end deleteMessage onclick
								
			} // end inboxList handle override
    		
    	}); // end inboxList clicked
    	
    	
    	cancelIn.setOnMouseClicked(new EventHandler<MouseEvent>() {
    		
    		@Override
    		public void handle(MouseEvent e) {
    			
    			inboxList.setDisable(false);
				manageMenuIn.setDisable(true);
				manageMenuIn.setVisible(false);
    			
    		}
    	});
    	
    	
    	outboxList.setOnMouseClicked(new EventHandler<MouseEvent>() {	

			@Override
			public void handle(MouseEvent e) {
				
				manageMenuOut.setDisable(false);
				manageMenuOut.setVisible(true);
				
				Message msg = outboxList.getSelectionModel().getSelectedItem();
				outboxList.setDisable(true);
				
				deleteMessageOut.setOnMouseClicked(new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {
						
						int i = 0;
						while (i < outbox.size()) {
							if (msg.equals(outbox.get(i))) {
								outbox.remove(i);
							} else i++;
						}
						
						outboxList.getItems().clear();
						
						for (i = 0; i < outbox.size(); i++) {
							outboxList.getItems().add(outbox.get(i));
						}				
						outboxList.refresh();
												
						outboxList.setDisable(false);
						manageMenuOut.setDisable(true);
						manageMenuOut.setVisible(false);
						
					} // end override deletemessage handle
					
				}); // end deleteMessage onclick
				
				resendMessageOut.setOnMouseClicked(new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {

						try {
							MessageHandler.sendMessage(msg, outbox, sent);
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						}
						
						int i = 0;
						while (i < sent.size()) {
							if (msg.equals(sent.get(i))) {
								outbox.remove(i);
							} else i++;
						}	
						outboxList.getItems().clear();
						
						for (i = 0; i < outbox.size(); i++) {
							outboxList.getItems().add(outbox.get(i));
						}				
						outboxList.refresh();
						
						outboxList.setDisable(false);
						manageMenuOut.setDisable(true);
						manageMenuOut.setVisible(false);
					} // end override resendmessage handle
					
				}); // end resendmessage onclick
								
			} // end outbox handle override
    		
    	}); // end outbox clicked
    	
    	cancelOut.setOnMouseClicked(new EventHandler<MouseEvent>() {
    		
    		@Override
    		public void handle(MouseEvent e) {
    			
    			outboxList.setDisable(false);
				manageMenuOut.setDisable(true);
				manageMenuOut.setVisible(false);
    			
    		}
    	});
    	
    	
    	sentList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				
				manageMenuSent.setDisable(false);
				manageMenuSent.setVisible(true);
				Message msg = sentList.getSelectionModel().getSelectedItem();
				sentList.setDisable(true);
				
				deleteMessageSent.setOnMouseClicked(new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {
						
						int i = 0;
						while (i < sent.size()) {
							if (msg.equals(sent.get(i))) {
								sent.remove(i);
							} else i++;
						}
						
						sentList.getItems().clear();
						
						for (i = 0; i < sent.size(); i++) {
							sentList.getItems().add(sent.get(i));
						}				
						sentList.refresh();
												
						sentList.setDisable(false);
						manageMenuSent.setDisable(true);
						manageMenuSent.setVisible(false);
						
					} // end override deletemessage handle
					
				}); // end deleteMessage onclick
				
				resendMessageSent.setOnMouseClicked(new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {

						try {
							MessageHandler.sendMessage(msg, outbox, sent);
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						}
						
						sentList.getItems().clear();
						
						for (int i = 0; i < sent.size(); i++) {
							sentList.getItems().add(sent.get(i));
						}
						sentList.refresh();
						
						sentList.setDisable(false);
						manageMenuSent.setDisable(true);
						manageMenuSent.setVisible(false);
						
					} // end override resendmessage handle
					
				}); // end resendmessage onclick
								
			} // end outbox handle override
    		
    	}); // end outbox clicked
    	
    	cancelSent.setOnMouseClicked(new EventHandler<MouseEvent>() {
    		
    		@Override
    		public void handle(MouseEvent e) {
    			
    			sentList.setDisable(false);
				manageMenuSent.setDisable(true);
				manageMenuSent.setVisible(false);
    			
    		}
    	});
    	/* END INBOX/OUTBOX/SENT BOX */
		
				
		
		/* OPTIONS */
		
    	// make buttons for menu
		Button backupButton = new Button("Make Backup");
		Button restoreButton = new Button("Restore from Backup");
		Button reconnectButton = new Button("Force Reconnect");
		
		reconnectButton.setAlignment(Pos.CENTER_RIGHT);
		backupButton.setAlignment(Pos.CENTER_RIGHT);
		restoreButton.setAlignment(Pos.CENTER_RIGHT);
		
		HBox backupBox = new HBox(new ImageView(new Image(new File("icons/BACKUP.png").toURI().toString())), backupButton);	
		HBox restoreBox = new HBox(new ImageView(new Image(new File("icons/RESTORE.png").toURI().toString())), restoreButton);
		HBox reconnectBox = new HBox(new ImageView(new Image(new File("icons/RECONNECT.png").toURI().toString())), reconnectButton);
		backupBox.setAlignment(Pos.CENTER);
		restoreBox.setAlignment(Pos.CENTER);
		reconnectBox.setAlignment(Pos.CENTER);
		
		VBox optionsPage = new VBox(backupBox, restoreBox, reconnectBox);
		

		DirectoryChooser chooser = new DirectoryChooser();
		
		
		backupButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent e) {

				try {
					
				File datapath = chooser.showDialog(primaryStage);
						
				FileIOHandler.backup(inbox, outbox, sent, contacts, datapath.getAbsolutePath());

				} catch (NullPointerException ex) {
					// nothing
				}
			} // end handle override
			
		}); // end backup button clicked
		
		restoreButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				
				try {
					File datapath = chooser.showDialog(primaryStage);
					File inPath = new File(datapath + "\\inbox.dat");
					File outPath = new File(datapath + "\\outbox.dat");
					File sentPath = new File(datapath + "\\outbox.dat");
					File contactsPath = new File(datapath + "\\contactList.dat");
					// if ok, restore from file
					if (inPath.exists() && outPath.exists() && sentPath.exists() && contactsPath.exists()) {
						FileIOHandler.restore(inbox, outbox, sent, contacts, datapath.getAbsolutePath());
						
					}
				} catch (NullPointerException ex) {
					// do nothing. it's ok to change your mind
				}
				
				
				
			} // end handle override
			
		}); // end restore button clicked
		
		reconnectBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent e) {
				contactList.refresh();
				inboxList.refresh();
				outboxList.refresh();
				sentList.refresh();
			} // end handle override
			
		}); // end reconnect button clicked
		
		/* END OPTIONS */
    	
    	
    	/* GENERAL */
    	
    	// prep tab pane and tabs
    	TabPane tabs = new TabPane();
    	tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    	
    	Tab contactTab = new Tab("Contacts", contactPage);
    	Tab inboxTab = new Tab("Inbox", inboxPage);
    	Tab outboxTab = new Tab("Outbox", outboxPage);
    	Tab sentTab = new Tab("Sent Box", sentPage);
    	Tab optionsTab = new Tab("Options", optionsPage);
    	
    	// add tabs to pane
    	tabs.getTabs().add(contactTab);
    	tabs.getTabs().add(inboxTab);
    	tabs.getTabs().add(outboxTab);
    	tabs.getTabs().add(sentTab);
    	tabs.getTabs().add(optionsTab);
    	
    	// add tab pane to scene
    	Scene scene = new Scene(tabs);
    	
    	// display stage
    	primaryStage.setScene(scene);
    	primaryStage.setTitle("LOMS");
    	primaryStage.show();
    	
    	/* END GENERAL */
    	
    	primaryStage.setOnCloseRequest(e -> {
    		FileIOHandler.backup(inbox, outbox, sent, contacts, ""); // backup to default location
    		System.exit(0);
    	});
    	
    } // end override start
	
} // end class test