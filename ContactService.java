import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class ContactService {
	static Connection conn = null;
	
//	Connect to database
	public static Connection databaseConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String url = "jdbc:oracle:thin:@localhost:1521:XE";
		
		try {
			conn = DriverManager.getConnection(url,"mohit","mohit");
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return conn;
	}
	
	public void addContact(Contact contact, List<Contact> contacts) {
		contacts.add(contact);
		System.out.println("Contact Added\n");
	}
	
	public void removeContact(Contact contact, List<Contact> contacts) throws ContactNotFoundException {
		Contact con = null;
		for (Contact c : contacts)
			if (c.getContactId() == contact.getContactId())
				con = c;
		if (con == null) {
			throw new ContactNotFoundException("Contact not found");
			
		}
		else {
			contacts.remove(con);
			System.out.println("\nContact Removed\n");
		}
	}
	
	public Contact searchContactByName(String name, List<Contact> contacts) throws ContactNotFoundException{
		for (Contact c : contacts) {
			if (c.getContactName().equals(name)) {
				return c;
			}
		}
		throw new ContactNotFoundException("Contact not found");
	}
	
	public List<Contact> searchContactByNumber(String number, List<Contact> contacts) throws ContactNotFoundException {
		List<Contact> contactList = new ArrayList<Contact>();
		for (Contact c : contacts) {
			for (String contactNumber : c.getContactNumber())
				if (contactNumber.contains(number)) {
					contactList.add(c);
					break;
				}
		}
		if (contactList.size() == 0)
			throw new ContactNotFoundException("Contact not in the provided list");
		
		return contactList;
	}
	
	public void addContactNumber(int contactId, String contactNo, List<Contact> contacts) {
		List<String> contactNumber = new ArrayList<String>();
		for(String s : contactNo.split(","))
			contactNumber.add(s);
		for (Contact c : contacts) {
			if (c.getContactId() == contactId) {
				c.setContactNumber(contactNumber);
			}
		}
	}
	
	public void sortContactsByName(List<Contact> contacts) {
		Comparator<Contact> cm = Comparator.comparing(Contact::getContactName);
		Collections.sort(contacts, cm);
	}
	
	public void readContactsFromFile(List<Contact> contacts, String fileName) {
		File file = new File(fileName);
		Scanner read;
		try {
			read = new Scanner(file);
			String [] contactInformation = null;
			List<String> contactNumber = new ArrayList<String>();
			
			while (read.hasNextLine()) {
				Integer contactID = 0;
				String contactName = "", emailAddress = "", contactNumberString = "";
				contactInformation = read.nextLine().split(",");
				for (int i = 0; i < contactInformation.length; i++) {
					if (i == 0)
						contactID = Integer.parseInt(contactInformation[i]);
					else if (i == 1)
						contactName = contactInformation[1];
					else if (i == 2)
						emailAddress = contactInformation[2];
					else {
						contactNumberString += contactInformation[i] + ",";
						for (String s : contactNumberString.split(","))
							contactNumber.add(s);
					}
				}
				Contact c = new Contact(contactID, contactName, emailAddress, contactNumber);
				contacts.add(c);
			}
			read.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		System.out.println("Data inserted!");
	}
	
	public void serializeContactDetails(List<Contact> contacts , String fileName) {
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		
		try {
			fout = new FileOutputStream(fileName, true);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(contacts);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	

	@SuppressWarnings("unchecked")
	public List<Contact> deserializeContact(String fileName) {
		ObjectInputStream ois = null;
		List<Contact> contacts = null;
		try {
			FileInputStream fin = new FileInputStream(fileName);
			ois = new ObjectInputStream(fin);
			contacts = (List<Contact>) ois.readObject();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return contacts;
	}
	
	public Set<Contact> populateContactFromDb(){
		
		Set<Contact> contactSet = new HashSet<Contact>();
		conn = databaseConnection();
		try {
			String sql = "SELECT * FROM contact_tbl";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				List<String> contactNumber = new ArrayList<String>();
				Integer contactID = 0;
				String contactName = "", emailAddress = "", contactNumberString = "";
				if (rs.getInt("CONTACTID") != 0)
					contactID = rs.getInt("CONTACTID");
				if (rs.getString("CONTACTNAME") != null)
					contactName = rs.getString("CONTACTNAME");
				if (rs.getString("CONTACTEMAIL") != null)
					emailAddress = rs.getString("CONTACTEMAIL");
				if (rs.getString("CONTACTLIST") != null)
					contactNumberString = rs.getString("CONTACTLIST");
				for (String s : contactNumberString.split(","))
					contactNumber.add(s);
				Contact c = new Contact(contactID, contactName, emailAddress, contactNumber);
				contactSet.add(c);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contactSet;
	}
	
	public Boolean addContacts(List<Contact> existingContact,Set<Contact> newContacts) {
		try {
			for (Contact c : newContacts)
				existingContact.add(c);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		Scanner read = new Scanner(System.in);
		List<Contact> contacts = new ArrayList<Contact>();
		ContactService cs = new ContactService();
		int choice;
		Integer contactID;
		String contactName, emailAddress, contactNumberString;
		List<String> contactNumber = null;
		
		
			System.out.println("\n1)Display contacts\n2)Add contact\n3)Remove contact\n"
					+ "4)Search by contact name\n5)Search by contact number\n6)Set contact number\n"
					+ "7)Sort contact by name\n8)Add contacts from file\n"
					+ "9)Serialize contact details\n10)Deserialize contact details\n11)Populate from DB\n"
					+ "12)Add new contacts to existing ones");
			System.out.print("Your choice: ");
			choice = read.next().charAt(0);
			switch(choice) {
			
			case 1:
				for (Contact c : contacts)
					c.display();
				break;
			
			case 2:
				System.out.print("\nEnter contact ID: ");
				contactID = read.nextInt();
				read.nextLine();
				System.out.print("\nEnter contact name: ");
				contactName = read.nextLine();
				System.out.print("\nEnter contact email address: ");
				emailAddress = read.nextLine();
				System.out.print("\nEnter contact number: ");
				contactNumberString = read.nextLine();
				contactNumber = new ArrayList<String>();
				for (String s : contactNumberString.split(","))
						contactNumber.add(s);
				cs.addContact(new Contact(contactID, contactName, emailAddress, contactNumber), contacts);
				break;
			
			case 3:
				System.out.print("\nEnter contact ID: ");
				contactID = read.nextInt();
				read.nextLine();
				System.out.print("\nEnter contact name: ");
				contactName = read.nextLine();
				System.out.print("\nEnter contact email address: ");
				emailAddress = read.nextLine();
				System.out.print("\nEnter contact number: ");
				contactNumberString = read.nextLine();
				contactNumber = new ArrayList<String>();
				for (String s : contactNumberString.split(","))
						contactNumber.add(s);
				try {
					cs.removeContact(new Contact(contactID, contactName, emailAddress, contactNumber), contacts);
				} catch (ContactNotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 4:
				System.out.print("\nEnter contact name: ");
				contactName = read.next();
				try {
					Contact c = cs.searchContactByName(contactName, contacts);
					c.display();
				} catch (ContactNotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 5:
				System.out.print("\nEnter contact number: ");
				contactNumberString = read.next();
				try {
					List<Contact> c = cs.searchContactByNumber(contactNumberString, contacts);
					for (Contact ct : c)
						ct.display();
				} catch (ContactNotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 6:
				System.out.print("\nEnter contact ID: ");
				contactID = read.nextInt();
				read.nextLine();
				System.out.print("\nEnter contact number: ");
				contactNumberString = read.nextLine();
				cs.addContactNumber(contactID, contactNumberString, contacts);
				break;
				
			case 7:
				cs.sortContactsByName(contacts);
				break;
				
			case 8:
				cs.readContactsFromFile(contacts, "./src/javaAssignment10/Contact.txt");
				break;
			
			case 9:
				cs.serializeContactDetails(contacts, "./src/javaAssignment10/output.ser");
				break;
				
			case 10:
				List<Contact> c = cs.deserializeContact("./src/javaAssignment10/output.ser");
				for (Contact ct : c)
					ct.display();
				break;
				
			case 11:
				Set<Contact> contactSet  = new HashSet<Contact>();
				contactSet = cs.populateContactFromDb();
				Iterator itr = contactSet.iterator();
				while (itr.hasNext()) {
					Contact ct = (Contact) itr.next();
					ct.display();
				}
				break;
			case 12:
				contactSet  = new HashSet<Contact>();
				contactSet = cs.populateContactFromDb();
				if (cs.addContacts(contacts, contactSet)) {
					System.out.println("\nNew contacts added\n");
				}
				else {
					System.out.println("\nError please try again\n");
				}
					
			}
			

		
		read.close();
	}

}