package com.fishdan.text;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.fishdan.DataFileObject;
import com.fishdan.objects.*;

public class L2Parser {

	static final String topDirectory = "C:\\data\\L2";
	static int COLUMN = 0;
	static int COUNT = 1;
	static int TABLE = 2;
	static int FIELD = 3;
	static int NAME = 4;
	static int TYPE = 5;

	static HashMap<String, LoaderObject> fieldMap = new HashMap<String, LoaderObject>();

	public static void main(String[] args) {
//		if(args.length!=1) {
//			System.out.println("Usage: Java L2Parser [DataDir] [config] [run]");
//			System.exit(-1);
//		}
//		String path=args[0];
//		File dataDir=new File(path);  
		// createOrderedFields();
		fieldMap = loadOrderedFields("c:/data/orderedfields.csv");
		File[] files = getFiles();
		for (int x = 0; x < files.length; x++) {
			processFile(files[x]);
		}
		// loadConfig();
		// createTablesScript();
		// processData();
		processHollyFile();

	}

	private static HashMap<String, LoaderObject> loadOrderedFields(String location) {
		// TODO Auto-generated method stub
		try {
			BufferedReader in = new BufferedReader(new FileReader(location));
			// skip header
			String line = in.readLine();
			while ((line = in.readLine()) != null) {
				String[] data = line.split(",", -1);
				LoaderObject lo = new LoaderObject();
				lo.setColumn(data[COLUMN]);
				lo.setCount(data[COUNT]);
				lo.setField(data[FIELD]);
				lo.setName(data[NAME]);
				lo.setTable(data[TABLE]);
				lo.setType(data[TYPE]);
				if (!lo.getTable().isEmpty()) {
					fieldMap.put(lo.getColumn(), lo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldMap;

	}

	private static void processHollyFile() {
		// read holly's sheets

	}

	private static void loadData() {
		// File orderedFields=new File()
	}

	public static File[] getFiles() {
		File dataDir = new File(topDirectory);
		return dataDir.listFiles();
	}

	public static String processFile(File file) {
		if (!file.getName().endsWith("csv")) {
			return "skipping " + file.getName();
		}
		try {
			int lines = (int) Files.lines(Paths.get(file.getAbsolutePath())).parallel().count();
			int processed=0;
			FileReader fr = new FileReader(file);
			BufferedReader in = new BufferedReader(fr);
			String header = in.readLine().toLowerCase();
			ArrayList<String> cols = null;
			if (header != null) {
				System.out.println(header);
				System.out.println(header.length());
				cols = splitAndTrim(header);
			}
			String line = "";
			StopWatch watch=new StopWatch();
			while ((line = in.readLine()) != null) {
				++processed;
//				if(processed<160) {
//					continue;
//				}
				watch.reset();
				watch.start();
				Person person = new Person();
				HashMap<String, Address> addresses = new HashMap<String, Address>();
				HashMap<String, Email> emails = new HashMap<String, Email>();
				HashMap<String, Externalid> xids = new HashMap<String, Externalid>();
				HashMap<String, Detail> details = new HashMap<String, Detail>();
				HashMap<String, Phone> phones = new HashMap<String, Phone>();
				HashMap<String, LinkPersonIssue> issueLinks = new HashMap<String, LinkPersonIssue>();
				HashMap<String, LinkPersonDetail> detailLinks = new HashMap<String, LinkPersonDetail>();
				ArrayList<HashMap<String, ?>> maps = new ArrayList<HashMap<String, ?>>();
				Collections.addAll(maps, addresses, emails, xids, details, phones, issueLinks, detailLinks);
				ArrayList<String> tokens = splitAndTrim(line);
				for (int x = 0; x < tokens.size(); x++) {
					String value = tokens.get(x);
					LoaderObject lo = fieldMap.get(cols.get(x));
					if (lo != null) {
						// work it
						try {
							if (lo.getTable().equalsIgnoreCase("Person")) {
								String methodName = "set" + StringUtils.capitalize(lo.getField());
								invokeMethod(value, person, methodName);
							}
							if (lo.getTable().equalsIgnoreCase("Address")) {
								Address address = addresses.get(lo.getType());
								if (address == null) {
									address = new Address();
									address.setType(lo.getType());
									addresses.put(lo.getType(), address);
								}
								String methodName = "set" + StringUtils.capitalize(lo.getField());
								invokeMethod(value, address, methodName);
							} else if (lo.getTable().equalsIgnoreCase("Email")) {
								Email email = emails.get(lo.getType());
								if (email == null) {
									email = new Email();
									email.setType(lo.getType());
									emails.put(lo.getType(), email);
								}
								String methodName = "set" + StringUtils.capitalize(lo.getField());
								invokeMethod(value, email, methodName);
							} else if (lo.getTable().equalsIgnoreCase("Externalid")) {
								Externalid externalid = xids.get(lo.getType());
								if (externalid == null) {
									externalid = new Externalid();
									externalid.setType(lo.getType());
									xids.put(lo.getType(), externalid);
								}
								String methodName = "set" + StringUtils.capitalize(lo.getField());
								invokeMethod(value, externalid, methodName);
							} else if (lo.getTable().equalsIgnoreCase("Issue") && !value.isEmpty()) {
								Issue issue = new Issue();
								issue.setName(lo.getName());
								List<FishObject> issueList = issue.getFromDatabase();
								if (issueList.size() == 0) {
									// doesn't exist yet
									issue.create();
								} else {
									issue = (Issue) issueList.get(0);
								}
								LinkPersonIssue lpi = new LinkPersonIssue();
								lpi.setIssueid(issue.getId());
								lpi.setValue(value);
								issueLinks.put(issue.getId().toString(), lpi);
							} else if (lo.getTable().equalsIgnoreCase("Detail")) {
								Detail detail = new Detail();
								detail.setName(lo.getName());
								if (!lo.getType().isEmpty()) {
									detail.setType(lo.getType());
								}
								detail.setValue(value);
								List<FishObject> detailList = detail.getFromDatabase();
								if (detailList.size() == 0) {
									// doesn't exist yet
									detail.create();
								} else {
									detail = (Detail) detailList.get(0);
								}
								LinkPersonDetail lpi = new LinkPersonDetail();
								lpi.setDetailid(detail.getId());
								lpi.setValue(value);
								detailLinks.put(detail.getId().toString(), lpi);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
				// all tokens parsed, time to save;
				// Save the person first to see if there is one like this
				List<FishObject> people = person.getFromDatabase();
				if (people.size() == 0) {
					// this is a unique person, save
					person.create();
				} else {
					System.out.println("Duplicate Person?");
					for (FishObject doppleganger : (people)) {
						// does this person have an address?
						Address ra = new Address();
						ra.setPersonid((Integer) doppleganger.getId());
						ra.setType("residential");
						List<FishObject> dbAddresses = ra.getFromDatabase();
						if (dbAddresses.size() == 0) {
							person = (Person) doppleganger;
						} else {
							for (FishObject ma : dbAddresses) {
								ra = (Address) ma;
								Address la = addresses.get("residential");
								if (ra.getCity().equalsIgnoreCase(la.getCity())
										&& ra.getStreet1().equalsIgnoreCase(la.getStreet1())
										&& ra.getState().equalsIgnoreCase(la.getState())
										&& ra.getZip().equals(la.getZip())) {
									// same person object with same name and address? ITS THE SAME PERSON
									person = (Person) doppleganger;
								}
							}
						}
					}
				}
				for (int x = 0; x < maps.size(); x++) {
					HashMap<String, FishObject> map = (HashMap) maps.get(x);
					for (String type : map.keySet()) {
						Personal po = (Personal) map.get(type);
						po.setPersonid(person.getId());
						po.update();
					}
				}
// we're done!!!
				watch.stop();
				System.out.println(processed+"/"+lines+" processed. Last line took "+watch.getTime()/1000.0);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AssignedIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * split and remove quotes
	 * 
	 * @param line
	 * @return
	 */
	private static ArrayList<String> splitAndTrim(String line) {
		StringBuffer wordBuffer = new StringBuffer();
		ArrayList<String> ar = new ArrayList<String>();
		boolean insideQuotes = false;
		char[] chars = line.toCharArray();
		for (int x = 0; x < chars.length; x++) {
			char c = chars[x];
			if (c == ',') {
				if (insideQuotes) {
					// do nothing
				} else {
					// end of a word, add it
					ar.add(wordBuffer.toString().trim());
					wordBuffer.setLength(0);
				}
			} else if (c == '"') {
				insideQuotes = !insideQuotes;
			} else if(c == '\'') {
				wordBuffer.append("''");				
			} else {
				wordBuffer.append(c);
			}
		}
		return ar;
	}

	private void processAddress() {
		// TODO Auto-generated method stub

	}

	public static void invokeMethod(String value, FishObject fish, String methodName) {
		Method method = null;
		try {
			method = fish.getClass().getMethod(methodName, String.class);
			method.invoke(fish, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createOrderedFields() {
		File dataDir = new File(topDirectory);
		File[] files = dataDir.listFiles();
		BufferedReader in = null;
		FileReader fr = null;
		Map<String, DataFileObject> fields = new HashMap<String, DataFileObject>();
		// String state="";
		for (int x = 0; x < files.length; x++) {
			File file = (File) files[x];
			String fname = file.getName();
			if (fname.endsWith(".csv")) {
				System.out.println(fname);
				// state=fname.split(" ")[0];
				try {
					fr = new FileReader(file);
					in = new BufferedReader(fr);
					String header = in.readLine();
					if (header != null) {
						System.out.println(header);
						System.out.println(header.length());
						String[] cols = header.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
						for (String field : cols) {
							field = field.toLowerCase().trim();
							field = field.replaceAll("\"", "");
							DataFileObject dfo = null;
							if (fields.containsKey(field)) {
								dfo = fields.get(field);
								dfo.setFileName(fname);
							} else {
								dfo = new DataFileObject(fname, field);
								fields.put(field, dfo);
							}
							dfo.increment();
						}
					}
					System.out.println("Fields current size:" + fields.size());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Skipping " + fname);
			}
		}
		ArrayList<DataFileObject> allFields = new ArrayList<DataFileObject>(fields.values());
		Collections.sort(allFields);
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter("c:/data/orderedfields.csv"));
			for (DataFileObject dfo : allFields) {
				pw.print(dfo.getFieldName() + ",");
				pw.print(dfo.getCount() + ",");
				pw.println(dfo.getFileName());
			}
			pw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fin");
	}

	static void processLine(String table, String ID, String[] fields, String[] values) {

	}

}
