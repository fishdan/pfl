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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
		// loadConfig();
		// createTablesScript();
		// processData();
		processHollyFile();

	}

	private static HashMap loadOrderedFields(String location) {
		// TODO Auto-generated method stub
		try {
			BufferedReader in = new BufferedReader(new FileReader(location));
			// skip header
			String line = in.readLine();
			while ((line = in.readLine()) != null) {
				String[] data = StringUtils.split(line, ',');
				LoaderObject lo = new LoaderObject();
				lo.setColumn(data[COLUMN]);
				lo.setCount(data[COUNT]);
				lo.setField(data[FIELD]);
				lo.setName(data[NAME]);
				lo.setTable(data[TABLE]);
				lo.setType(data[TYPE]);
				fieldMap.put(lo.getColumn(), lo);
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

	public File[] getFiles() {
		File dataDir = new File(topDirectory);
		return dataDir.listFiles();
	}

	public String processFile(File file) {
		if (!file.getName().endsWith("csv")) {
			return "skipping " + file.getName();
		}
		try {
			FileReader fr = new FileReader(file);
			BufferedReader in = new BufferedReader(fr);
			String header = in.readLine().toLowerCase();
			String[] cols = null;
			if (header != null) {
				System.out.println(header);
				System.out.println(header.length());
				cols = header.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

			}
			String line = "";
			while ((line = in.readLine()) != null) {
				HashMap<String,FishObject> data = new HashMap();
				HashMap<String, Address> addresses = new HashMap();
				HashMap<String, Email> emails = new HashMap();
				HashMap<String, Externalid> xids = new HashMap();
				HashMap<String, Detail> details = new HashMap();
				HashMap<String, LinkPersonIssue> issueLinks = new HashMap();
				String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				for (int x = 0; x < tokens.length; x++) {
					String value = tokens[x];
					LoaderObject lo = fieldMap.get(cols[x]);
					if (!lo.getTable().isEmpty()) {
						// work it
						String fullClassName = "com.fishdan.object" + StringUtils.capitalize(lo.getTable());
						Class<?> c = Class.forName(fullClassName);
						FishObject fish = (FishObject) c.getConstructor().newInstance();
						if(fish instanceof Address) {
							Address address = addresses.get(lo.getType());
							if(address ==null) {
								address=new Address();
								address.setType(lo.getType());
								addresses.put(fullClassName, address);
							}
							String methodName = "set" + StringUtils.capitalize(lo.getField());
							invokeMethod(value, fish, methodName);									
						} 
						else if (fish instanceof Email) {
							if(fish instanceof Email) {
								Email email = emails.get(lo.getType());
								if(email ==null) {
									email=new Email();
									email.setType(lo.getType());
									emails.put(fullClassName, email);
								}
								String methodName = "set" + StringUtils.capitalize(lo.getField());
								invokeMethod(value, fish, methodName);									
							} 							
						} 
						else if (fish instanceof Externalid) {
							if(fish instanceof Externalid) {
								Externalid externalid = xids.get(lo.getType());
								if(externalid ==null) {
									externalid=new Externalid();
									externalid.setType(lo.getType());
									xids.put(fullClassName, externalid);
								}
								String methodName = "set" + StringUtils.capitalize(lo.getField());
								invokeMethod(value, fish, methodName);									
							} 								
						} 
						else if (fish instanceof Issue) {
							Issue issue=new Issue();
							issue.setName(lo.getName());
							List<FishObject> issueList=issue.getFromDatabase();
							if(issueList.size()==0) {
								//doesn't exist yet
								issue.update();
							}
						} 
						else if (fish instanceof Person) {
							
						} 						
						else if (fish instanceof Phone) {
							
						} 

						
						
						if(lo.getType().isEmpty()) {//no type  1-n object
							if(lo.getName().isEmpty()) {//no type, no name
								String methodName = "set" + StringUtils.capitalize(lo.getField());
								invokeMethod(value, fish, methodName);								
							}
							else { //no type, yes name
								String methodName = "setName";
								invokeMethod(value, fish, methodName);
							}
						}
						else {//yes type
							String key=lo.getTable()+"_"+lo.getType();
							FishObject fo=data.get(key);
							if(fo == null) {
								fo=fish;									
							}
							if(lo.getField().isEmpty()) {//yes type, no field
								FishObject fo=data.get(lo.getTable()+lo.getType());

								String methodName = "setType";
								invokeMethod(value, fish, methodName);
								data.put(methodName, fo)
							}
							else {//yes type, yes field -- this is a 1-n sort of object from the person
								//find the object in data
								
							}
						}

					}
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
		return "";
	}

	private void processAddress() {
		// TODO Auto-generated method stub
		
	}

	public void invokeMethod(String value, FishObject fish, String methodName) {
		Method method=null;
		try {
		  method = fish.getClass().getMethod(methodName, String.class);
		  method.invoke(fish, value);
		} catch(Exception e) {
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
