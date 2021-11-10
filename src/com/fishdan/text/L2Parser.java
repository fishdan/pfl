package com.fishdan.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class L2Parser {

	
	public static void main(String[] args) {		
		if(args.length!=1) {
			System.out.println("Usage: Java L2Parser [DataDir] [config] [run]");
			System.exit(-1);
		}
		String path=args[0];
		File dataDir=new File(path);  
		//createOrderedFields();
		loadConfig();
		createTablesScript();
		processData();
		processHollyFile();
		
	}

	private static void processHollyFile() {
		// read holly's sheets
		
	}

	private static void loadData() {
		File orderedFields=new File()
		
	}

	public static void createOrderedFields() {
		File dataDir = new File("C:\\data\\L2");
		File[] files = dataDir.listFiles();
		BufferedReader in = null;
		FileReader fr = null;
		ArrayList <String>orderedFields=new ArrayList<String>();
		Map<String, Integer> fields = new HashMap<String, Integer>();
		String state="";
		for (File file : files) {
			String fname = file.getName();
			if (fname.endsWith(".csv")) {
				System.out.println(fname);
				state=fname.split(" ")[0];
				try {
					fr = new FileReader(file);
					in = new BufferedReader(fr);
					String header = in.readLine();
					String[] cols = header.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
					for (String field : cols) {
						field=field.toLowerCase().trim();
						field=field.replaceAll("\"", "");
						int value = 0;
						if (fields.containsKey(field)) {
							value = fields.get(field);
						}
						fields.put(field, ++value);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		fields.entrySet().forEach(entry ->{
			String full=entry.getKey()+"|"+entry.getValue();
			orderedFields.add(full);
		});
		Collections.sort(orderedFields);
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter("c:/data/orderedfields.csv"));
			for(String f:orderedFields) {
				pw.println(f);
			}
			pw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fin");
	}
	
	static void processLine(String table,String ID,String [] fields,String [] values) {
		
	}

}
