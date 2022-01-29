package com.fishdan.text;

import java.lang.reflect.Field;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.fishdan.objects.Person;

public class Loader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sql2o sql2o = new Sql2o("jdbc:mysql://localhost:3306/call", "root", "mysql");
		String sql="INSERT INTO person (FIRST,LAST) VALUES (:first,:last)";
		try {
			/*
			Connection con = sql2o.open()
		    Long insertedId = (Long) con.createQuery(sql, true)
			    .addParameter("first", "Ima")
			    .addParameter("last", "Pseudonym")
			    .executeUpdate()
			    .getKey();
		      ResultSet rs = stmt.executeQuery("select * from MyPlayers");
		      //Retrieving the ResultSetMetadata object
		      ResultSetMetaData rsMetaData = rs.getMetaData();
		      System.out.println("List of column names in the current table: ");
		      //Retrieving the list of column names
		      int count = rsMetaData.getColumnCount();
		      for(int i = 1; i<=count; i++) {
		         System.out.println(rsMetaData.getColumnName(i));
		      }
		      */
			Person person=new Person();
			person.setFirst("Ima");
			List<?> people=person.getFromDatabase();
			System.out.println(people.size());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
