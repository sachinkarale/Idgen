IDGen
testing for pipe line
Overview:
---------
IDGen, provide automatically generated IDs based on a selector which is supplied by the caller using rest url, and stores the data into files.

Data storage for the IDGen takes place in text files,
1) IDGen_Tracker.txt : Stores the selector value and its generated ID.
2) IDGen_Selector.txt : Stores the caller and selector values if the tracker file does not have its record.
3) IDGen_Log.txt : Stores the Log details.

Requirement:
------------
1) getID:
For every rest url request, increment the selector's id and returns it in json format. 

2) ListIDSelector: 
ListIDSelector will return all callers and selectors from the “IDGen_Selector.txt” file, and return it in json format.

Implementation:
---------------
Controller class has two restful request. 

1) getID:
For every rest url request, increment the selector's id and returns it in json format. 

The rest url will contain caller and selector, for every url request the selector values is been checked with the “IDGen_Tracker.txt” file selector value, and if the file contains the selector value the id of selector in “IDGen_Tracker.txt” file will increment by 1.

If the selector value is not in “IDGen_Tracker.txt” file, then the selector adds the caller value and selector value to the “IDGen_Selector.txt” file. And add selector value with id 1 in “IDGen_Tracker.txt” file.

“IDGen_Log.txt” file will save the time date and the values of caller and selector with the id.

2) ListIDSelector : 
ListIDSelector will return all callers and selectors from the “IDGen_Selector.txt” file, and return it in json format.

When the request comes for ListIDSelector,  the “IDGen_Selector.txt” file is read, and return the data as key and value pair of selector value and its caller value in json format. 

Test Request and Response:
--------------------------
Success URL:
------------
1) To save the selector to files, and generate the id we have the following url.

url:
----
http://localhost:8080/getID/{CALLER}?selector={SELECTOR}

example : 
Request: http://localhost:8080/getID/image?selector=IMG
Response will be : {"id":"IMG0000000001"}

Note: The application name can have 3 special characters (:,-,_), rest all special characters gives us the error message.
-----

Note: Numbers are allowed with the selector value and selector values are case insensitive.
-----
2) To get the listIDSelector we have the following url .

Request: http://localhost:8080/ListIDSelectors
Response will be : {"IMG":"image","IMG:01":"image","IMG:02":"image","IMG:03":"image"}

Failure URL:
------------
1) Error message for Special character.

Request: http://localhost:8080/getID/image?selector=IMG!
Response will be : ErrorCode=400, message=request has bad syntax, Unsupportable special character !

Request: http://localhost:8080/getID/image?selector=IMG@
Response will be : ErrorCode=400, message=request has bad syntax, Unsupportable special character @

2) If the File path is missing.

I) If the path of “IDGen_Tracker.txt” is missing the error response will be.

Request:
--------
When request for “getID” comes and if the path of “IDGen_Tracker.txt” is missing or there is no file in the mentioned path then,

Response:
---------
ErrorCode=506, message=Unable to read to file with name : TrackerFile.


II) If the path of “IDGen_Selector.txt” is missing the error response will be.

Request:
--------
When request for “getID” comes and if the path of “IDGen_Selector.txt” is missing or there is no file in the mentioned path then,

Response:
---------
ErrorCode=507, message=Unable to Write to file with name : SelectorFile.

Request:
-------------
When request for “ListIDSelector” comes and if the path of “IDGen_Selector.txt” is missing or there is no file in the mentioned path then,

Response:
---------
ErrorCode=506, message=Unable to Read to file with name : SelectorFile.


III) If the path of “IDGen_Log.txt” is missing the error response will be.

Request:
--------
When request for “getID” comes and if the path of “IDGen_Log.txt” is missing or there is no file in the mentioned path then,


Response:
---------
ErrorCode=507, message=Unable to write to file with name : LogFile

3) If ID Reaches maximum i.e[9999999999L].

Request:
--------
When request for “getID” comes and the selector value reaches it maximum number then,

Response:
---------
ErrorCode=222, message=ID reached maximum value 9999999999.
