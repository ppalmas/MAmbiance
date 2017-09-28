package org.fasol.mambiance.db;

import java.util.Date;

import android.content.ContentValues;

public class Marqueur extends DataObject {

	
	//Attributes
	/**
	 * long id of the object marqueur
	 */
	private long marqueur_id;
	/**
	 * long id of the place to which the marker belongs
	 */
	private long lieu_id;
	/**
	 * date when the marker is set
	 */
	private Date date_creation;

	//Getters
	/**
	 * getter for the marqueur_id
	 * @return long marqueur_id
	 */
	public long getMarqueur_id() {
		return marqueur_id;
	}
	
	/**
	 * getter for the lieu_id
	 * @return long lieu_id
	 */
	public long getLieu_id() {
		return lieu_id;
	}

	/**
	 * getter for the date_creation
	 * @return Date date_creation
     */
	public Date getDate_creation() { return date_creation; }

	//Setters
	/**
	 * setter for the marqueur_id
	 * @param  marqueur_id
	 */
	public void setMarqueur_id(long marqueur_id) {
		this.marqueur_id = marqueur_id;
	}

	/**
	 * setter for the lieu_id
	 * @param lieu_id
	 */
	public void setLieu_id(long lieu_id) {
		this.lieu_id = lieu_id;
	}

	/**
	 * setter for the datecreation
	 * @param date_creation
     */
	public void setDate_creation(Date date_creation) { this.date_creation = date_creation; }
	
	//Abstract methods
	
	@Override
	public String toString() {
		return "Marqueur [marqueur_id=" + marqueur_id + ", lieu_id=" + lieu_id
				+ ", date_creation=" + date_creation + "]";
	}


	@Override
	public void saveToLocal(LocalDataSource datasource) {
		ContentValues values = new ContentValues();

		//Cursor cursor = datasource.getDatabase().rawQuery(GETMAXMARQUEURID, null);
		//cursor.moveToFirst();

		//this.setMarqueur_id(1+cursor.getLong(0));
		//TODO WTF	this.setMarqueur_id(Sync.getMaxId().get("Marqueur")+1);

		//values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);
		values.put(MySQLiteHelper.COLUMN_LIEUID, this.lieu_id);
		values.put(MySQLiteHelper.COLUMN_DATECREATION, this.date_creation.toString());

		if(this.registredInLocal){
			String str = "_id "+"="+this.marqueur_id;
			datasource.getDatabase().update(MySQLiteHelper.TABLE_MARQUEUR, values, str, null);
		}
		else{
			long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_MARQUEUR, null, values);
			this.setMarqueur_id(row_id);
			this.setRegistredInLocal(true);
		}
	}

	/**
	 * query to get the biggest Element_id from local db
	 */
	private static final String	GETMAXELEMENTID =
			"SELECT "+MySQLiteHelper.TABLE_MARQUEUR+"."+MySQLiteHelper.COLUMN_MARQUEURID+" FROM "
			+ MySQLiteHelper.TABLE_MARQUEUR
			+" ORDER BY "+MySQLiteHelper.TABLE_MARQUEUR+"."+MySQLiteHelper.COLUMN_MARQUEURID
			+" DESC LIMIT 1 ;";

}