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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Class that defines a simplistic query browser
 * @author Raghu Mittal
 *
 */
public class LdbQueryBrowser extends Composite {

	public Logger logger = Logger.getLogger("");

	private Label lblFunctionToBeExec;
	private TextArea textAreaDescription;
	private Label lblParam1;
	private TextBox txtbxParam1;
	private Label lblParam2;
	private TextBox txtbxParam2;
	private Label lblParam3;
	private TextBox txtbxParam3;
	private TextArea textAreaResult;

	public LdbQueryBrowser() {
		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);

		verticalPanel.setSize("692px", "522px");

		MenuBar menuBar = new MenuBar(false);
		verticalPanel.add(menuBar);
		MenuBar menuBar_1 = new MenuBar(true);

		MenuItem mntmUse_1 = new MenuItem("Use", false, menuBar_1);

		MenuItem mntmUseDatabase = new MenuItem("Use database", false, new Command() {
			public void execute() {
				params("Use database", "", "dbName", null, null);
			}
		});
		menuBar_1.addItem(mntmUseDatabase);
		menuBar.addItem(mntmUse_1);
		MenuBar menuBar_2 = new MenuBar(true);

		MenuItem mntmCreate = new MenuItem("Create", false, menuBar_2);

		MenuItem mntmCreateDatabase = new MenuItem("Create database", false, new Command() {
			public void execute() {
				params("Create database", "", "dbName", null, null);
			}
		});
		menuBar_2.addItem(mntmCreateDatabase);

		MenuItem mntmCreateObjectStore = new MenuItem("Create object store", false, new Command() {
			public void execute() {
				params("Create object store", "", "objStrName", null, null);
			}
		});
		menuBar_2.addItem(mntmCreateObjectStore);
		menuBar.addItem(mntmCreate);
		MenuBar menuBar_3 = new MenuBar(true);

		MenuItem mntmList = new MenuItem("List", false, menuBar_3);

		MenuItem mntmGetDatabaseList = new MenuItem("Get database list", false, new Command() {
			public void execute() {
				params("Get database list", "", null, null, null);
				addToTextAreaResult( "Database list:\n"+  LdbApi.printList(LdbApi.getDbList()) );
			}
		});
		menuBar_3.addItem(mntmGetDatabaseList);

		MenuItem mntmGetObjectStore = new MenuItem("Get object store list", false, new Command() {
			public void execute() {

				try {
					params("Get object store list", "", null, null, null);
					addToTextAreaResult("Object store list:\n"+  LdbApi.printList(LdbApi.getObjStoreList()) );
				} catch (LocalStorageDatabaseException e) {
					logger.log(Level.SEVERE, "Database Exception", e);
				}
			}
		});
		menuBar_3.addItem(mntmGetObjectStore);

		MenuItem mntmGetColumnList = new MenuItem("Get column list", false, new Command() {
			public void execute() {
				params("Get column list", "", "objStrName", null, null);
			}
		});
		menuBar_3.addItem(mntmGetColumnList);

		MenuItem mntmGetValuesFrom = new MenuItem("Get values from column", false, new Command() {
			public void execute() {
				params("Get values from column", "", "objStrName", "columnName", null);
			}
		});
		menuBar_3.addItem(mntmGetValuesFrom);
		menuBar.addItem(mntmList);
		MenuBar menuBar_4 = new MenuBar(true);

		MenuItem mntmAdd = new MenuItem("Add", false, menuBar_4);

		MenuItem mntmColumn = new MenuItem("Column", false, new Command() {
			public void execute() {
				params("Add Column", "Column definition for primary key can be specified as - true, <isAutogenerate>, '<NameOfColumn>'\n" +
						"Column def for non-primary key can be specified as - '<NameOfColumn>', <isUnique>, <isNotNull>\n" +
						"Note:Please don't include <> in your values, they are meant for indication only." +
						" eg. true, false,'Sno' or 'formData', false, true", "objStrName", "column definition", null);
			}
		});
		menuBar_4.addItem(mntmColumn);

