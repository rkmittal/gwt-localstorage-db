# Getting started #

The steps to using the API are:
  * Download the latest release of gwt-localstorage-api.jar file
  * Also download the latest java-docs, since they will be important in understanding how to use the API.
  * Add the jar file to your build path in Eclipse/put it in your pom.xml as an artifact (The API needs atleast GWT v2.3.0 )
  * Add the following inherits line in your GWT module xml file.
```
<inherits name='com.handsrel.localstorage.Localstorage' />
```
  * Include this code, which will initialize the local storage, so that you can start using the API.

```
try {
	Storage storage = Storage.getLocalStorageIfSupported();
	if (storage==null)
		throw new LocalStorageDatabaseException("Local storage not supported in the browser");
	else {
		LdbApi.initialize(storage);
	}
} catch (LocalStorageDatabaseException e) {
	e.printStackTrace();
	//catch the exception and display on browser etc.
}
```

  * LdbApi is the main class that contains all the methods that a developer will use in his code. Refer to java docs to find out what are the methods you can execute. Don't forget to put a try-catch, where you will be catching exceptions of type LocalStorageDatabaseException, same as is shown in the above code snippet.

  * There is a query browser that comes with the API which can also be put in your own application. The query browser is defined as a widget, which you can easily add to your application like this:

```
LdbQueryBrowser lqb = new LdbQueryBrowser();
rootPanel.add(lqb); // or add it to any other place
```

Move to the LdbQueryBrowser wiki page for help related to Query browser.

## Applications of this library ##

This library has been actively used in one of our applications called mCollect for android. We also did a study where 1.1 million records were collected using the same app. Please read here to find out more:
[Census of Minor Irrigation schemes](http://handsrel.com/index.php/case-studies/census-of-minor-irrigation-schemes)

There are a lot of tests written within the library, which ensure that data is not lost/corrupted during any of the read/write operations. You can have a look at the tests and also run the tests to independently see that the storage api works without any issues.

Even though the library has been used for a long-time without any data loss or issues on data integrity, and it also has tests to ensure that it works correctly, you should read the following warning carefully.

## Statutory Warning ##

The API deals with storage of data on the client-side, and in many cases, the data may be valuable for the user of the application. Any data-loss resulting from the use of this API does not hold me responsible (Read the license text). So you are advised to thoroughly test the API before releasing your application to the public.