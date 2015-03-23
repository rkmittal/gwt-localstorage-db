Having problems on determining what database api to use for your webapp? Indexed DB is still in development mode, and not available for Android/iPhone browsers. Web Database is deprecated by W3C, with support discontinued by major browsers like Firefox.

I was also faced with the same dilemma some time back. We tried out IndexedDb to work with our GWT webapp on Firefox 4.0, but it didn't work out well, especially because it led to problematic JSNI code, and of course, there was no way our app could run on mobile phones. So, I devised a simple API to work as a wrapper on plain vanilla Localstorage API (supported in all HTML5 browsers) which will allow the user the benefit of putting their data in structured formats.

**So, in very brief terms, this API can be used for client-side storage in form of object stores and databases. And it runs across all browsers that have basic local storage API.**

Here's how its done:

1. In your GWT webapp, just put the gwt-localstorage-db jar file.

2. Using the localstorage api, you can do things the same way you do in a database, i.e. select records, add records, delete records, drop tables, drop databases etc.

Effectively, the data is being stored in key-value pairs in the Localstorage, but the localstorage API creates the keys in such a way that it can run queries and make the user feel that he has a full-fledged database.

GettingStarted - Get started with Localstorage Api

UsingTheApi - A short tutorial on using the Api

**Users group:** All users of gwt-localstorage-db may join the following group:

http://groups.google.com/group/gwt-localstorage-db-users

Please post all queries related to this api in this group, since I may not look at posted comments on the various wiki pages.

**Important Update:** Since security of data within the db has become really important requirement, I have started active work on encryption of the localstorage db. Soon, developers will have the option to encrypt their app data. AES-128 is the encryption algorithm that will be used.