		MenuItem mntmRecord = new MenuItem("Record", false, new Command() {
			public void execute() {
				params("Add Record", "Specify the map as 'col1','val1','col2','val2'... etc. The columns can be defined in any order. " +
						"Make sure that primary key is the first one to be defined if it is not autogenerated", "objStrName", "map", null);
			}
		});
		menuBar_4.addItem(mntmRecord);

		MenuItem mntmRecordallValues = new MenuItem("Record (all values)", false, new Command() {
			public void execute() {
				params("Add Record (all values)", "Specify the map as 'val1','val2','val3'...", "objStrName", "map", null);
			}
		});
		menuBar_4.addItem(mntmRecordallValues);
		menuBar.addItem(mntmAdd);
		MenuBar menuBar_5 = new MenuBar(true);

		MenuItem mntmUpdate = new MenuItem("Update", false, menuBar_5);

		MenuItem mntmUpdateIthRecord = new MenuItem("Update ith record", false, new Command() {
			public void execute() {
				params("Update ith record", "Specify the map as 'col1','val1','col2', 'val2'...", "objStrName", "map", "i");
			}
		});
		menuBar_5.addItem(mntmUpdateIthRecord);

		MenuItem mntmUpdateRecordBy = new MenuItem("Update record by Pkey", false, new Command() {
			public void execute() {
				params("Update record by Pkey", "Specify the map as 'col1','val1','col2', 'val2'...", "objStrName", "map", "pkeyValue");
			}
		});
		menuBar_5.addItem(mntmUpdateRecordBy);
		menuBar.addItem(mntmUpdate);
		
		MenuBar menuBar_5_1 = new MenuBar(true);
		MenuItem mntmAddOrUpdate = new MenuItem("AddOrUpdate", false, menuBar_5_1);
		
		MenuItem mntmAddOrUpdateRec = new MenuItem("Add or update record", false, new Command() {
			public void execute() {
				params("Add or update record", "Specify the map as 'col1','val1','col2', 'val2'...", "objStrName", "map", "pkeyValue");
			}
		});
		menuBar_5_1.addItem(mntmAddOrUpdateRec);
		
		MenuItem mntmAddOrUpdateRecAllVal = new MenuItem("Add or update record (All values)", false, new Command() {
			public void execute() {
				params("Add or update record (All values)", "Specify the map as 'val1','val2','val3'...", "objStrName", "map", null);
			}
		});
		menuBar_5_1.addItem(mntmAddOrUpdateRecAllVal);
		
		menuBar.addItem(mntmAddOrUpdate);
		MenuBar menuBar_6 = new MenuBar(true);

		MenuItem mntmPrint = new MenuItem("Print", false, menuBar_6);

		MenuItem mntmPrintAllKeys = new MenuItem("Print all keys", false, new Command() {
			public void execute() {
				params("Print all keys", "", null, null, null);
				addToTextAreaResult( LdbApi.printAllKeys() );
			}
		});
		menuBar_6.addItem(mntmPrintAllKeys);
		menuBar.addItem(mntmPrint);
		MenuBar menuBar_7 = new MenuBar(true);

		MenuItem mntmSelect = new MenuItem("Select", false, menuBar_7);

		MenuItem mntmSelectAll = new MenuItem("Select All", false, new Command() {
			public void execute() {
				params("Select All", "", "objStrName", null, null);
			}
		});
		menuBar_7.addItem(mntmSelectAll);

		MenuItem mntmSelectAllcolumns = new MenuItem("Select All (columns)", false, new Command() {
			public void execute() {
				params("Select All (columns)", "Specify columnNames as 'col1','col2','col3'...", "columnNames", "objStrName", null);
			}
		});
		menuBar_7.addItem(mntmSelectAllcolumns);
		menuBar.addItem(mntmSelect);
		MenuBar menuBar_8 = new MenuBar(true);

		MenuItem mntmGet = new MenuItem("Get", false, menuBar_8);

		MenuItem mntmGetNumberOf = new MenuItem("Get number of records", false, new Command() {
			public void execute() {
				params("Get number of records", "", "objStrName", null, null);
			}
		});
		menuBar_8.addItem(mntmGetNumberOf);

