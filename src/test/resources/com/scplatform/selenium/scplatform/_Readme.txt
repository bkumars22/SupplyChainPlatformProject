This file can be deleted

MANUAL FIX: Rename src/test/resources/com/scplatform/selenium/newProduct --> src/test/resources/com/scplatform/selenium/<projectName>

All non-java resources for the API level will go here.  
Suggest that the files are in the same package names as the Test class that is calling them.  
Then, access using:

File resourceFile = FileHelper.getResourceFile(getClass(), "myResourceFile.txt");
