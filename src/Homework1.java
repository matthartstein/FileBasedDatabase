/*
 * Title: Database Management Homework #1
 * Authors: Matthew Hartstein & Siri Sirigineedi
 * Date: 2/5/2020
 * Synopsis: This program replicates a simple file-based database through menu interaction
 *           with the user.
 */


import java.io.*;
import java.util.*;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import java.nio.charset.StandardCharsets;

public class Homework1 {
	
	// ----------------------------------------------------------------------------------------
	// Class Variables and Constants
	// ----------------------------------------------------------------------------------------
	static final int RECORD_SIZE = 6;
	static final int NUM_RECORDS = 500;
		
	static String filename = null;
	static String companyName = null;
	static int menuInput = 0;
	static boolean isOpen = false;
	
	static Scanner scan = new Scanner(System.in);
	static Path config = Paths.get("./Fortune_500_HQ.config");
	static Path data = Paths.get("./Fortune_500_HQ.data");
	static Path overflow = Paths.get("./Fortune_500_HQ.overflow");
	static Path output = Paths.get("./output.txt");
	
	static int maxLengthOfRank;
	static int maxLengthOfName;
	static int maxLengthOfCity;
	static int maxLengthOfState;
	static int maxLengthOfZip;
	static int maxLengthOfEmployees;
	
	// ----------------------------------------------------------------------------------------
	// Menu Functions
	// ----------------------------------------------------------------------------------------
	public static void createDatabase(String file) throws IOException {
		
		if(isOpen == true) {
			System.out.println("Close your database before creating a new one.");
			programLoop();
		}
		else {

			Files.newOutputStream(config, CREATE, APPEND);
			Files.newOutputStream(data, CREATE, APPEND);
			Files.newOutputStream(overflow, CREATE, APPEND);

			// Open, read, and extract CSV file
			BufferedReader br = new BufferedReader((new FileReader(file)));
			String fieldswithcommas = br.readLine();
			ArrayList<String> strList = new ArrayList<>();
			String path = file;
			String line;
			try{
				br = new BufferedReader(new FileReader(new File(path)));
				br.readLine();
				while ((line = br.readLine())!=null){
					strList.add(line);
				}
			}catch (IOException e){
				System.out.println(e);
			}

			String subStr;
			String maxName = "";
			int maxNumber = 0; 	//referring to length of number not actual size of the value of the number
			String maxCity = "";
			String maxState = "";
			String maxZip = "";
			String maxEmployees = "";

			for(String str: strList) {
				int first = str.indexOf(",");
				int second = str.indexOf(",",first+1);
				int third = str.indexOf(",",second+1);
				int fourth = str.indexOf(",",third+1);
				int fifth = str.indexOf(",",fourth+1);

				subStr = str.substring(first+1,second);
				String number = str.substring(0,str.indexOf(','));
				String city = str.substring(second+1,third);
				String state = str.substring(third+1,fourth);
				String zip = str.substring(fourth+1,fifth);
				String employees = str.substring(fifth+1);

				if(maxName.length() < subStr.length()){
					maxName = subStr;
				}
				if(maxNumber < number.length()){
					maxNumber = number.length();
				}
				if(maxCity.length() < city.length()){
					maxCity = city;
				}
				if(maxState.length() < state.length()){
					maxState = state;
				}
				if(maxZip.length()<zip.length()){
					maxZip = zip;
				}
				if(maxEmployees.length()<employees.length()){
					maxEmployees = employees;
				}
				Files.write(data, str.getBytes(), StandardOpenOption.APPEND);
			}

			maxLengthOfRank = maxNumber;
			maxLengthOfName = maxName.length();
			maxLengthOfCity = maxCity.length();
			maxLengthOfState = maxState.length();
			maxLengthOfZip = maxZip.length();
			maxLengthOfEmployees = maxEmployees.length();

			/*
			// Print contents
			for(int a = 0; a < NUM_RECORDS; a++) {
				System.out.println(strList.get(a));
			}
			*/

			ArrayList<String> lineList = new ArrayList<>();
			for(String str: strList) {
				String lineForData = "";
				int first = str.indexOf(",");
				int second = str.indexOf(",",first+1);
				int third = str.indexOf(",",second+1);
				int fourth = str.indexOf(",",third+1);
				int fifth = str.indexOf(",",fourth+1);
				String number = str.substring(0,str.indexOf(','));
				String name = str.substring(first+1,second);
				String city = str.substring(second+1,third);
				String state = str.substring(third+1,fourth);
				String zip = str.substring(fourth+1,fifth);
				String employees = str.substring(fifth+1);
				lineForData = String.format("%"+-maxLengthOfRank+"s",number)+","
						+String.format("%"+-maxLengthOfName+"s",name)+","
						+String.format("%"+-maxLengthOfCity+"s",city)+","
						+String.format("%"+-maxLengthOfState+"s",state)+","
						+String.format("%"+-maxLengthOfZip+"s",zip)+","
						+String.format("%"+-maxLengthOfEmployees+"s",employees)
						+",";
				lineList.add(lineForData);
				//System.out.println(lineForData);
				Files.write(data,lineList, StandardCharsets.UTF_8);

				// Write contents to CONFIG file
				ArrayList<String> listForConfig = new ArrayList<>();
				listForConfig.add(String.valueOf(lineList.size()));
				listForConfig.add(fieldswithcommas);
				listForConfig.add(String.valueOf(maxLengthOfRank));
				listForConfig.add(String.valueOf(maxLengthOfName));
				listForConfig.add(String.valueOf(maxLengthOfCity));
				listForConfig.add(String.valueOf(maxLengthOfState));
				listForConfig.add(String.valueOf(maxLengthOfZip));
				listForConfig.add(String.valueOf(maxLengthOfEmployees));
				Files.write(config,listForConfig,StandardCharsets.UTF_8);
			}
			System.out.println("Success!");
			isOpen = true;
		}
	}
	