		MenuItem mntmGetPkeysForValueInColumn = new MenuItem("Get Pkeys For Value In Column", false, new Command() {
			public void execute() {
				params("Get Pkeys For Value In Column", "", "objStrName", "columnName", "searchValue");
			}
		});
		menuBar_8.addItem(mntmGetPkeysForValueInColumn);
		
		MenuItem mntmGetPkeyFor = new MenuItem("Get Pkey for ith record", false, new Command() {
			public void execute() {
				params("Get Pkey for ith record", "", "objStrName", "i", null);
			}
		});
		menuBar_8.addItem(mntmGetPkeyFor);

		MenuItem mntmGetIthRecord = new MenuItem("Get ith record", false, new Command() {
			public void execute() {
				params("Get ith record", "", "objStrName", "i", null);
			}
		});
		menuBar_8.addItem(mntmGetIthRecord);
		
		MenuItem mntmGetRecordByPkey = new MenuItem("Get record by Pkey", false, new Command() {
			public void execute() {
				params("Get record by Pkey", "", "objStrName", "pkeyValue", null);
			}
		});
		menuBar_8.addItem(mntmGetRecordByPkey);

		MenuItem mntmGetIthRecord_1 = new MenuItem("Get ith record (columns)", false, new Command() {
			public void execute() {
				params("Get ith record (columns)", "Specify columnNames as 'col1', 'col2', 'col3' ...", "columnNames", "objStrName", "i");
			}
		});
		menuBar_8.addItem(mntmGetIthRecord_1);
		
		MenuItem mntmGetRecordColByPkey = new MenuItem("Get record (columns) by Pkey", false, new Command() {
			public void execute() {
				params("Get record (columns) by Pkey", "Specify columnNames as 'col1', 'col2', 'col3' ...", "columnNames", "objStrName", "pkeyValue");
			}
		});
		menuBar_8.addItem(mntmGetRecordColByPkey);
		
		menuBar.addItem(mntmGet);
		MenuBar menuBar_9 = new MenuBar(true);

		MenuItem mntmDelete = new MenuItem("Delete", false, menuBar_9);

		MenuItem mntmDeleteIthRecord = new MenuItem("Delete ith record", false, new Command() {
			public void execute() {
				params("Delete ith record", "", "objStrName", "i", null);
			}
		});
		menuBar_9.addItem(mntmDeleteIthRecord);

		MenuItem mntmDeleteRecordBy = new MenuItem("Delete record by Pkey", false, new Command() {
			public void execute() {
				params("Delete record by Pkey", "", "objStrName", "pkeyValue", null);
			}
		});
		menuBar_9.addItem(mntmDeleteRecordBy);

		MenuItem mntmDeleteAllRecords = new MenuItem("Delete all records", false, new Command() {
			public void execute() {
				params("Delete all records", "", "objStrName", null, null);
			}
		});
		menuBar_9.addItem(mntmDeleteAllRecords);

		MenuItem mntmDeleteObjectStore = new MenuItem("Delete object store", false, new Command() {
			public void execute() {
				params("Delete object store", "", "objStrName", null, null);
			}
		});
		menuBar_9.addItem(mntmDeleteObjectStore);
		menuBar.addItem(mntmDelete);
		MenuBar menuBar_10 = new MenuBar(true);

		MenuItem mntmDrop = new MenuItem("Drop", false, menuBar_10);

		MenuItem mntmDropDatabase = new MenuItem("Drop database", false, new Command() {
			public void execute() {
				params("Drop database", "", "dbName", null, null);
			}
		});
		menuBar_10.addItem(mntmDropDatabase);
		menuBar.addItem(mntmDrop);
		MenuBar menuBar_11 = new MenuBar(true);

		MenuItem mntmRemove = new MenuItem("Remove", false, menuBar_11);

