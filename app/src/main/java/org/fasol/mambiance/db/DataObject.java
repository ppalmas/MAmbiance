package org.fasol.mambiance.db;

public abstract class DataObject {
	
	//Attributes 
	/**
	 * Boolean attribute to know if the object is already created in the local database or not
	 */
	protected Boolean registredInLocal = false;
	
	//Getters
	/**
	 * getter for the attribute registredInLocal
	 * @return Boolean
	 */
	public Boolean getRegistredInLocal() {
		return registredInLocal;
	}
	
	//Setters
	/**
	 * setter for the attribute registredInLocal
	 * @param registredInLocal the value to insert
	 */
	public void setRegistredInLocal(Boolean registredInLocal) {
		this.registredInLocal = registredInLocal;
	}
	
	//Methods
	/**
	 * method to save the object in the Database
	 * will be specified in each class that inherits DataObjects
	 * @param datasource is the Localdatasource of which DataObject is a table
	 */
	public abstract void saveToLocal(LocalDataSource datasource);
	
	/**
	 * method to use the toString of a DataObject
	 * will be specified in each class that inherits DataObjects
	 */
	public abstract String toString();
	

}