	public static void openDatabase() throws IOException {
		
		File c = new File("./Fortune_500_HQ.config");
		File d = new File("./Fortune_500_HQ.data");
		File o = new File("./Fortune_500_HQ.overflow");
		
		boolean findConfig = c.exists();
		boolean findData = d.exists();
		boolean findOverflow = o.exists();

		while(isOpen == true) {
			System.out.println("Close the database before opening a new one. Try again.");
			break;
		}
		
		if(isOpen == false) {
			if((findConfig == true) && (findData == true) && (findOverflow == true)) {
				System.out.println("Unable to open database. Closing opened databases now. . .");
				isOpen = true;
				closeDatabase();
			}
			else {
				createDatabase(filename);
				isOpen = true;
			}
		}
	}
	
	public static void closeDatabase() throws IOException {
	
		File c = new File("./Fortune_500_HQ.config");
		File d = new File("./Fortune_500_HQ.data");
		File o = new File("./Fortune_500_HQ.overflow");
		
		boolean cc = c.exists();
		boolean dd = d.exists();
		boolean oo = o.exists();
		
		if(isOpen == false) {
			if((cc == true) && (dd == true) && (oo == true)) {
				//System.out.println("There is no database to close.");
				Files.deleteIfExists(config);
				Files.deleteIfExists(data);
				Files.deleteIfExists(overflow);
				System.out.println("Successfully closed database.");
				isOpen = false;
			}
		}
		else {
			Files.deleteIfExists(config);
			Files.deleteIfExists(data);
			Files.deleteIfExists(overflow);
			System.out.println("Successfully closed database.");
			isOpen = false;
		}
	}
	
	public static String getRecord(RandomAccessFile Din, int recordNum) throws IOException
	{
		String record = "NOT_FOUND";
//		if ((recordNum >=1) && (recordNum <= NUM_RECORDS))

		Din.seek(0); // return to the top fo the file
		Din.skipBytes((recordNum-1) * 81);
		record = Din.readLine();
		return record;
	}
	
	public static String binarySearch(RandomAccessFile Din, String id) throws IOException
	{
		boolean found = false;
//		System.out.println(getRecord(Din,501));
		int low = 0;
		int middle;
		int high = NUM_RECORDS-1;
		String MiddleId;
		String record = "";

		int length = 0;

		for(length = id.length(); length < 38; length++){
			id = id + " ";
		}

		while (low<=high)
		{
			middle = (low + high) / 2;

			record = getRecord(Din, middle);
			MiddleId = record.substring(4,42);

			int result = MiddleId.compareTo(id);



//			System.out.println("Middle ID = " + MiddleId);

			if (result == 0)
			{
				found = true;
				return record;

			}   // ids match
			//Found = true;
			else if (result < 0){
				low = middle + 1;
			}

			else{
				high = middle - 1;
			}

//			System.out.println(High);
//			System.out.println(Middle);
//			System.out.println(Low);
		}

		return "NOT_FOUND";
	}
	
