package com.fishdan.objects;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public abstract class FishObject {

	private static final int LOGLEVEL_SQL = 1;
	private static int LOGLEVEL = 0;
	String classname = this.getClass().getSimpleName();
	Class<? extends FishObject> myClass = this.getClass();
	Sql2o sql2o = new Sql2o("jdbc:mysql://localhost:3306/call", "root", "mysql");
	Field[] fields = null;
	String fieldString = null;

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

	@SuppressWarnings("unchecked")
	public List<FishObject> getFromDatabase() {
		Connection con = sql2o.open();
		StringBuffer SQL = new StringBuffer("select " + fieldString + " from " + this.getClass().getSimpleName());
		String where = getWhereString();
		if (where != null) {
			SQL.append(" " + where);
		}
		if(LOGLEVEL == LOGLEVEL_SQL) {
			System.out.println(SQL);
		}
		try {
			String query = SQL.toString();
			return (List<FishObject>) con.createQuery(query).executeAndFetch(this.getClass());
		}
		catch(Exception e) {
			System.out.println(SQL);
			e.printStackTrace();
			return null;
		}
		finally {
			con.close();
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
				if (obj != null) {
					objectFields.append(field.getName() + ",");
					if (obj instanceof Integer) {
						Integer i = (Integer) obj;
						objectValues.append(i.intValue());
					} else if (obj instanceof String) {
						String s = (String) obj;
						objectValues.append("'" + s + "'");
					}
					objectValues.append(",");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		objectFields.deleteCharAt(objectFields.length()-1);
		objectValues.deleteCharAt(objectValues.length()-1);
		String fString = objectFields.toString();
		String vString = objectValues.toString();
		String insertSQL = "insert into " + this.getClass().getSimpleName() + " " + fString + ") VALUES " + (vString)
				+ ")";
		if(LOGLEVEL == LOGLEVEL_SQL) {
			System.out.println(insertSQL);
		}
		try (Connection con = sql2o.open()) {
			int objectKey = con.createQuery(insertSQL).executeUpdate().getKey(Integer.class);
			this.setId(objectKey);
		}
	}

	public void update() {
		if (this.getId() == null) {
			try {
				this.create();
			} catch (AssignedIDException e) {
				e.printStackTrace();
			}
		} else {
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
			String updateSQL = updateString.append(whereString).toString();
			if(LOGLEVEL == LOGLEVEL_SQL) {
				System.out.println(updateSQL);
			}	
			try (Connection con = sql2o.open()) {
				con.createQuery(updateSQL).executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract void setId(Integer i);
	public abstract Integer getId();

	public String getWhereString() {
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
							s=s.replaceAll("\'", "");
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

	/**
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}

	/**
	 * @param classname the classname to set
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}

	/**
	 * @return the myClass
	 */
	public Class<? extends FishObject> getMyClass() {
		return myClass;
	}

	/**
	 * @param myClass the myClass to set
	 */
	public void setMyClass(Class<? extends FishObject> myClass) {
		this.myClass = myClass;
	}

	/**
	 * @return the sql2o
	 */
	public Sql2o getSql2o() {
		return sql2o;
	}

	/**
	 * @param sql2o the sql2o to set
	 */
	public void setSql2o(Sql2o sql2o) {
		this.sql2o = sql2o;
	}

	/**
	 * @return the fields
	 */
	public Field[] getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(Field[] fields) {
		this.fields = fields;
	}

	/**
	 * @return the fieldString
	 */
	public String getFieldString() {
		return fieldString;
	}

	/**
	 * @param fieldString the fieldString to set
	 */
	public void setFieldString(String fieldString) {
		this.fieldString = fieldString;
	}
}
