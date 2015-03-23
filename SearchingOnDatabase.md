# Searching on the database #

Searching is a very important part of a RDBMS. Any database, without the searching feature will be difficult to use.

An elementary search feature is available with this Api, that can allow searching on one variable.

```
getPkeysForValueInColumn(String objStrName, String columnName, String searchValue, boolean first)
```

The method here returns all record primary keys, where the columnName has the specified searchValue

The boolean value first is used to specify if you want to continue the search if an item is found. So if you specify it as **true**, then the search stops and only one key is returned, **false** will get all the matches.

**Note:** You don't need to search on primary keys, since knowing the primary key, you can directly access the record

Once you get the list of primary keys, you can use the method
`getRecordByPkey` to get each record and do further processing or perhaps further searching. Eg.
```
ArrayList<String> pkeys =  LdbApi.getPkeysForValueInColumn("Employee", "Department", "Operations", false);
for(int i=0; i<pkeys.size(); i++) {
     String[] record = LdbApi.getRecordByPkey("Employee",pkeys.get(i));
     //Do further processing on the retrieved record, or search on the already obtained results
}
```

A limitation here is, since all keys and data are treated as Strings, you can't search on numbers like age>5. If you want to search like that, then you have to get all records from the store, convert all age data from String to int, then do your check.

**Note:** Complex operations like Joins can also be done, without too much difficulty. If requested, I will do a short tutorial on Joins as well