	public static String[] getRecord1(RandomAccessFile Din, int recordNum) throws IOException
	{
		String record = "NOT_FOUND";
//		if ((recordNum >=1) && (recordNum <= NUM_RECORDS))

		Din.seek(4); // return to the top fo the file
		String fields[] = new String[6];
		fields = Din.readLine().trim().split(",");

		return fields;
	}
	
	public static String[] displayRecord(String record) throws IOException {
		String[] fields;
		String[] data;
		RandomAccessFile Din = new RandomAccessFile("./Fortune_500_HQ.config","r");
		fields = getRecord1(Din,1);
		Din.close();
		data = record.split(",");
		String line = "";
		for(String individual: data){
			individual = individual.trim();
//			System.out.println(individual);
		}
		for(String field: fields){
//			System.out.println(field);
		}
		for(int i =0; i<6;i++){
			line = line + fields[i] + ":" + data[i] + "\n";
		}
//		System.out.println(line);
//		String record = "Inside displayRecord()";
		System.out.println(line);
		return data;
	}

	public static void updateRecord(String fieldForChange, String record, String[] datas) throws IOException {
		System.out.println("Inside updateRecord()");
		RandomAccessFile Din = new RandomAccessFile("./Fortune_500_HQ.data","r");
		System.out.println("What would you like to change it to?");
		String change = scan.nextLine();
		String lineForData = "";
		if(fieldForChange.equals("RANK")){
			lineForData = String.format("%"+-maxLengthOfRank+"s",change)+","
					+String.format("%"+-maxLengthOfName+"s",datas[1])+","
					+String.format("%"+-maxLengthOfCity+"s",datas[2])+","
					+String.format("%"+-maxLengthOfState+"s",datas[3])+","
					+String.format("%"+-maxLengthOfZip+"s",datas[4])+","
					+String.format("%"+-maxLengthOfEmployees+"s",datas[5])
					+",";
		}
		else if(fieldForChange.equals("CITY")){
			lineForData = String.format("%"+-maxLengthOfRank+"s",datas[0])+","
					+String.format("%"+-maxLengthOfName+"s",datas[1])+","
					+String.format("%"+-maxLengthOfCity+"s",change)+","
					+String.format("%"+-maxLengthOfState+"s",datas[3])+","
					+String.format("%"+-maxLengthOfZip+"s",datas[4])+","
					+String.format("%"+-maxLengthOfEmployees+"s",datas[5])
					+",";
		}
		else if(fieldForChange.equals("STATE")){
			lineForData = String.format("%"+-maxLengthOfRank+"s",datas[0])+","
					+String.format("%"+-maxLengthOfName+"s",datas[1])+","
					+String.format("%"+-maxLengthOfCity+"s",datas[2])+","
					+String.format("%"+-maxLengthOfState+"s",change)+","
					+String.format("%"+-maxLengthOfZip+"s",datas[4])+","
					+String.format("%"+-maxLengthOfEmployees+"s",datas[5])
					+",";
		}
		else if(fieldForChange.equals("ZIP")) {
			lineForData = String.format("%" + -maxLengthOfRank + "s", datas[0]) + ","
					+ String.format("%" + -maxLengthOfName + "s", datas[1]) + ","
					+ String.format("%" + -maxLengthOfCity + "s", datas[2]) + ","
					+ String.format("%" + -maxLengthOfState + "s", datas[3]) + ","
					+ String.format("%" + -maxLengthOfZip + "s", change) + ","
					+ String.format("%" + -maxLengthOfEmployees + "s", datas[5])
					+ ",";
		}
		else if(fieldForChange.equals("EMPLOYEES")) {
			lineForData = String.format("%" + -maxLengthOfRank + "s", datas[0]) + ","
					+ String.format("%" + -maxLengthOfName + "s", datas[1]) + ","
					+ String.format("%" + -maxLengthOfCity + "s", datas[2]) + ","
					+ String.format("%" + -maxLengthOfState + "s", datas[3]) + ","
					+ String.format("%" + -maxLengthOfZip + "s", datas[4]) + ","
					+ String.format("%" + -maxLengthOfEmployees + "s", change)
					+ ",";
		}
		List<String> fileContent = new ArrayList<>(Files.readAllLines(data,StandardCharsets.UTF_8));
		for(int i =0; i < fileContent.size();i++){
			if(fileContent.get(i).equals(record)){
				fileContent.set(i,lineForData);
			}
		}
		Din.close();
	}
	