		MenuItem mntmRemoveAllKeys = new MenuItem("Remove all keys", false, new Command() {
			public void execute() {
				params("Remove all keys", "", null, null, null);
				boolean confirm = Window.confirm("Are you sure you want to remove all keys from the local storage. This will completely wipe off all data" +
						" irreversibly.");
				if(confirm)
					LdbApi.removeAll();
			}
		});
		menuBar_11.addItem(mntmRemoveAllKeys);
		menuBar.addItem(mntmRemove);

		lblFunctionToBeExec = new Label("Function to be executed: ");
		verticalPanel.add(lblFunctionToBeExec);

		textAreaDescription = new TextArea();
		verticalPanel.add(textAreaDescription);
		textAreaDescription.setSize("672px", "61px");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setSize("550px", "25px");

		lblParam1 = new Label("Param 1:");
		horizontalPanel.add(lblParam1);
		lblParam1.setSize("200px", "25px");

		txtbxParam1 = new TextBox();
		horizontalPanel.add(txtbxParam1);
		txtbxParam1.setSize("300px", "20px");

		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_1);
		horizontalPanel_1.setSize("550px", "25px");

		lblParam2 = new Label("Param 2:");
		horizontalPanel_1.add(lblParam2);
		lblParam2.setSize("200px", "25px");

		txtbxParam2 = new TextBox();
		horizontalPanel_1.add(txtbxParam2);
		txtbxParam2.setSize("300px", "20px");

		HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_2);
		horizontalPanel_2.setSize("550px", "25px");

		lblParam3 = new Label("Param 3:");
		horizontalPanel_2.add(lblParam3);
		lblParam3.setSize("200px", "25px");

		txtbxParam3 = new TextBox();
		horizontalPanel_2.add(txtbxParam3);
		txtbxParam3.setSize("300px", "20px");

		HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_3);
		horizontalPanel_3.setSize("115px", "35px");

		Button btnExecute = new Button("Execute");
		btnExecute.addClickHandler( getExecuteButtonClickHandler() );
		horizontalPanel_3.add(btnExecute);

		Button btnReset = new Button("Reset");

		btnReset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				params("", "", "Param 1", "Param 2", "Param 3");
			}

		});
		horizontalPanel_3.add(btnReset);


		textAreaResult = new TextArea();
		verticalPanel.add(textAreaResult);
		textAreaResult.setSize("667px", "280px");
	}

	/**
	 * Method to specify the function to be executed and the list of parameters that needs to be filled
	 * @param fnToBeExecuted DB Function to be executed
	 * @param description Description/help on the function to be executed
	 * @param param1 Param 1
	 * @param param2 Param 2
	 * @param param3 Param 3
	 */
	private void params(String fnToBeExecuted, String description, String param1, String param2, String param3) {
		lblFunctionToBeExec.setText("Function to be executed: "+fnToBeExecuted);
		if(param1==null)
			txtbxParam1.setEnabled(false);
		else {
			txtbxParam1.setEnabled(true);
			lblParam1.setText(param1+":");
		}
		if(param2==null)
			txtbxParam2.setEnabled(false);
		else {
			txtbxParam2.setEnabled(true);
			lblParam2.setText(param2+":");
		}
		if(param3==null)
			txtbxParam3.setEnabled(false);
		else {
			txtbxParam3.setEnabled(true);
			lblParam3.setText(param3+":");
		}

		textAreaDescription.setText(description);

	}

	private void addToTextAreaResult(String text) {
		//textArea.setText( textArea.getText()+ text);
		textAreaResult.setText(text);
	}

	private ClickHandler getExecuteButtonClickHandler() {
		ClickHandler execute = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String param1 = txtbxParam1.isEnabled()? txtbxParam1.getValue():null;
				String param2 = txtbxParam2.isEnabled()? txtbxParam2.getValue():null;
				String param3 = txtbxParam3.isEnabled()? txtbxParam3.getValue():null;

				String fnToBeExecuted = lblFunctionToBeExec.getText().substring(25);

				try {
					if(fnToBeExecuted.equals("Use database")) {
						LdbApi.useDb(param1);
						addToTextAreaResult("Database in use: "+param1);
					} else if(fnToBeExecuted.equals("Create database")) {
						LdbApi.createDb(param1);
						addToTextAreaResult("New database '"+param1+"' created");
					} else if(fnToBeExecuted.equals("Create object store")) {
						LdbApi.createObjectStore(param1);
						addToTextAreaResult("New object store '"+param1+"' created");
					} else if(fnToBeExecuted.equals("Get column list")) {
						addToTextAreaResult("Column list:\n"+ LdbApi.printList(LdbApi.getColumnList(param1)) );
					} else if(fnToBeExecuted.equals("Get values from column")) {
						addToTextAreaResult("Values from column:\n"+ LdbApi.printList(LdbApi.getValuesFromColumn(param1, param2)) );
					} else if(fnToBeExecuted.equals("Add Column")) {
						String s[] = param2.split(",");
						if(s[0].trim().equals("true")) { //Column definition for primary key
							LdbApi.addColumn( param1, new Column(true, Boolean.parseBoolean(s[1].trim()) , s[2].trim().substring(1, s[2].trim().length()-1)) );
							addToTextAreaResult("Primary Key "+ s[2].trim()+" added");
						} else { //Column def for non-primary key
							String nameOfColumn = s[0].trim().substring(1, s[0].trim().length()-1) ;
							LdbApi.addColumn( param1, new Column(nameOfColumn, Boolean.parseBoolean(s[1].trim()), Boolean.parseBoolean(s[2].trim())) );
							addToTextAreaResult("Column "+ s[0].trim()+" added");
						}
					} else if(fnToBeExecuted.equals("Add Record") || fnToBeExecuted.equals("Add Record (all values)") ) {
						String s[] = param2.split(",");
						for(int i=0; i<s.length; i++) {
							s[i] = s[i].trim();
							if(s[i].equals("null"))
								s[i] = null;
							else
								s[i] = s[i].substring(1, s[i].length()-1);
						}

						if(fnToBeExecuted.equals("Add Record"))
							LdbApi.addRecord(param1, s);
						else if(fnToBeExecuted.equals("Add Record (all values)"))
							LdbApi.addRecordAllValues(param1, s);

						addToTextAreaResult("Record added successfully");
					} else if(fnToBeExecuted.equals("Update ith record") || fnToBeExecuted.equals("Update record by Pkey") ) {
						String s[] = param2.split(",");
						for(int i=0; i<s.length; i++) {
							s[i] = s[i].trim();
							s[i] = s[i].substring(1, s[i].length()-1);
						}

						if(fnToBeExecuted.equals("Update ith record"))
							LdbApi.updateIthRecord(param1, s, Integer.parseInt(param3));
						else if(fnToBeExecuted.equals("Update record by Pkey"))
							LdbApi.updateRecordByPkey(param1, s, param3);
						addToTextAreaResult("Record updated successfully");
					} else if(fnToBeExecuted.equals("Add or update record") || fnToBeExecuted.equals("Add or update record (All values)") ) {
						String s[] = param2.split(",");
						for(int i=0; i<s.length; i++) {
							s[i] = s[i].trim();
							s[i] = s[i].substring(1, s[i].length()-1);
						}
						
						if(fnToBeExecuted.equals("Add or update record"))
							LdbApi.addOrUpdateRecord(param1, s, param3);
						else if(fnToBeExecuted.equals("Add or update record (All values)"))
							LdbApi.addOrUpdateRecordAllValues(param1, s);
						addToTextAreaResult("Add/update successful");
					} else if(fnToBeExecuted.equals("Select All")) {
						addToTextAreaResult( "Records in the object store '"+param1+"'\n"+ LdbApi.printRecords(LdbApi.selectAll(param1)) );
					} else if(fnToBeExecuted.equals("Select All (columns)")) {
						String s[] = param1.split(",");
						for(int i=0; i<s.length; i++) {
							s[i] = s[i].trim();
							s[i] = s[i].substring(1, s[i].length()-1);
						}
						addToTextAreaResult( "Records in the object store '"+param2+"'\n"+ LdbApi.printRecords(LdbApi.selectAllColumn(s, param2)) );
					} else if(fnToBeExecuted.equals("Get number of records")) {
						addToTextAreaResult( "Number of records in the object store '"+param1+"': "+ LdbApi.getNumberOfRecords(param1) );
					} else if(fnToBeExecuted.equals("Get Pkeys For Value In Column")) {
						ArrayList<String> pkeys = LdbApi.getPkeysForValueInColumn(param1, param2, param3, false);
						if(pkeys.size()==0)
							addToTextAreaResult( "Search didn't return any matches" );
						else
							addToTextAreaResult( "Pkeys of records having the required search value:" + LdbApi.printList(pkeys) );
						
					} else if(fnToBeExecuted.equals("Get Pkey for ith record")) {
						int i = Integer.parseInt(param2);
						addToTextAreaResult( "Pkey for "+i+getSub(i)+" record: "+ LdbApi.getPkeyForIthRecord(param1, i) );
					} else if(fnToBeExecuted.equals("Get ith record")) {
						int i = Integer.parseInt(param2);
						addToTextAreaResult( "Value for Record no "+i+":\n"+LdbApi.printRecord(LdbApi.getIthRecord(param1, i)) );
					} else if(fnToBeExecuted.equals("Get record by Pkey")) {
						addToTextAreaResult( "Value for Record having Pkey '"+param2+"':\n"+LdbApi.printRecord(LdbApi.getRecordByPkey(param1, param2)) );
					} else if(fnToBeExecuted.equals("Get ith record (columns)")) {
						int j = Integer.parseInt(param3);
						String s[] = param1.split(",");
						for(int i=0; i<s.length; i++) {
							s[i] = s[i].trim();
							s[i] = s[i].substring(1, s[i].length()-1);
						}
						addToTextAreaResult( "Value for Record no "+j+":\n"+ LdbApi.printRecord(LdbApi.getIthRecordColumn(s, param2, j)) );
					} else if(fnToBeExecuted.equals("Get record (columns) by Pkey")) {
						String s[] = param1.split(",");
						for(int i=0; i<s.length; i++) {
							s[i] = s[i].trim();
							s[i] = s[i].substring(1, s[i].length()-1);
						}
						addToTextAreaResult( "Value for Record having Pkey '"+param3+"':\n"+ LdbApi.printRecord(LdbApi.getRecordColumnByPkey(s, param2, param3)) );
					} else if(fnToBeExecuted.equals("Delete ith record")) {
						int i = Integer.parseInt(param2);
						LdbApi.deleteIthRecord(param1, i);
						addToTextAreaResult( "Record no. "+i+" deleted" );
					} else if(fnToBeExecuted.equals("Delete record by Pkey")) {
						LdbApi.deleteRecordByPkey(param1, param2);
						addToTextAreaResult( "Record with Pkey '"+param2+"' deleted" );
					} else if(fnToBeExecuted.equals("Delete all records")) {
						boolean confirm = Window.confirm("Are you sure you want to delete all records for object store '"+param1+"'?");
						if(confirm)
							LdbApi.deleteAllRecords(param1);
						addToTextAreaResult( "All records successfully deleted" );
					} else if(fnToBeExecuted.equals("Delete object store")) {
						boolean confirm = Window.confirm("Are you sure you want to delete the object store '"+param1+"'?");
						if(confirm)
							LdbApi.deleteObjectStore(param1);
						addToTextAreaResult( "Object store '"+param1+"' successfully deleted" );
					} else if(fnToBeExecuted.equals("Drop database")) {
						boolean confirm = Window.confirm("Are you sure you want to drop the database '"+param1+"'?");
						if(confirm)
							LdbApi.dropDatabase(param1);
						addToTextAreaResult( "Database '"+param1+"' successfully dropped" );
					}

				} catch (LocalStorageDatabaseException e) {
					logger.log(Level.SEVERE, "Database Exception", e);
				}

			}
		};
		return execute;
	}

	
	
	private String getSub(int i) {
		String sub="th";
		if(i==1) sub = "st";
		else if(i==2) sub = "nd";
		else if(i==3) sub = "rd";
		return sub;
	}
}
