# Settlement

Application will  load financial Transaction records from CSV file and calculate Relative Account balance of a Account at a gven point of Time.

Technologies Used:

Spring Boot, JPA, Restful Webservices, Jackson Data format CSV, Derby .

Csv File is read using Jackson data format csv library.



In order to load data from CSV to DB, Hit end point: 

localhost:8080/api/v1/settlement

In order to get relative Account balance:

localhost:8080/api/v1/settlement/transactions

{
  "accountId" :"ACC334455",
   "fromDate" : "2018-47-20 12:47:55",
    "toDate": "2018-30-21 09:30:00"
}


Steps to run the code:

Maven  clean and build. 
The jar file will be available in the target file.
Copy jar and using cmd  type "java -jar Settlement-0.0.1-SNAPSHOT.jar"

