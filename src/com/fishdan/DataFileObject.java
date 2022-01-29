package com.fishdan;

public class DataFileObject implements Comparable {

	String fileName;
	String fieldName;
	int count = 0;

	public DataFileObject(String file, String field) {
		fileName = file;
		fieldName = field;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int increment() {
		return ++count;
	}

	public int decrement() {
		if (count > 0)
			--count;
		return count;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof DataFileObject) {
			DataFileObject dfo = (DataFileObject) o;
			return this.getFieldName().compareTo(dfo.getFieldName());
		}
		// TODO Auto-generated method stub
		return 0;
	}
}