	public static void createReport() throws IOException {		
		
		if(isOpen == false) {
			System.out.println("Open or create a new database to make a report.");
		}
		else {
			
			String line = null;
			ArrayList<String> records = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader("./Fortune_500_HQ.data"));
			
			for(int i = 0; i < 10; i++) {
				line = reader.readLine();
				records.add(line);
			}
			
			Files.newOutputStream(output, CREATE, APPEND);
			Files.write(output,records,StandardCharsets.UTF_8);
			reader.close();
			System.out.println("Report successfully generated in output file.");
		}
	}
	
	public static void addRecord() {
		if(isOpen == false) {
			System.out.println("Open a database to add a record");
		}
	}
	
	public static void deleteRecord() {
		if(isOpen == false) {
			System.out.println("Open a database to delete a record");
		}
	}
	
	public static void quit() throws IOException {
		
		// Check if database is closed before allowing the user to quit
		while(isOpen == true) {
			System.out.println("Attempting to close database. . .");
			closeDatabase();
		}
		
		if(isOpen == false) {
			System.out.println("Exiting");
			System.exit(0);
		}
	}
	
	// ----------------------------------------------------------------------------------------
	// Custom Functions
	// ----------------------------------------------------------------------------------------
	
	public static void checkFile() {		
		System.out.println("Enter the name of your file:");
		filename = scan.next() + ".csv";
	}

	public static void getName() throws IOException {
				
		while(isOpen == false) {
			System.out.println("You must open or create a database to use the search feature.");
			return;
		}
		
		if(isOpen == true) {
			System.out.println("Enter company name:");
			Scanner in = new Scanner(System.in);
			companyName = in.nextLine();
			in.close();
		}
	}
	
	public static void getInput() {
		System.out.println("What would you like to do?");
		try {
			menuInput = scan.nextInt();
		}
		catch(Exception e) {
			scan.close();
		}		
	}
	
	public static void printMenu() {	
		System.out.println("\n1.) Create New Database");
		System.out.println("2.) Open Database");
		System.out.println("3.) Close Database");
		System.out.println("4.) Display Record");
		System.out.println("5.) Update Record");
		System.out.println("6.) Create Report");
		System.out.println("7.) Add Record");
		System.out.println("8.) Delete Record");
		System.out.println("9.) Quit");
	}
	
	public static void programLoop() throws IOException {
		
		while(menuInput != 9) {
			
			printMenu();
			getInput();
			
			if(menuInput == 1) {
				checkFile();
				try {
					createDatabase(filename);
				} catch(Exception e) {
					System.out.println("Could not locate file. Try again.");}
			}
			else if(menuInput == 2) {
				checkFile();
				openDatabase();
			}
			else if(menuInput == 3) {
				closeDatabase();
			}
			else if(menuInput == 4) {
				if(isOpen == true) {
					System.out.println("Enter company name");
					String companyName = scan.nextLine();
					RandomAccessFile Din = new RandomAccessFile("./Fortune_500_HQ.data","r");
					companyName = binarySearch(Din,companyName.toUpperCase());
					Din.close();
//					System.out.println("Binary Search Results: " + companyName);
					displayRecord(companyName);
					printMenu();
					getInput();
				}
				else {
					System.out.println("You must open or create a database to search.");
				}
			}
			else if(menuInput == 5) {
				try {
					System.out.println("Enter company name");
					String companyName = scan.nextLine();
					RandomAccessFile Din = new RandomAccessFile("./Fortune_500_HQ.data","r");
					companyName = binarySearch(Din,companyName.toUpperCase());
					Din.close();
					System.out.println("Which field would you like to change excluding the primary key name?(RANK,NAME,CITY,STATE,ZIP,EMPLOYEES)");
					String fieldForChange = scan.nextLine().toUpperCase();
					String[] data = displayRecord(companyName);
					updateRecord(fieldForChange,companyName,data);
				} catch(Exception e) {System.out.println("Open a database before updating record.");}
			}
			else if(menuInput == 6) {
				createReport();
				printMenu();
				getInput();
			}
			else if(menuInput == 7) {
				addRecord();
			}
			else if(menuInput == 8) {
				deleteRecord();
			}
			else {
				System.out.println("Exiting");
				scan.close();
				System.exit(0);
			}
		}
		quit();
	}
	
	// ----------------------------------------------------------------------------------------
	// Main Program
	// ----------------------------------------------------------------------------------------
	public static void main(String[] args) throws IOException {
		programLoop();
	}
}