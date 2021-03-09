import java.io.Serializable;
import java.util.*;

public class Contact implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer contactID = 0;
	private String contactName = "";
	private String emailAddress = "";
	private List<String> contactNumber = null;
	
	Contact(Integer contactID, String contactName, String emailAddress, List<String> contactNumber){
		this.contactID = contactID;
		this.contactName = contactName;
		this.emailAddress = emailAddress;
		this.contactNumber = contactNumber;
	}
	
	public Contact getReference() {
		return this;
	}
	
	public Integer getContactId() {
		return this.contactID;
	}
	
	public void setContactID(Integer contactID) {
		this.contactID = contactID;
	}
	
	public String getContactName() {
		return this.contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public String getEmailAddress() {
		return this.emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress= emailAddress;
	}
	
	public List<String> getContactNumber() {
		return this.contactNumber;
	}
	
	public void setContactNumber(List<String> contactNumber) {
		this.contactNumber = contactNumber;
	}
	
	public void display() {
		System.out.println("\nName: " + this.contactName + "\nEmail Address: " 
				+ this.emailAddress + "\nContact Number: " + this.contactNumber + "\n");
	}
	
}