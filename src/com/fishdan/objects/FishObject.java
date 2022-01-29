package com.fishdan.objects;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public abstract class FishObject {

	String classname = this.getClass().getSimpleName();
	Class myClass = this.getClass();
	Sql2o sql2o = new Sql2o("jdbc:mysql://localhost:3306/call", "root", "mysql");
	Field[] fields = null;
	String fieldString = null;
	private Integer id;

	public FishObject() {
		if (fields == null) {
			fields = this.getClass().getDeclaredFields();
			StringBuffer buff = new StringBuffer();
			for (int x = 0; x < fields.length; x++) {
				Field field = fields[x];
				if (x != 0) {
					buff.append(",");
				}
				buff.append(field.getName());
			}
			fieldString = buff.toString();
		}
	}

	public abstract void setId(int id);

	public abstract Integer getId();

	@SuppressWarnings("unchecked")
	public List<FishObject> getFromDatabase() {
		StringBuffer SQL = new StringBuffer("select " + fieldString + " from " + this.getClass().getSimpleName());
		String where = getWhereString();
		if (where != null) {
			SQL.append(" " + where);
		}
		System.out.println(SQL);
		try (Connection con = sql2o.open()) {
			String query = SQL.toString();
			return (List<FishObject>) con.createQuery(query).executeAndFetch(this.getClass());
		}
	}

	public void create() throws AssignedIDException {
		if (this.getId() != null) {
			throw new AssignedIDException();
		}
		StringBuffer objectFields = new StringBuffer("(");
		StringBuffer objectValues = new StringBuffer("(");
		for (Field field : fields) {
			String methodName = "get" + StringUtils.capitalize(field.getName());
			Class<?> c = this.getClass();
			try {
				Method method = c.getDeclaredMethod(methodName);
				Object obj = method.invoke(this);
				objectFields.append(field.getName() + ",");
				if (obj instanceof Integer) {
					Integer i = (Integer) obj;
					objectValues.append(i.intValue());
				} else if (obj instanceof String) {
					String s = (String) obj;
					objectValues.append("'" + s + "',");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		objectFields.deleteCharAt(objectFields.length());
		objectValues.deleteCharAt(objectValues.length());
		String fString = objectFields.toString();
		String vString = objectValues.toString();
		String insertSQL = "insert into " + this.getClass().getSimpleName() + " (" + fString + ") VALUES " + (vString)
				+ ")";
		try (Connection con = sql2o.open()) {
			int objectKey = con.createQuery(insertSQL).executeUpdate().getKey(Integer.class);
			this.setId(objectKey);
		}
	}

	public void update() {
		StringBuffer updateString = new StringBuffer("(");
		String whereString = "";
		for (Field field : fields) {
			String methodName = "get" + StringUtils.capitalize(field.getName());
			Class<?> c = this.getClass();
			try {
				Method method = c.getDeclaredMethod(methodName);
				Object obj = method.invoke(this);
				if (field.getName().equalsIgnoreCase("id")) {
					Integer i = (Integer) obj;
					whereString += " WHERE ID=" + i.intValue();
				} else {
					updateString.append(field.getName() + "=");
					if (obj instanceof Integer) {
						Integer i = (Integer) obj;
						updateString.append(i.intValue());
					} else if (obj instanceof String) {
						String s = (String) obj;
						updateString.append("'" + s + "',");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String updateSQL=updateString.append(whereString).toString();
		try (Connection con = sql2o.open()) {
		    con.createQuery(updateSQL).executeUpdate();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getWhereString(int setSwitch) {
		StringBuffer where = null;
		for (Field field : fields) {
			String methodName = "get" + StringUtils.capitalize(field.getName());
			Class<?> c = this.getClass();
			try {
				Method method = c.getDeclaredMethod(methodName);
				try {
					Object obj = method.invoke(this);
					if (obj != null) {
						if (where == null) {
							where = new StringBuffer("WHERE ");
						} else {
							where.append(" AND ");
						}
						where.append(field.getName() + " = ");
						if (obj instanceof Integer) {
							Integer i = (Integer) obj;
							where.append(i.intValue());
						} else if (obj instanceof String) {
							String s = (String) obj;
							where.append("'" + s + "'");
						}
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return where == null ? null : where.toString();
	}

	String insertQuery = "INSERT INTO customers (id, name, address) " + "VALUES (:id, :name, :address)";

}
