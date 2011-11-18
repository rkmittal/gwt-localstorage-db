/**
 * Copyright 2011, Raghu Kumar Mittal, Handheld Solutions Research Labs Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.handsrel.localstorage.client;

import java.util.ArrayList;

import com.google.gwt.storage.client.Storage;

/**
 * The class which holds all Local DB methods
 * @author Raghu Mittal
 *
 */
public class LdbApi {
	private static Storage storage;
	private static String db;
	private static String lastPkeyNo;
	
	public static String getDb() {
		return db;
	}

	public static void setDb(String db) {
		LdbApi.db = db;
	}

	/**
	 * Initialization of the whole API, the first thing that should be executed before
	 * beginning to use the Database API
	 * @param storage The localStorage for future use
	 */
	public static void initialize(Storage storage) {
		LdbApi.storage = storage; 
		String db_no = storage.getItem("db_no");
		if(db_no==null)
			storage.setItem("db_no", "0");
	}
	
	
	/**
	 * Warning: Removes everything from the localstorage (an irreversible process) - should be very carefully used, 
	 * give a proper dialog box and let the user confirm deletion inside your code
	 */
	//No internal checks needed - checks on confirmation
	public static void removeAll() {
		storage.clear();
		initialize(LdbApi.storage);
	}
	
	/**
	 * Just returns all local storage keys for reference sake as a String type
	 * @return Result containing all local storage keys 
	 */
	//No checks needed to print all keys
	public static String printAllKeys() {
		//print all local storage keys
		String result="";
		for(int i=0; i<storage.getLength(); i++) {
			String key = storage.key(i);
			String item = storage.getItem(key);
			result = result + key+":"+item +"\n";
		}
		return result;
	}
	
	//TODO getAllKeys() function to return the map of key value pairs
	
	/**
	 * A simple method to print an ArrayList<String> type
	 * @param list List to be printed
	 * @return Returns the result as a string
	 */
	//No checks needed
	public static String printList(ArrayList<String> list) {
		String result="";
		for(int i=0; i<list.size(); i++) {
			result = result + list.get(i) + "\n";
		}
		return result;
	}
	
	//No checks needed here
	private static String get(String key) {
		return storage.getItem(key);
	}
	
	
	private static String get(String objStrName, String extension) {
		return storage.getItem("db_"+db+"_objstr_"+objStrName+"_"+extension);
	}
	
	private static void set(String key, String value) {
		storage.setItem(key, value);
	}
	
	//Checking db is in use has to be taken care of by the caller function, to prevent repeated checking
	//Checking whether object store exists also to be taken care of by the caller function 
	private static void set (String objStrName, String extension, String value) {
		storage.setItem("db_"+db+"_objstr_"+objStrName+"_"+extension, value);
	}
	
	private static void remove(String key) {
		storage.removeItem(key);
	}

	//Checking db is in use has to be taken care of by the caller function, to prevent repeated checking
	//Checking whether object store exists also to be taken care of by the caller function 
	private static void remove(String objStrName, String extension) {
		storage.removeItem("db_"+db+"_objstr_"+objStrName+"_"+extension);
	}
	
	/**
	 * Internal function to search for a particular number - Not part of the external api since its only to be used by the Api
	 * itself
	 * @param keyPart Key part which on adding with _no gives the counter to the data
	 * @param valueToSearch
	 * @return the integer that contains the searched for value
	 */
	//No checks needed, since the assumption is that the keyPart_no will always be correct and be present for changing
	private static int searchForNumber(String keyPart, String valueToSearch) {
		int maxCount = Integer.parseInt( get(keyPart+"_no") );
		for(int i=0; i<maxCount; i++) {
			String rec = get(keyPart+"_"+i);
			if(rec!=null && rec.equals(valueToSearch)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Looks at the available databases and returns all names of the databases
	 * @return the db list
	 */
	//No checks needed
	public static ArrayList<String> getDbList() {
		ArrayList<String> dbList = new ArrayList<String>();
		int db_no = Integer.parseInt(get("db_no"));
		
		for(int i=0; i<db_no; i++) {
			String dbName = get("db_"+i);
			if(dbName!=null)
				dbList.add(dbName);
		}
		return dbList;
	}
	
	
	/**
	 * Looks at the available object stores in the db and returns the list of all stores
	 * @return List of all store names
	 */
	//check to see if db is used, needed because this method will be called by the user externally
	public static ArrayList<String> getObjStoreList() throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		ArrayList<String> objStrList = new ArrayList<String>();
		int objstr_no = Integer.parseInt(get("db_"+db+"_objstr_no"));
		
		for(int i=0; i<objstr_no; i++) {
			String objStrName = get("db_"+db+"_objstr_"+i);
			if(objStrName!=null)
				objStrList.add(objStrName);
		}
		return objStrList;
	}

	/**
	 * Looks at the available column names in an object store
	 * @param objStrName The name of the object store
	 * @return List of column names in the specified object store
	 */
	//check if db is being used, check if objstr exists
	public static ArrayList<String> getColumnList(String objStrName) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		ArrayList<String> columnList = new ArrayList<String>();
		int col_no = Integer.parseInt(get(objStrName,"col_no"));
		
		for(int i=0; i<col_no; i++) {
			String columnName = get(objStrName,"col_"+i);
			if(columnName!=null)
				columnList.add(columnName);
		}
		return columnList;
	}

	/**
	 * Gets all the values from the particular column
	 * @param objStrName Name of object store
	 * @param columnName Name of the column
	 * @return List of values
	 */
	//check if db is being used, check if object store exists, check if columnName exists
	public static ArrayList<String> getValuesFromColumn(String objStrName, String columnName) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		if( checkIfColumnNameAlreadyExists(objStrName, columnName)==false )
			throw new LocalStorageDatabaseException("Column name '"+columnName+"' doesn't exist");
		
		boolean pkey = isPkey(objStrName, columnName);
		ArrayList<String> valueList = new ArrayList<String>();
		int record_no = Integer.parseInt(get( objStrName, "pkey_no"));

		for(int i=0; i<record_no; i++) {
			String value = null;
			String key = get(objStrName,"pkey_"+i);
			if(pkey && key!=null) {
				valueList.add(key);
			} else if(!pkey && key!=null) {
				value = get(objStrName,"col_"+columnName+"_value_"+key);
				if(value!=null)
					valueList.add(value);
			}
		}
		return valueList;
	}
	
