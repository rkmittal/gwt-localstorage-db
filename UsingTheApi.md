# A short tutorial on using the API #

The API basically consists of various methods that can be executed to retrieve or modify data in the object stores.

The methods need to be written inside a try-catch, or with throws parameter to the method, since most of the methods throw the LocalStorageDatabaseException.

```
try {
        //Your localstorage methods go here
} catch (LocalStorageDatabaseException e) {
        e.printStackTrace();
        //catch the exception and display on browser etc.
}
```

## Tutorial ##

  * Creates a database called ecollect
```
LdbApi.createDb("ecollect");
```

  * Specifies the database to be used. Very important, since all further queries and operations will be done on this database itself.
```
LdbApi.useDb("ecollect");
```

  * Creates an object store called study in the current database
```
LdbApi.createObjectStore("study");
```

  * Adds columns to the object store 'study', the first one always has to be the primary key. Refer to Java docs to find out how to specify Column type
```
LdbApi.addColumn("study", new Column(true, false, "studyId"));
LdbApi.addColumn("study", new Column("name", true, true));
LdbApi.addColumn("study", new Column("description", false, false));
```

  * Add records specifying select column names and values to be put in (Note: the description field, being not required field can be skipped, also, the IDs, even though numbers, have to be specified as Strings)
```
LdbApi.addRecord("study", new String[]{"studyId","1", "name", "WHO study1"});
```

  * Add record, specifying all values for each column in that order
```
LdbApi.addRecordAllValues("study", new String[]{"2", "WHO study2", "This study relates to heart disease"});
LdbApi.addRecordAllValues("study", new String[]{"3", "WHO study3", null});
```

  * Get all records as a List of String array. Equivalent to the SQL statement - "select `*` from study"
```
List<String[]> listOfAllRecords = LdbApi.selectAll("study");
```

  * Updates the 3rd record
```
LdbApi.updateIthRecord("study",new String[]{"description","Description3"},3);
```

  * Deletes the 1st record for object store 'study'
```
LdbApi.deleteIthRecord("study", 1);
```

  * Deletes all records inside the object store 'study'
```
LdbApi.deleteAllRecords("study");
```

  * Deletes the object store 'study' and all its data
```
LdbApi.deleteObjectStore("study");
```

  * Drops the database called 'ecollect' and deletes all data, object stores inside in one go.
```
LdbApi.dropDatabase("ecollect");
```


For help on searching on the database, goto SearchingOnDatabase