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

/**
 * Class to define a column inside an object store
 * @author Raghu Mittal
 *
 */
public class Column {
	
		private boolean pkey;
		private boolean autogenerate;
		private String nameOfColumn;
		private boolean unique;
		private boolean notNull;
		
		//constructor to call for making the column as primary key
		/**
		 * Constructor to define column that is a primary key
		 * @param pkey Should be true to indicate that this is a primary key column, else exception will be thrown
		 * @param autogenerate boolean to indicate whether the primary key is autogenerated or not
		 * @param nameOfColumn Name of the column
		 * @throws LocalStorageDatabaseException
		 */
		public Column(boolean pkey, boolean autogenerate, String nameOfColumn) throws LocalStorageDatabaseException {
			if(pkey==false)
				throw new LocalStorageDatabaseException("This constructor is to be called for pkey=true");
			this.pkey = true;
			this.autogenerate = autogenerate;
			this.nameOfColumn = nameOfColumn;
			this.unique = true;
			this.notNull = true;
		}
		
		/**
		 * Constructor for the non-primary key, ordinary column
		 * @param nameOfColumn Name of the column to be added
		 * @param unique boolean to indicate whether the column is unique
		 * @param notNull boolean to indicate whether the column is notNull
		 */
		public Column(String nameOfColumn, boolean unique, boolean notNull) {
			this.pkey = false;
			this.autogenerate = false;
			this.nameOfColumn = nameOfColumn;
			this.unique = unique;
			this.notNull = notNull;
		}

		public boolean isPkey() {
			return pkey;
		}

		public void setPkey(boolean pkey) {
			this.pkey = pkey;
		}

		public boolean isAutogenerate() {
			return autogenerate;
		}

		public void setAutogenerate(boolean autogenerate) {
			this.autogenerate = autogenerate;
		}

		public String getNameOfColumn() {
			return nameOfColumn;
		}

		public void setNameOfColumn(String nameOfColumn) {
			this.nameOfColumn = nameOfColumn;
		}

		public boolean isUnique() {
			return unique;
		}

		public void setUnique(boolean unique) {
			this.unique = unique;
		}

		public boolean isNotNull() {
			return notNull;
		}

		public void setNotNull(boolean notNull) {
			this.notNull = notNull;
		}
		
}