	/**
	 * Gets the total number of records present in the object store
	 * @param objStrName Name of the object store
	 * @return Number of records
	 */
	//check if db is being used, check if object store exists
	public static int getNumberOfRecords(String objStrName) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		int count = 0;
		int maxCount = Integer.parseInt( get(objStrName, "pkey_no"));
		for(int i=0; i<maxCount; i++) {
			String pkeyValue = get(objStrName, "pkey_"+i);
			if(pkeyValue!=null)
				count++;
		}
		return count;
	}
	
	/**
	 * Gets all the values from the particular column
	 * @param objStrName 
	 * @param columnName Name of the column
	 * @return List of values
	 */
	
	/**
	 * Returns the list of Pkeys (or the first pkey) of the records that contain the searchValue
	 * Useful for quick searching of something in particular (cannot be used with wild cards etc.)
	 * @param objStrName Name of object store
	 * @param columnName Name of the column
	 * @param searchValue Value to be searched
	 * @param first true to indicate whether you want to stop searching after the first one is found, false to get all
	 * matches
	 * @return Array containing all pkeys having the column
	 * @throws LocalStorageDatabaseException
	 */
	//check if db is being used, check if object store exists, check if columnName exists
	//check if column name is not the primary key
	public static ArrayList<String> getPkeysForValueInColumn(String objStrName, String columnName, String searchValue, boolean first) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		if( checkIfColumnNameAlreadyExists(objStrName, columnName)==false )
			throw new LocalStorageDatabaseException("Column name '"+columnName+"' doesn't exist");
		if( isPkey(objStrName, columnName) )
			throw new LocalStorageDatabaseException("Primary key can't be specified for searching the records");

		ArrayList<String> pkeys = new ArrayList<String>();
		
		int record_no = Integer.parseInt(get( objStrName, "pkey_no"));

		for(int i=0; i<record_no; i++) {
			String value = null;
			String key = get(objStrName,"pkey_"+i);
			if(key!=null) {
				value = get(objStrName,"col_"+columnName+"_value_"+key);
				if( value!=null && value.equals(searchValue) ) {
					pkeys.add(key);
					if(first==true) //only the first record is needed
						break;
				}
			}
		}
		return pkeys;
	}
	
	/**
	 * Gets the Primary key for the last added/updated record 
	 * @return last primary key
	 */
	public static String getLastPkeyNo() {
		return LdbApi.lastPkeyNo;
	}
	
	private static void throwExceptionIfDbIsNotBeingUsed() throws LocalStorageDatabaseException {
		if(db==null)
			throw new LocalStorageDatabaseException("No database in use");
	}

	private static boolean checkIfDbAlreadyExists(String dbName) {
		ArrayList<String> dbList = getDbList();
		for(int i=0; i<dbList.size(); i++) {
			if ( dbName.equals(dbList.get(i)) )
				return true;
		}
		return false;
	}

	private static boolean checkIfObjStrAlreadyExists(String objStrName) throws LocalStorageDatabaseException {
		ArrayList<String> objStrList = getObjStoreList();
		for(int i=0; i<objStrList.size(); i++) {
			if ( objStrName.equals(objStrList.get(i)) )
				return true;
		}
		return false;
	}

	private static boolean checkIfColumnNameAlreadyExists(String objStrName, String columnName) throws LocalStorageDatabaseException {
		ArrayList<String> columnList = getColumnList(objStrName);
		for(int i=0; i<columnList.size(); i++) {
			if ( columnName.equals(columnList.get(i)) )
				return true;
		}
		return false;
	}

	private static boolean checkIfValueAlreadyExists(String objStrName, String columnName, String value) throws LocalStorageDatabaseException {
		ArrayList<String> valueList = getValuesFromColumn(objStrName, columnName);
		for(int i=0; i<valueList.size(); i++) {
			if ( value.equals(valueList.get(i)) )
				return true;
		}
		return false;
	}
	
	private static boolean isPkey(String objStrName, String columnName) {
		return get(objStrName,"col_0").equals(columnName);
	}

	private static boolean isUnique(String objStrName, String columnName) {
		return isPkey(objStrName, columnName) || get(objStrName,"col_"+columnName+"_unique")!=null;
	}

	private static boolean isAutoGenerate(String objStrName) {
		return get(objStrName,"pkey_autogenerate")!=null;
	}

	private static boolean isNotNull(String objStrName, String columnName) {
		return isPkey(objStrName, columnName) || get( objStrName,"col_"+columnName+"_notnull" )!=null;
	}

	/**
	 * Method to create a new database
	 * @param dbName Name of the database to be created
	 * @throws LocalStorageDatabaseException If the dbName already exists
	 */
	//check if that dbName shouldn't already exist
	public static void createDb(String dbName) throws LocalStorageDatabaseException {
		if(checkIfDbAlreadyExists(dbName)==true)	
			throw new LocalStorageDatabaseException("Database '"+dbName+"' already exists");
		
		//change db_no index
		int db_no = Integer.parseInt(get("db_no"));
		set("db_no", String.valueOf(db_no+1) );
		set("db_"+db_no, dbName);
	}
	
	/**
	 * Method to use a particular database for further changes to be done
	 * @param dbName Name of db to be used
	 * @throws LocalStorageDatabaseException throws exception if the db doesn't exist
	 */
	//check if the db exists
	public static void useDb(String dbName) throws LocalStorageDatabaseException {
		if(checkIfDbAlreadyExists(dbName)==false)
			throw new LocalStorageDatabaseException("Database '"+dbName+"' doesn't exist");
		
		setDb(dbName);
		String db_objstr_no = get("db_"+dbName+"_objstr_no");
		if(db_objstr_no==null)
			set("db_"+dbName+"_objstr_no", "0");
		
	}
	
	/**
	 * Creates a new object store in the current database
	 * @param objStrName
	 * @throws LocalStorageDatabaseException
	 */
	//check if db is being used, check that the object store name shouldn't already exist 
	public static void createObjectStore(String objStrName) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==true)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' already exists");
		
		int objstr_no = Integer.parseInt(get("db_"+db+"_objstr_no"));
		set("db_"+db+"_objstr_no", String.valueOf(objstr_no+1) );
		set("db_"+db+"_objstr_"+objstr_no, objStrName);

		//initialize col no for obj store
		String objstr_col_no = get(objStrName,"col_no");
		if(objstr_col_no==null)
			set(objStrName,"col_no", "0");
	}
	
	/**
	 * Adds a column to the specified object store <br>
	 * <b>Note:</b> Primary key column should be the first column, and there should be a primary key defined, otherwise an 
	 * exception will be raised
	 * @param objStrName Name of the object store for adding the column
	 * @param col The column definition
	 */
	//check if db is being used, check if object store already exists, check that columnName to be added doesn't already exist
	//check that if its the Primary key, then it should be the first column
	//check if the first column to be added is not primary key, then the whole thing fails - so it is not allowed
	public static void addColumn(String objStrName, Column col) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");

		if( checkIfColumnNameAlreadyExists(objStrName, col.getNameOfColumn())==true )
			throw new LocalStorageDatabaseException("Column name '"+col.getNameOfColumn()+"' already exists");

		int col_no = Integer.parseInt( get(objStrName,"col_no") );
		
		if( (col_no==0 && !col.isPkey()) || (col.isPkey() && col_no!=0) )
			throw new LocalStorageDatabaseException("Primary key column should be the first column");

		//All checks done
		set( objStrName,"col_no", String.valueOf(col_no+1) );
		set( objStrName,"col_"+col_no, col.getNameOfColumn() );
		
		if(col.isPkey()) {
			set( objStrName,"pkey_no", "0" );
			if(col.isAutogenerate())
				set( objStrName,"pkey_autogenerate", "1" );
		} else {
			if(col.isUnique())
				set( objStrName,"col_"+col.getNameOfColumn()+"_unique", "1" );
			if(col.isNotNull())
				set( objStrName,"col_"+col.getNameOfColumn()+"_notnull", "1" );
		}

	}
	
	/**
	 * Adds the record, given the column names and their values one after the other in a string array
	 * Similar to <b>INSERT INTO table SET (col1, col2, col3) VALUES (val1, val2, val3)</b><br>
	 * <b>Note:</b> Primary key, if autogenerated, shouldn't be specified or specified as null, else if not auto-generated, 
	 * it should be uniquely specified 
	 * @param objStrName Name of the object store to add the record 
	 * @param map Array of string containing the column names and the values - defined as col1,val1,col2,val2... etc. 
	 * The columns can be defined in any order. Make sure that primary key is the first one to be defined if it is not autogenerated
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists
	// check if the column names in map are present in the object store
	// check if key autogenerated - then the map shouldn't contain a value for the primary key
	// check if key not autogenerated, then the map should specify a value for the primary key
	// check that if a column is unique, then the value to be added should be unique
	// check that if a column is notNull, then it should be specified in the map
	// check if primary key is not auto-generated, then the first column to be set should be the primary key
	public static void addRecord(String objStrName, String[] map) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		for(int i=0; i<map.length; i+=2) {
			if( checkIfColumnNameAlreadyExists(objStrName, map[i])==false )
				throw new LocalStorageDatabaseException("Column name '"+map[i]+"' doesn't exist in the object store '"+objStrName+"'");
		}
		if( !isAutoGenerate(objStrName) && !isPkey(objStrName, map[0]) )
			throw new LocalStorageDatabaseException("Since primary key is not autogenerated, the first column to be set should be the primary key");

		
		for(int i=0; i<map.length; i+=2) {
			String columnName = map[i];
			String value = map[i+1];
			if (isPkey(objStrName, columnName)) {
				if( isAutoGenerate(objStrName) && value!=null ) 
					throw new LocalStorageDatabaseException("Primary key is autogenerated, its value can't be specified");
				else if (!isAutoGenerate(objStrName) && value==null)
					throw new LocalStorageDatabaseException("Primary key value can't be null");
				else if(checkIfValueAlreadyExists(objStrName, columnName, value))
					throw new LocalStorageDatabaseException("Primary key should have unique value");
			} else {
				if(isUnique(objStrName, columnName) && value!=null && checkIfValueAlreadyExists(objStrName, columnName, value))
					throw new LocalStorageDatabaseException("Unique pre-condition for the column '"+columnName+"' has failed");
				if(isNotNull(objStrName, columnName) && value==null)
					throw new LocalStorageDatabaseException("NotNull pre-condition for the column '"+columnName+"' has failed");
			}
		}
		
		//go through all unfilled columns and check if they are not defined as notNull
		ArrayList<String> columnList = getColumnList(objStrName);
		for(int i=0; i<columnList.size(); i++) {
			boolean filled = false;
			//check if ith column name is unfilled
			for(int j=0; j<map.length; j+=2)
				if(columnList.get(i).equals(map[j])) {
					filled = true;
					break;
				}
			
			if(!filled) {
				/*if(isPkey(objStrName, columnList.get(i)) && !isAutoGenerate(objStrName))
					throw new LocalStorageDatabaseException("Primary key not specified");*/ //Redundant check on primary key, thus removed
				if( !isPkey(objStrName, columnList.get(i)) && isNotNull(objStrName, columnList.get(i)) )
					throw new LocalStorageDatabaseException("Column '"+columnList.get(i)+"' is notNull, so the value should be specified");
			}
		}

		//All checks done, ready to add the record now
		int pkey_no = Integer.parseInt( get(objStrName,"pkey_no") );
		set(objStrName,"pkey_no", String.valueOf(pkey_no+1) );

		if(isAutoGenerate(objStrName)) {
			set( objStrName,"pkey_"+pkey_no, String.valueOf(pkey_no) );
			LdbApi.lastPkeyNo = String.valueOf(pkey_no);
		}
		
		for(int i=0; i<map.length; i+=2) {
			String columnName = map[i];
			String value = map[i+1];
			if (isPkey(objStrName, columnName)) {
				set( objStrName,"pkey_"+pkey_no, value );
				LdbApi.lastPkeyNo = value;
			} else if(value!=null) {
				String keyValue = get(objStrName,"pkey_"+pkey_no);
				set( objStrName,"col_"+columnName+"_value_"+keyValue, value );
			}
		}
	}
	
	
	/**
	 * Adds the record, given all values for the record in correct sequence of the columns defined for the object store.
	 * Similar to the sql query - <b>INSERT INTO table VALUES (val1, val2, val3)</b><br>
	 * Its perfectly normal to leave null value for a column that you don't want to fill, of course, the column should not
	 * be specified as NotNull, otherwise an exception will be thrown
	 * @param objStrName Name of the object store
	 * @param map String array containing all the values in sequence to add as a record
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists
	// check key autogenerate - then the map shouldn't contain a value for the primary key
	// check if key not autogenerated, then the map should specify a value for the primary key
	// check that number of values specified matches with number of columns inside the object store
	// check that if a column is unique, then the value to be added should be unique
	// check that if a column is notNull, then it should be specified in the map
	public static void addRecordAllValues(String objStrName, String[] map) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		if(isAutoGenerate(objStrName) && map[0]!=null)
			throw new LocalStorageDatabaseException("Primary key being autogenerated can be only specified as null");
		if(!isAutoGenerate(objStrName) && map[0]==null)
			throw new LocalStorageDatabaseException("Primary key should be specified");
		String columnName = get(objStrName,"col_0");
		if( !isAutoGenerate(objStrName) && checkIfValueAlreadyExists(objStrName, columnName, map[0]) )	
			throw new LocalStorageDatabaseException("Primary key value should be uniquely specified");
		
		ArrayList<String> columnList = getColumnList(objStrName);
		if(columnList.size()!=map.length)
			throw new LocalStorageDatabaseException("Number of columns and number of specified values don't match up");
		
		//checks again
		for(int i=1; i<map.length; i++) {
			columnName = columnList.get(i);
			if(map[i]==null && isNotNull(objStrName, columnName))
				throw new LocalStorageDatabaseException("'"+columnName+"' should be specified and not contain null values");
			if(map[i]!=null && isUnique(objStrName, columnName) && checkIfValueAlreadyExists(objStrName, columnName, map[i]))
				throw new LocalStorageDatabaseException("'"+map[i]+"' value already exists in the '"+columnName+"'. Unique precondition failed");
		}
		
		//all checks done, now we can start filling up the record
		int pkey_no = Integer.parseInt( get(objStrName,"pkey_no") );
		set(objStrName,"pkey_no", String.valueOf(pkey_no+1) );
		if(isAutoGenerate(objStrName)) {
			set(objStrName,"pkey_"+pkey_no, String.valueOf(pkey_no) );
			LdbApi.lastPkeyNo = String.valueOf(pkey_no);
		}
		else {
			set(objStrName, "pkey_"+pkey_no, map[0]);
			LdbApi.lastPkeyNo = map[0];
		}
		
		for(int i=1; i<map.length; i++) {
			columnName = columnList.get(i);
			if(map[i]!=null) {
				String keyValue = get(objStrName,"pkey_"+pkey_no);
				set(objStrName, "col_"+columnName+"_value_"+keyValue, map[i]);
			}
		}
	}
	
	/**
	 * Gets the primary key value for the ith record
	 * @param objStrName Name of the object store
	 * @param i The number of the record
	 * @return Primary key value, so that it can be used to access the full record
	 */
	// check if db is being used, check if object store already exists
	// check that i should be less than or equal to number of records, and greater than zero
	public static String getPkeyForIthRecord(String objStrName, int i) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		if(i<=0)
			throw new LocalStorageDatabaseException("i value should be greater than zero");

		int numOfRecords = getNumberOfRecords(objStrName);
		if(i>numOfRecords)
			throw new LocalStorageDatabaseException("i value cannot be more than the number of records in the object store");
		
		String pkeyValue=null;
		
		int count=0;
		for(int j=0; count<i; j++) {
			pkeyValue = get(objStrName, "pkey_"+j);
			if(pkeyValue!=null)
				count++;
		}
		
		return pkeyValue;
	}
	
	/**
	 * Gets the ith record
	 * @param objStrName Name of the object store
	 * @param i Number i
	 * @return The string array containing the values inside the record
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists - done in call to getPkeyForIthRecord
	// check that i should be less than or equal to number of records, and greater than zero - done in call to getPkeyForIthRecord
	public static String[] getIthRecord(String objStrName, int i) throws LocalStorageDatabaseException {
		String pkeyValue = getPkeyForIthRecord(objStrName, i);
		return getRecordByPkey(objStrName, pkeyValue);
	}
	
	/**
	 * Returns the record that has the given pkey value
	 * @param objStrName Name of the object store
	 * @param pkeyValue Primary key value for accessing the record
	 * @return The string array containing the values inside the record
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists
	// check if pkey value exists
	public static String[] getRecordByPkey(String objStrName, String pkeyValue) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		if( !checkIfValueAlreadyExists(objStrName, get(objStrName, "col_0"), pkeyValue) )
			throw new LocalStorageDatabaseException("'"+pkeyValue+"' doesn't exist in the object store '"+objStrName+"'");
		
		ArrayList<String> columnList = getColumnList(objStrName);
		
		String[] record = new String[columnList.size()];
		record[0] = pkeyValue;
		for(int j=1; j<columnList.size(); j++) {
			record[j] = get(objStrName, "col_"+columnList.get(j)+"_value_"+pkeyValue);
		}
		return record;
	}
	
	/**
	 * Gets the ith record from the given object store and returns only the column names requested. <br>
	 * @param columnNames Names of the columnNames to be printed
	 * @param objStrName Name of the object store
	 * @param i Number i
	 * @return Record containing data for the specified columns
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists - done by getPkeyForIthRecord
	// check if columnNames already exist in the object store
	// checks on i - done by getPkeyForIthRecord
	public static String[] getIthRecordColumn(String[] columnNames, String objStrName, int i) throws LocalStorageDatabaseException {
		String pkeyValue = getPkeyForIthRecord(objStrName, i);
		return getRecordColumnByPkey(columnNames, objStrName, pkeyValue);
	}
	
	/**
	 * Gets the record from the given object store according to the Primary key value specified. Only the requested column
	 * names are retrieved. <br>
	 * @param columnNames Names of the columnNames to be printed
	 * @param objStrName Name of the object store
	 * @param pkeyValue Primary key value for record retrieval
	 * @return Record with data for the specified columns
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists
	// check if columnNames already exist in the object store
	// check whether pkey value already exists
	public static String[] getRecordColumnByPkey(String[] columnNames, String objStrName, String pkeyValue) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		for(int j=0; j<columnNames.length; j++) {
			if( checkIfColumnNameAlreadyExists(objStrName, columnNames[j])==false )
				throw new LocalStorageDatabaseException("Column name '"+columnNames[j]+"' doesn't exist in the object store '"+objStrName+"'");
		}
		
		if( !checkIfValueAlreadyExists(objStrName, get(objStrName, "col_0"), pkeyValue) )
			throw new LocalStorageDatabaseException("'"+pkeyValue+"' doesn't exist in the object store '"+objStrName+"'");
		
		//ArrayList<String> columnList = getColumnList(objStrName);
		
		String[] record = new String[columnNames.length];
		for(int j=0; j<columnNames.length; j++) {
			if (isPkey(objStrName, columnNames[j]))
				record[j] = pkeyValue;
			else
				record[j] = get(objStrName, "col_"+columnNames[j]+"_value_"+pkeyValue);
		}
		return record;
	}
	
	
	/**
	 * Prints the record, with tabs 
	 * @param record
	 * @return Return the final result as a String
	 */
	public static String printRecord(String[] record) {
		String result = "";
		for(int j=0; j<record.length; j++) {
			result = result + record[j]+"\t";
		}
		result = result + "\n";
		return result;
	}
	
	/**
	 * Gets all the records from the given object store
	 * Equivalent to the SQL statement - "select * from objStrName"
	 * @param objStrName Name of the object store
	 * @return The list of String arrays containing all data
	 */
	// check if db is being used, check if object store already exists
	// what happens when the object store is empty?
	public static ArrayList<String[]> selectAll(String objStrName) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");

		ArrayList<String[]> records = new ArrayList<String[]>();
		
		ArrayList<String> columnList = getColumnList(objStrName);
		int columnListSize = columnList.size();
		String pkeyValue=null;
		int numOfRecords = getNumberOfRecords(objStrName);
		
		ArrayList<String> pkeyValueList = getValuesFromColumn( objStrName, get(objStrName, "col_0") );
		for(int i=0; i<numOfRecords; i++) {
			String[] current = new String[columnListSize];
			pkeyValue = pkeyValueList.get(i);
			//fill in the current string array with all column values
			current[0] = pkeyValue;
			for (int k=1; k<columnListSize; k++) {
				current[k] = get(objStrName, "col_"+columnList.get(k)+"_value_"+pkeyValue);
			}
			records.add(current);
		}
		
		return records;
	}
	
	
	/**
	 * Gets all the records from the given object store and returns only the column names requested. 
	 * Equivalent to the sql query - "select <list of column names> from objStrName"<br>
	 * @param columnNames Names of the columnNames to be printed
	 * @param objStrName Name of the object store
	 * @return The list of String arrays containing all data for the specified columns
	 */
	// check if db is being used, check if object store already exists
	// check if column names are existing in the object store
	// what happens when the object store is empty?
	public static ArrayList<String[]> selectAllColumn(String[] columnNames, String objStrName) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		for(int j=0; j<columnNames.length; j++) {
			if( checkIfColumnNameAlreadyExists(objStrName, columnNames[j])==false )
				throw new LocalStorageDatabaseException("Column name '"+columnNames[j]+"' doesn't exist in the object store '"+objStrName+"'");
		}
		
		//ArrayList<String> columnList = getColumnList(objStrName);
		ArrayList<String[]> records = new ArrayList<String[]>();
		
		int columnNamesSize = columnNames.length;
		String pkeyValue=null;
		int numOfRecords = getNumberOfRecords(objStrName);
		
		ArrayList<String> pkeyValueList = getValuesFromColumn( objStrName, get(objStrName, "col_0") );
		for(int i=0; i<numOfRecords; i++) {
			String[] current = new String[columnNamesSize];
			pkeyValue = pkeyValueList.get(i);
			//fill in the current string array with all column values
			for (int k=0; k<columnNamesSize; k++) {
				if (isPkey(objStrName, columnNames[k]))
					current[k] = pkeyValue;
				else
					current [k] = get(objStrName, "col_"+columnNames[k]+"_value_"+pkeyValue);
			}
			records.add(current);
		}
		
		return records;
	}
	
	/**
	 * Prints the selected records
	 * @param recordList List of records to be printed
	 * @return Result as a string
	 */
	public static String printRecords(ArrayList<String[]> recordList) {
		String result = "";
		if(recordList.size()!=0) {
			int columnSize = recordList.get(0).length;
			for(int i=0; i<recordList.size(); i++) {
				String [] record = recordList.get(i);
				for(int j=0; j<columnSize; j++) {
					result = result + record[j]+"\t";
				}
				result = result + "\n";
			}
		}
		return result;
	}
	
	/**
	 * Updates the ith record according to the given map
	 * @param objStrName Name of the object store
	 * @param map Array of string containing the column names and the values - defined as col1,val1,col2,val2... etc. 
	 * The columns can be defined in any order.<br>
	 * <b>Note:</b> Make sure not to specify the primary key in the map, otherwise an exception will be thrown 
	 * @param i The number i
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists - done in getPkeyForIthRecord
	// check that i should be less than or equal to number of records, and greater than zero - done in getPkeyForIthRecord
	// check if column names in map are existing in the object store - done in updateRecordByPkey
	// check for isUnique while adding - done in updateRecordByPkey
	// check that primary key should not be specified in the column names - done in updateRecordByPkey 
	public static void updateIthRecord(String objStrName, String[] map, int i ) throws LocalStorageDatabaseException {
		String pkeyValue = getPkeyForIthRecord(objStrName, i);
		updateRecordByPkey(objStrName, map, pkeyValue);
	}
	
	/**
	 * Updates the record according to the primary key specified
	 * @param objStrName Name of the object store
	 * @param map Array of string containing the column names and the values - defined as col1,val1,col2,val2... etc. 
	 * The columns can be defined in any order.<br>
	 * <b>Note:</b> Make sure not to specify the primary key in the map, otherwise an exception will be thrown
	 * @param pkeyValue The primary key value
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists
	// check if column names in map are existing in the object store
	// check that primary key should not be specified in the column names
	// check if the primary key value actually exists among the filled data
	// check for isUnique while updating
	// check for notNull while updating
	public static void updateRecordByPkey(String objStrName, String[] map, String pkeyValue) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		if( !checkIfValueAlreadyExists(objStrName, get(objStrName, "col_0"), pkeyValue) )
			throw new LocalStorageDatabaseException("'"+pkeyValue+"' doesn't exist in the object store '"+objStrName+"'");
		
		for(int i=0; i<map.length; i+=2) {
			if (isPkey(objStrName, map[i]))
				throw new LocalStorageDatabaseException("Primary key can't be specified inside the map");
			if( checkIfColumnNameAlreadyExists(objStrName, map[i])==false )
				throw new LocalStorageDatabaseException("Column name '"+map[i]+"' doesn't exist in the object store '"+objStrName+"'");
			if(map[i+1]!=null && isUnique(objStrName, map[i]) && checkIfValueAlreadyExists(objStrName, map[i], map[i+1]) ) {
				//if pkey and the associated value are the same, then don't throw this error
				String existingValue = get(objStrName, "col_"+map[i]+"_value_"+pkeyValue);
				if(!map[i+1].equals(existingValue))
					throw new LocalStorageDatabaseException("Unique pre-condition for the column '"+map[i]+"' has failed");
			}
			if( map[i+1]==null && isNotNull(objStrName, map[i]) )
				throw new LocalStorageDatabaseException("NotNull pre-condition for the column '"+map[i]+"' has failed");
		}
		
		//ArrayList<String> columnList = getColumnList(objStrName);
		LdbApi.lastPkeyNo = pkeyValue;
		for (int j=0; j<map.length; j+=2) {
			if(map[j+1]==null)
				remove(objStrName, "col_"+map[j]+"_value_"+pkeyValue);
			else
				set(objStrName, "col_"+map[j]+"_value_"+pkeyValue, map[j+1]);
		}
	}
	
	/**
	 * Adds of updates record according to the primary key that is specified
	 * @param objStrName Name of the object store
	 * @param map Array of string containing the column names and the values - defined as col1,val1,col2,val2... etc. 
	 * The columns can be defined in any order.<br>
	 * <b>Note:</b> Make sure not to specify the primary key in the map, otherwise an exception will be thrown
	 * @param pkeyValue The primary key value
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists - done in checkIfValueAlreadyExists(getValuesFromColumn)
	// check if column names in map are existing in the object store
	// check that primary key should not be specified in the column names
	// checks on the null values inside the map for the notNull columns
	// check for isUnique while adding
	// can only be used for adding something when primary key is not auto-generated
	public static void addOrUpdateRecord(String objStrName, String[] map, String pkeyValue) 
			throws LocalStorageDatabaseException {
		
		if( checkIfValueAlreadyExists(objStrName, get(objStrName, "col_0"), pkeyValue) ) {
			updateRecordByPkey(objStrName, map, pkeyValue);
		}
		else {
			if(isAutoGenerate(objStrName))
				throw new LocalStorageDatabaseException("Primary key can't be auto-generated for using this method");
			String[] newMap = new String[map.length+2];
			newMap[0] = get(objStrName, "col_0");
			newMap[1] = pkeyValue;
			for(int i=0; i<map.length; i++) {
				newMap[i+2] = map[i];
			}
			addRecord(objStrName, newMap);
		}
	}
	
	/**
	 * Adds of updates record according to the primary key specified in the map
	 * @param objStrName Name of the object store
	 * @param map Array of string containing the values - defined as val1,val2,val3... etc. 
	 * The primary key should be non-autogenerated and should be specified as non-null, else exception will be raised.<br>
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists
	// can only be used for adding something when primary key is not auto-generated
	// check that first column is the primary key specified - not null
	// checks on the null values inside the map for the notNull columns - done in add or update
	// check for isUnique while adding - done in add or update
	public static void addOrUpdateRecordAllValues(String objStrName, String[] map)
			throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		if(isAutoGenerate(objStrName))
			throw new LocalStorageDatabaseException("Primary key can't be auto-generated for using this method");
		if(map[0]==null)
			throw new LocalStorageDatabaseException("Primary key should be specified");
		
		ArrayList<String> columnList = getColumnList(objStrName);
		if(columnList.size()!=map.length)
			throw new LocalStorageDatabaseException("Number of columns and number of specified values don't match up");
		
		if( checkIfValueAlreadyExists(objStrName, get(objStrName, "col_0"), map[0]) ) {
			String[] updateMap = new String[(map.length-1)*2];
			for(int i=0; i<map.length-1; i++) {
				updateMap[2*i] = columnList.get(i+1);
				updateMap[2*i+1] = map[i+1];
			}
			updateRecordByPkey(objStrName, updateMap, map[0]);
		} else {
			addRecordAllValues(objStrName, map);
		}
	}
	
	/**
	 * Deletes the ith record
	 * @param objStrName Name of the object store
	 * @param i Number i
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists - done by getPkeyForIthRecord
	// check that i should be less than or equal to number of records, and greater than zero - done by getPkeyForIthRecord
	public static void deleteIthRecord(String objStrName, int i) throws LocalStorageDatabaseException {
		String pkeyValue = getPkeyForIthRecord(objStrName, i);
		deleteRecordByPkey(objStrName, pkeyValue);
	}
	
	/**
	 * Deletes a record, given the primary key value for the record
	 * @param objStrName Name of object store
	 * @param pkeyValue The value of primary key for deletion
	 */
	// check if db is being used, check if object store already exists
	// check if the primary key value actually exists among the filled data
	public static void deleteRecordByPkey(String objStrName, String pkeyValue) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		if( !checkIfValueAlreadyExists(objStrName, get(objStrName, "col_0"), pkeyValue) )
			throw new LocalStorageDatabaseException("'"+pkeyValue+"' doesn't exist in the object store '"+objStrName+"'");
		
		ArrayList<String> columnList = getColumnList(objStrName);
		
		int number = searchForNumber("db_"+db+"_objstr_"+objStrName+"_pkey", pkeyValue);
		if(number!=-1)
			remove(objStrName, "pkey_"+number);
		
		for(int j=1; j<columnList.size(); j++) {
			remove(objStrName, "col_"+columnList.get(j)+"_value_"+pkeyValue);
		}
	}
	
	/**
	 * Deletes all records from the object store
	 * @param objStrName Name of object store
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists
	// what happens if the object store is already empty? Should just reset the pkey_no to zero
	public static void deleteAllRecords(String objStrName) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		int numOfRecords = getNumberOfRecords(objStrName);
		for(int i=0; i<numOfRecords; i++) {
			deleteIthRecord(objStrName, 1);
		}
		set(objStrName, "pkey_no","0");
	}
	
	/**
	 * Deletes the whole object store
	 * @param objStrName Name of the object store to be removed
	 * @throws LocalStorageDatabaseException
	 */
	// check if db is being used, check if object store already exists
	public static void deleteObjectStore(String objStrName) throws LocalStorageDatabaseException {
		throwExceptionIfDbIsNotBeingUsed();
		if(checkIfObjStrAlreadyExists(objStrName)==false)
			throw new LocalStorageDatabaseException("Object store '"+objStrName+"' doesn't exist");
		
		ArrayList<String> keys = new ArrayList<String>();
		
		for(int i=0; i<storage.getLength(); i++) {
			keys.add(storage.key(i));
		}
		for(int i=0; i<keys.size(); i++) {
			if(keys.get(i).startsWith("db_"+db+"_objstr_"+objStrName+"_"))
				remove(keys.get(i));
		}
		
		int number = searchForNumber("db_"+db+"_objstr", objStrName);
		if(number!=-1)
			remove("db_"+db+"_objstr_"+number);
	}
	
	/**
	 * Drops the whole database
	 * @param dbName Name of the database to be dropped/removed
	 */
	//check if dbName exists
	public static void dropDatabase(String dbName) throws LocalStorageDatabaseException {
		if (!checkIfDbAlreadyExists(dbName))
			throw new LocalStorageDatabaseException("'"+dbName+"' doesn't exist");
		
		ArrayList<String> keys = new ArrayList<String>();
		for(int i=0; i<storage.getLength(); i++) {
			keys.add(storage.key(i));
		}
		for(int i=0; i<keys.size(); i++) {
			if(keys.get(i).startsWith("db_"+dbName+"_"))
				remove(keys.get(i));
		}
			
		int number = searchForNumber("db", dbName);
		if(number!=-1)
			remove("db_"+number);
	}
}
