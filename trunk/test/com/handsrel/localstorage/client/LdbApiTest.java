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

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.storage.client.Storage;

/**
 * A test file containing all tests to test for the various methods provided in the localstorage-db
 * API 
 * @author Raghu Mittal
 *
 */
public class LdbApiTest extends GWTTestCase {

	private Storage storage;
	
	@Override
	public String getModuleName() {
		return "com.handsrel.localstorage.Localstorage";
	}
	
	@Override
	public void gwtSetUp() {
		storage = Storage.getLocalStorageIfSupported();
		
		LdbApi.initialize(storage);
		try {
			LdbApi.createDb("ecollect");
			LdbApi.useDb("ecollect");
			LdbApi.createObjectStore("formData");
			LdbApi.addColumn("formData", new Column(true, true, "formDataId"));
			LdbApi.addColumn("formData", new Column("formDefinitionVersionId", false, true));
			LdbApi.addColumn("formData", new Column("description", false, false));
			LdbApi.addColumn("formData", new Column("data", false, true));
			LdbApi.addColumn("formData", new Column("creator", true, false));
			
			LdbApi.addRecord("formData", new String[]{"formDefinitionVersionId","1", "data","my data","creator","3"});
			LdbApi.addRecord("formData", new String[]{"formDefinitionVersionId","1", "data","my data","creator","4"});
			
			LdbApi.createObjectStore("users");
			LdbApi.addColumn("users", new Column(true, false, "userId"));
			LdbApi.addColumn("users", new Column("userName", true, true));
			
			LdbApi.addRecord("users", new String[]{"userId","1", "userName", "raghu"});
			LdbApi.addRecordAllValues("users", new String[]{"2", "rahul"});
			LdbApi.addRecordAllValues("users", new String[]{"3", "sneha"});
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
		
		LdbApi.setDb(null); //No db in use
	}
	
	@Override
	public void gwtTearDown() {
		storage.clear();
	}

	public void testInitialize() {
		assertNotNull(storage);
		
		String db_no = storage.getItem("db_no");
		assertNotNull(db_no);
	}
	
	//TODO check all methods calling get( ) are checking for useDb and raise exceptions
	//TODO same for set( )
	//TODO same for remove
	
	public void testCreateDb() {
		try {
			LdbApi.createDb("ecollect");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Database 'ecollect' already exists", e.getMessage());
		}
		//System.out.println(storage.getItem("db_no"));
		assertEquals("1", storage.getItem("db_no"));
		assertEquals("ecollect", storage.getItem("db_0"));
	}
	
	public void testUseDb() {
		try {
			LdbApi.useDb("ecollect123");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Database 'ecollect123' doesn't exist", e.getMessage());
		}
		
		try {
			LdbApi.useDb("ecollect");
			assertEquals("ecollect", LdbApi.getDb());
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testCreateObjectStore() {
		try {
			LdbApi.createObjectStore("formData");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}
		
		try {
			LdbApi.useDb("ecollect");
			LdbApi.createObjectStore("formData");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData' already exists", e.getMessage());
		}
		
		try {
			assertEquals("formData\nusers\n", LdbApi.printList(LdbApi.getObjStoreList()) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}	
	}
	
	public void testAddColumn() {
		try {
			LdbApi.addColumn("formDef", new Column(true,true,"formDefXml"));
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}
		
		try {
			LdbApi.useDb("ecollect");
			LdbApi.addColumn("formDef", new Column(true,true,"formDefXml"));
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formDef' doesn't exist", e.getMessage());
		}
		
		try {
			LdbApi.useDb("ecollect");
			LdbApi.createObjectStore("formDef");
			LdbApi.addColumn("formDef", new Column("formDefXml", false, true));
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key column should be the first column", e.getMessage());
		}
		
		try {
			LdbApi.addColumn("formDef", new Column(true, true, "formDefId"));
			LdbApi.addColumn("formDef", new Column("formDefId", true, false));
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'formDefId' already exists", e.getMessage());
		}
		
		try {
			LdbApi.addColumn("formDef", new Column(true, false, "formDefSno"));
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key column should be the first column", e.getMessage());
		}
		//Exception checking done
		
		try {
			assertEquals("formDataId\nformDefinitionVersionId\ndescription\ndata\ncreator\n",
					LdbApi.printList(LdbApi.getColumnList("formData")) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	public void testAddRecord() {
		// check if db is being used
		try {
			LdbApi.addRecord("formData", new String[0]);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}
		
		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.addRecord("formData1", new String[]{"formDefinitionVersionId","1"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check if the column names in map are present in the object store
		try {
			LdbApi.addRecord("formData", new String[]{"formDefinitionVersionId","1","description1", "My description"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'description1' doesn't exist in the object store 'formData'", e.getMessage());
		}
		
		// check if key autogenerated - then the map shouldn't contain a value for the primary key
		try {
			LdbApi.addRecord("formData", new String[]{"formDataId","3","description", "My description"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key is autogenerated, its value can't be specified", e.getMessage());
		}
		
		// check if key not autogenerated, then the map should specify a value for the primary key
		try {
			LdbApi.addRecord("users", new String[]{"userName", "raghu"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Since primary key is not autogenerated, the first column to be set should be the primary key", e.getMessage());
		}
		
		//if pkey is specified, but not the first one, then it should throw an exception
		try {
			LdbApi.addRecord("users", new String[]{"userName", "raghu", "userId", "1"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Since primary key is not autogenerated, the first column to be set should be the primary key", e.getMessage());
		}
		
		//primary key should be unique
		try {
			LdbApi.addRecord("users", new String[]{"userId","1", "userName", "rahul"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key should have unique value", e.getMessage());
		}
		
		//primary key should be non-null
		try {
			LdbApi.addRecord("users", new String[]{"userId",null,"userName", "raghu"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key value can't be null", e.getMessage());
		}
		
		// check that if a column is unique, then the value to be added should be unique
		try {
			LdbApi.addRecord("users", new String[]{"userId","4","userName", "raghu"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Unique pre-condition for the column 'userName' has failed", e.getMessage());
		}
		
		// check that if a column is notNull, then it should be specified in the map
		try {
			LdbApi.addRecord("formData", new String[]{"formDefinitionVersionId","2","description", "My description"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column 'data' is notNull, so the value should be specified", e.getMessage());
		}
		
		// all checks done
		try {
			LdbApi.addRecord("formData", new String[]{"formDefinitionVersionId","2", "data","my data1","creator","5"});
			LdbApi.addRecord("formData", new String[]{"formDefinitionVersionId","2", "data","my data1","creator","6"});
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testAddRecordAllValues() {
		// check if db is being used
		try {
			LdbApi.addRecordAllValues("formData", new String[]{null,"2", "my data"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}
		
		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.addRecordAllValues("formData1", new String[]{null,"2", "my data"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check key autogenerate - then the map shouldn't contain a value for the primary key
		try {
			LdbApi.addRecordAllValues("formData", new String[]{"3","3", "my descr", "my data", "3"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key being autogenerated can be only specified as null", e.getMessage());
		}
		
		// check if key not autogenerated, then the map should specify a value for the primary key
		try {
			LdbApi.addRecordAllValues("users", new String[]{null,"raghu1"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key should be specified", e.getMessage());
		}
		
		// check that number of values specified matches with number of columns inside the object store
		try {
			LdbApi.addRecordAllValues("users", new String[]{"4","raghu1","raghu2"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Number of columns and number of specified values don't match up", e.getMessage());
		}
		
		// check that pkey is unique when specifying it
		try {
			LdbApi.addRecordAllValues("users", new String[]{"1","raghukmittal"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key value should be uniquely specified", e.getMessage());
		}
		
		// check that if a column is unique, then the value to be added should be unique
		try {
			LdbApi.addRecordAllValues("formData", new String[]{null, "3", "my descr", "my data", "3"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'3' value already exists in the 'creator'. Unique precondition failed", e.getMessage());
		}
		
		// check that if a column is notNull, then it should be specified in the map
		try {
			LdbApi.addRecordAllValues("formData", new String[]{null, "3", "my descr", null, "5"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'data' should be specified and not contain null values", e.getMessage());
		}
		
		//all exception checks done
		
		try {
			LdbApi.addRecordAllValues("users", new String[]{"4", "swetha"});
			LdbApi.addRecordAllValues("users", new String[]{"5", "mahesh"});
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
		
	}
	
	public void testGetPkeyForIthRecord() {
		// check if db is being used
		try {
			LdbApi.getPkeyForIthRecord("formData", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}
		
		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getPkeyForIthRecord("formData1", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check that i should be less than or equal to number of records, and greater than zero
		try {
			LdbApi.getPkeyForIthRecord("formData", 0);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value should be greater than zero", e.getMessage());
		}
		
		try {
			LdbApi.getPkeyForIthRecord("formData", 3);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value cannot be more than the number of records in the object store", e.getMessage());
		}
		
		try {
			String pkeyValue = LdbApi.getPkeyForIthRecord("formData", 2);
			assertEquals("1", pkeyValue);
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	public void testGetIthRecord() {
		// check if db is being used
		try {
			LdbApi.getIthRecord("formData", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getIthRecord("formData1", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}

		// check that i should be less than or equal to number of records, and greater than zero
		try {
			LdbApi.getIthRecord("formData", 0);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value should be greater than zero", e.getMessage());
		}

		try {
			LdbApi.getIthRecord("formData", 3);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value cannot be more than the number of records in the object store", e.getMessage());
		}
		
		try {
			assertEquals("1\traghu\t\n", LdbApi.printRecord(LdbApi.getIthRecord("users", 1)) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	
	public void testGetRecordByPkey() {
		// check if db is being used
		try {
			LdbApi.getRecordByPkey("formData", "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getRecordByPkey("formData1", "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}

		// check that pkeyvalue should be there
		try {
			LdbApi.getRecordByPkey("formData", "3");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'3' doesn't exist in the object store 'formData'", e.getMessage());
		}

		try {
			assertEquals("1\traghu\t\n", LdbApi.printRecord(LdbApi.getRecordByPkey("users", "1")) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testGetIthRecordColumn() {
		// check if db is being used
		try {
			LdbApi.getIthRecordColumn(new String[]{"data","description"}, "formData", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getIthRecordColumn(new String[]{"data","description"}, "formData1", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check if columnNames already exist in the object store
		try {
			LdbApi.getIthRecordColumn(new String[]{"description","data1"}, "formData", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'data1' doesn't exist in the object store 'formData'", e.getMessage());
		}
		
		// checks on i
		try {
			LdbApi.getIthRecordColumn(new String[]{"description","data"}, "formData", 0);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value should be greater than zero", e.getMessage());
		}

		try {
			LdbApi.getIthRecordColumn(new String[]{"description","data"}, "formData", 3);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value cannot be more than the number of records in the object store", e.getMessage());
		}
		
		//all checks on exceptions have been completed
		try {
			String record = LdbApi.printRecord( LdbApi.getIthRecordColumn(new String[]{"description","data"}, "formData", 2) );
			assertEquals("null\tmy data\t\n", record);			
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	
	public void testGetRecordColumnByPkey() {
		// check if db is being used
		try {
			LdbApi.getRecordColumnByPkey(new String[]{"data","description"}, "formData", "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getRecordColumnByPkey(new String[]{"data","description"}, "formData1", "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check if columnNames already exist in the object store
		try {
			LdbApi.getRecordColumnByPkey(new String[]{"description","data1"}, "formData", "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'data1' doesn't exist in the object store 'formData'", e.getMessage());
		}
		
		// check whether pkeyvalue exists
		try {
			LdbApi.getRecordColumnByPkey(new String[]{"description","data"}, "formData", "3");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'3' doesn't exist in the object store 'formData'", e.getMessage());
		}

		//all checks on exceptions have been completed
		try {
			String record = LdbApi.printRecord( LdbApi.getRecordColumnByPkey(new String[]{"description","data"}, "formData", "1") );
			assertEquals("null\tmy data\t\n", record);			
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	public void testSelectAll() {
		// check if db is being used
		try {
			LdbApi.selectAll("formData");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.selectAll("formData1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
	}
	
	
	public void testSelectAllColumn() {
		// check if db is being used
		try {
			LdbApi.selectAllColumn(new String[]{"data", "description"}, "formData");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.selectAllColumn(new String[]{"data", "description"}, "formData1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check if columnNames already exist in the object store
		try {
			LdbApi.selectAllColumn(new String[]{"description","data1"}, "formData");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'data1' doesn't exist in the object store 'formData'", e.getMessage());
		}

		//all checks on exceptions have been completed
		try {
			String records = LdbApi.printRecords( LdbApi.selectAllColumn(new String[]{"description","data"}, "formData") );
			assertEquals("null\tmy data\t\nnull\tmy data\t\n", records);			
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	
	public void testUpdateIthRecord() {
		// check if db is being used
		try {
			LdbApi.updateIthRecord("formData", new String[]{"data", "my changed data"}, 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.updateIthRecord("formData1", new String[]{"data", "my changed data"}, 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check if columnNames already exist in the object store
		try {
			LdbApi.updateIthRecord("formData", new String[]{"description", "my descr", "data1", "my changed data"}, 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'data1' doesn't exist in the object store 'formData'", e.getMessage());
		}
				
		// check that i should be less than or equal to number of records, and greater than zero
		try {
			LdbApi.updateIthRecord("formData", new String[]{"data", "my changed data"}, 0);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value should be greater than zero", e.getMessage());
		}

		try {
			LdbApi.updateIthRecord("formData", new String[]{"data", "my changed data"}, 3);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value cannot be more than the number of records in the object store", e.getMessage());
		}
		
		// check that primary key is not part of the columnNames in the map
		try {
			LdbApi.updateIthRecord("formData", new String[]{"description", "my changed descr","formDataId","3"}, 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key can't be specified inside the map", e.getMessage());
		}
		
		// check for isUnique while changing something
		try {
			// creator is the same as before, but it shouldn't throw a Unique pre-condition failed
			LdbApi.updateIthRecord("formData", new String[]{"description", "my changed descr","creator","3"}, 1);
			
			LdbApi.updateIthRecord("formData", new String[]{"description", "my changed descr","creator","3"}, 2);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Unique pre-condition for the column 'creator' has failed", e.getMessage());
		}
		
		// check for notNull condition in the updated columns
		try {
			//should work fine
			LdbApi.updateIthRecord("formData", new String[]{"description", "my changed descr","creator", null}, 2);
			
			LdbApi.updateIthRecord("formData", new String[]{"description", "my changed descr","data", null}, 2);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("NotNull pre-condition for the column 'data' has failed", e.getMessage());
		}
		
		// all checks on exceptions completed
		try {
			LdbApi.updateIthRecord("formData", new String[]{"description", "my changed descr","creator","5"}, 2);
			assertEquals("1\t1\tmy changed descr\tmy data\t5\t\n", LdbApi.printRecord(LdbApi.getIthRecord("formData", 2)));
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	
	public void testUpdateRecordByPkey() {
		// check if db is being used
		try {
			LdbApi.updateRecordByPkey("formData", new String[]{"data", "my changed data"}, "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.updateRecordByPkey("formData1", new String[]{"data", "my changed data"}, "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check if columnNames in map already exist in the object store
		try {
			LdbApi.updateRecordByPkey("formData", new String[]{"description", "my descr", "data1", "my changed data"}, "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'data1' doesn't exist in the object store 'formData'", e.getMessage());
		}
		
		// check that primary key is not part of the columnNames in the map
		try {
			LdbApi.updateRecordByPkey("formData", new String[]{"description", "my changed descr","formDataId","3"}, "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key can't be specified inside the map", e.getMessage());
		}
		
		// check if the primary key value actually exists among the filled data
		try {
			LdbApi.updateRecordByPkey("formData", new String[]{"description", "my changed descr", "data", "my data"}, "2");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'2' doesn't exist in the object store 'formData'", e.getMessage());
		}
		
		// check for isUnique while changing something
		try {
			// creator is the same as before, but it shouldn't throw a Unique pre-condition failed
			LdbApi.updateRecordByPkey("formData", new String[]{"description", "my changed descr","creator","3"}, "0");
			
			LdbApi.updateRecordByPkey("formData", new String[]{"description", "my changed descr","creator","3"}, "1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Unique pre-condition for the column 'creator' has failed", e.getMessage());
		}
		
		// check for notNull condition in the updated columns
		try {
			//should work fine
			LdbApi.updateRecordByPkey("formData", new String[]{"description", "my changed descr","creator", null}, "1");

			LdbApi.updateRecordByPkey("formData", new String[]{"description", "my changed descr","data", null}, "1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("NotNull pre-condition for the column 'data' has failed", e.getMessage());
		}
		
		// all checks on exceptions completed
		try {
			LdbApi.updateRecordByPkey("formData", new String[]{"description", "my changed descr","creator","5"}, "1");
			assertEquals("1\t1\tmy changed descr\tmy data\t5\t\n", LdbApi.printRecord(LdbApi.getRecordByPkey("formData", "1")));
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testAddOrUpdateRecord() {
		// check if db is being used
		try {
			LdbApi.addOrUpdateRecord("formData", new String[]{"data", "my changed data"}, "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.addOrUpdateRecord("formData1", new String[]{"data", "my changed data"}, "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// check if columnNames in map already exist in the object store
		try {
			LdbApi.addOrUpdateRecord("formData", new String[]{"description", "my descr", "data1", "my changed data"}, "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'data1' doesn't exist in the object store 'formData'", e.getMessage());
		}
		
		try {
			LdbApi.addOrUpdateRecord("users", new String[]{"password", "my pass"}, "4");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'password' doesn't exist in the object store 'users'", e.getMessage());
		}
		
		// check that primary key should not be specified in the column names
		try {
			LdbApi.addOrUpdateRecord("formData", new String[]{"description", "my descr", "formDataId", "my changed data"}, "0");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key can't be specified inside the map", e.getMessage());
		}
		
		//TODO leave it as it is -the user should ensure that he doesn't mention the primary key while adding
		//try {
		//	LdbApi.addOrUpdateRecord("users", new String[]{"userId", "4", "userName", "swetha"}, "4");
		//	fail();
		//} catch (LocalStorageDatabaseException e) {
		//	assertEquals("Primary key can't be specified inside the map", e.getMessage());
		//}
		
		// checks on the null values inside the map for the notNull columns
		try {
			//should work fine
			LdbApi.addOrUpdateRecord("formData", new String[]{"description", "my changed descr","creator", null}, "1");

			LdbApi.addOrUpdateRecord("formData", new String[]{"description", "my changed descr","data", null}, "1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("NotNull pre-condition for the column 'data' has failed", e.getMessage());
		}
		
		try {
			LdbApi.addOrUpdateRecord("users", new String[]{"userName", null}, "4");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("NotNull pre-condition for the column 'userName' has failed", e.getMessage());
		}
		
		// check for isUnique while adding/updating
		try {
			LdbApi.addOrUpdateRecord("formData", new String[]{"description", "my changed descr","creator","3"}, "1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Unique pre-condition for the column 'creator' has failed", e.getMessage());
		}
		
		try {
			LdbApi.addOrUpdateRecord("users", new String[]{"userName", "raghu"}, "4");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Unique pre-condition for the column 'userName' has failed", e.getMessage());
		}
		
		// can only be used for adding something when primary key is not auto-generated
		try {
			LdbApi.addOrUpdateRecord("formData", new String[]{"description", "my changed descr","creator","6"}, "2");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key can't be auto-generated for using this method", e.getMessage());
		}
	}
	
	public void testAddOrUpdateRecordAllValues() {
		// check if db is being used
		try {
			LdbApi.addOrUpdateRecordAllValues("formData", new String[]{"3","3", "my descr", "my data", "5"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.addOrUpdateRecordAllValues("formData1", new String[]{"3","3", "my descr", "my data", "5"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		// can only be used for adding something when primary key is not auto-generated
		try {
			LdbApi.addOrUpdateRecordAllValues("formData", new String[]{"3","3", "my descr", "my data", "5"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key can't be auto-generated for using this method", e.getMessage());
		}
		
		// check that first column is the primary key specified - not null
		try {
			LdbApi.addOrUpdateRecordAllValues("users", new String[]{null, "mahesh"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key should be specified", e.getMessage());
		}
		
		// checks on the null values inside the map for the notNull columns
		try {
			LdbApi.addOrUpdateRecordAllValues("users", new String[]{"3", null});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("NotNull pre-condition for the column 'userName' has failed", e.getMessage());
		}
		
		try {
			LdbApi.addOrUpdateRecordAllValues("users", new String[]{"4", null});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'userName' should be specified and not contain null values", e.getMessage());
		}
		
		// check for isUnique while adding
		try {
			LdbApi.addOrUpdateRecordAllValues("users", new String[]{"3", "raghu"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Unique pre-condition for the column 'userName' has failed", e.getMessage());
		}
		
		try {
			LdbApi.addOrUpdateRecordAllValues("users", new String[]{"4", "raghu"});
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'raghu' value already exists in the 'userName'. Unique precondition failed", e.getMessage());
		}
	}
	
	public void testGetDbList() {
		//no exceptions raised, just check out correctness
		try {
			LdbApi.createDb("ecollectUpdate");
			String dbList = LdbApi.printList( LdbApi.getDbList() );
			assertEquals("ecollect\necollectUpdate\n", dbList);
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	public void testGetObjStoreList() {
		// check if db is being used
		try {
			LdbApi.getObjStoreList();
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}
		
		// all checks for the exceptions have been completed
		try {
			LdbApi.useDb("ecollect");
			String storeList = LdbApi.printList( LdbApi.getObjStoreList() );
			assertEquals("formData\nusers\n", storeList);
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	public void testGetColumnList() {
		// check if db is being used
		try {
			LdbApi.getColumnList("formData");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getColumnList("formData1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		//all exception related checks completed
		try {
			String columnList = LdbApi.printList( LdbApi.getColumnList("formData") );
			assertEquals("formDataId\nformDefinitionVersionId\ndescription\ndata\ncreator\n", columnList);
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testGetValuesFromColumn() {
		// check if db is being used
		try {
			LdbApi.getValuesFromColumn("formData", "formDataId");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getValuesFromColumn("formData1", "formDataId");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		//check if columnName exists
		try {
			LdbApi.getValuesFromColumn("formData", "data1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'data1' doesn't exist", e.getMessage());
		}
		
		//check if values are correctly retrieved from primary key as well as a non-primary key column
		try {
			assertEquals("0\n1\n", LdbApi.printList( LdbApi.getValuesFromColumn("formData", "formDataId") ) );
			assertEquals("3\n4\n", LdbApi.printList( LdbApi.getValuesFromColumn("formData", "creator") ) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	
	public void testGetNumberOfRecords() {
		// check if db is being used
		try {
			LdbApi.getNumberOfRecords("formData");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getNumberOfRecords("formData1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}
		
		//all exception checks are completed
		try {
			assertEquals(2, LdbApi.getNumberOfRecords("formData"));
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	public void testGetPkeysForValueInColumn() {
		// check if db is being used
		try {
			LdbApi.getPkeysForValueInColumn("users", "userName", "raghu", true);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}
		
		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.getPkeysForValueInColumn("users1", "userName", "raghu", true);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'users1' doesn't exist", e.getMessage());
		}
		
		//check if columnName exists
		try {
			LdbApi.getPkeysForValueInColumn("users", "userName2", "raghu", true);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Column name 'userName2' doesn't exist", e.getMessage());
		}
		
		//check to see that primary key is not specified for searching
		try {
			LdbApi.getPkeysForValueInColumn("users", "userId", "1", false);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Primary key can't be specified for searching the records", e.getMessage());
		}
		
		//all checks done
		try {
			assertEquals( "1\n", LdbApi.printList(LdbApi.getPkeysForValueInColumn("users", "userName", "raghu", true)) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testGetLastPkeyNo() {
		try {
			LdbApi.useDb("ecollect");
			
			//primary key auto generated
			LdbApi.addRecord("formData", new String[]{"formDefinitionVersionId","2","data","my data", "creator","6"});
			assertEquals( "2", LdbApi.getLastPkeyNo() );
			LdbApi.addRecordAllValues("formData", new String[]{null, "3", "my descr", "my dataXml","7"});
			assertEquals( "3", LdbApi.getLastPkeyNo() );
			
			//primary key not auto-generated
			LdbApi.addRecord("users", new String[]{"userId","4", "userName", "mahesh"});
			assertEquals( "4", LdbApi.getLastPkeyNo() );
			LdbApi.addRecordAllValues("users", new String[]{"5", "swetha"});
			assertEquals( "5", LdbApi.getLastPkeyNo() );
			
			LdbApi.updateIthRecord("users", new String[]{"userName","raghavendra"}, 2);
			assertEquals( "2", LdbApi.getLastPkeyNo() );
			
			LdbApi.updateRecordByPkey("users", new String[]{"userName","sneha jonnalagadda"}, "3");
			assertEquals( "3", LdbApi.getLastPkeyNo() );
			
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testDeleteIthRecord() {
		// check if db is being used
		try {
			LdbApi.deleteIthRecord("formData", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.deleteIthRecord("formData1", 1);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'formData1' doesn't exist", e.getMessage());
		}

		// check that i should be less than or equal to number of records, and greater than zero
		try {
			LdbApi.deleteIthRecord("formData", 0);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value should be greater than zero", e.getMessage());
		}

		try {
			LdbApi.deleteIthRecord("formData", 3);
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("i value cannot be more than the number of records in the object store", e.getMessage());
		}
		
		//all exception checks done 
		try {
			LdbApi.deleteIthRecord("users", 1);
			assertEquals("2\trahul\t\n", LdbApi.printRecord( LdbApi.getIthRecord("users", 1) ) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	
	public void testDeleteRecordByPkey() {
		// check if db is being used
		try {
			LdbApi.deleteRecordByPkey("users", "2");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.deleteRecordByPkey("username", "2");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'username' doesn't exist", e.getMessage());
		}

		// check if the primary key value actually exists among the filled data
		try {
			LdbApi.deleteRecordByPkey("users", "4");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'4' doesn't exist in the object store 'users'", e.getMessage());
		}
		
		//all exception checks done 
		try {
			LdbApi.deleteRecordByPkey("users", "2");
			assertEquals("3\tsneha\t\n", LdbApi.printRecord( LdbApi.getIthRecord("users", 2) ) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
		
	}
	
	
	public void testDeleteAllRecords() {
		// check if db is being used
		try {
			LdbApi.deleteAllRecords("users");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.deleteAllRecords("username");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'username' doesn't exist", e.getMessage());
		}

		//all exception checks already done
		try {
			LdbApi.addRecordAllValues("users", new String[]{"4","swetha"});
			LdbApi.addRecordAllValues("users", new String[]{"5","mahesh"});
			LdbApi.deleteAllRecords("users");
			assertEquals(0, LdbApi.getNumberOfRecords("users"));
			assertEquals( "0",storage.getItem("db_ecollect_objstr_users_pkey_no") );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testDeleteObjectStore() {
		// check if db is being used
		try {
			LdbApi.deleteObjectStore("formData");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("No database in use", e.getMessage());
		}

		// check if object store already exists
		try {
			LdbApi.useDb("ecollect");
			LdbApi.deleteObjectStore("username");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("Object store 'username' doesn't exist", e.getMessage());
		}
		
		//all exception checks already done
		try {
			LdbApi.deleteObjectStore("formData");
			assertEquals( "users\n", LdbApi.printList(LdbApi.getObjStoreList()) );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}
	}
	
	public void testDropDatabase() {
		
		// check if db being deleted exists				
		try {
			LdbApi.dropDatabase("ecollect1");
			fail();
		} catch (LocalStorageDatabaseException e) {
			assertEquals("'ecollect1' doesn't exist", e.getMessage());
		}
		
		//all exception checks already done
		try {
			LdbApi.dropDatabase("ecollect");
			
			assertEquals( "db_no:1\n", LdbApi.printAllKeys() );
		} catch (LocalStorageDatabaseException e) {
			fail();
		}

	}
	
}
