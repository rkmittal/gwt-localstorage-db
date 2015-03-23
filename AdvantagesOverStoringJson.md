# Advantages over Json Objects #

When you want to store your data on the client-side, there is an option that you can store data in the form of Json objects in the localstorage and later parse it to retrieve back the result.

Suppose you have an employee object store. For the sake of simplicity, we can assume that it contains 3 fields - Employee name, Employee code and Age.
Now we could simply store all our employees into the localstorage in the form of Json objects where each employee could be stored in a different key-value pair.
```
{ "EmpName":"John Doe", "EmpCode":"123", "Age":"29" }
```

If your application has simple read/write requirement, this scheme could be sufficient. But what if you were to search on this database, say to find out all employees with age greater than 25?

Searching would mean that you parse all Json objects, pick out all the ages from each employee and then make a decision as to which are the employees that satisfy your criteria. This process will be very slow. Consider that you have a much larger object containing many more fields. Or if your database has thousands of employees. In such cases, Json parsing is hardly the answer to an efficient and speedy storage.

In contrast, gwt-localstorage-db stores all the fields separately, in different key-value pairs. So, if you were to search on "Age", it indexes all the age fields automatically, so all the Age fields will automatically be picked up, converted from String to Integer (this step is needed since localstorage only stores strings and no other data-type), and then you can easily pick up the whole Employee object once it satisfies your criteria for age. What this means is that because of the inherent indexing, gwt-localstorage-db api is much faster than storing custom json objects.

So, most importantly, before you decide on a particular api that you want to use, you should see if the api stores data in json form or through indexes, since this determines how fast you can search on the database.