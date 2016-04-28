Android SDK 21
Java JDK 1.8.40


Database
-----------------------------------------------
Stored in the assets folder
employee_mobile.db

sqlplus

When initializing the application

Check if the database file exists "/data/data/"+packageName+"/databases/DBName".
If doesnt exists, copy the db file from the assests folder to "/data/data/com.example.multiframe/databases/"

Sync Server
-----------------------------------------------

Images were synced from
http://insight.library.cornell.edu/mrsid/mrsid_images/RMC/Size4/RMC0036/
Doesnt exists anymore

Currently it reads from the assets/json_data/weekly_report.json.
The code needs to be modified to read the file downloaded from the server.
The server sync task will enable downloading the file from the server.

Employee attendance data is synced from https://api.myjson.com/bins/4q7am

{
  "creation_date":"2016-04-21 08:50:26.000",
  "monthlyreports":[
  {
    "empid":"10874030",
    "name":"Sajive Kumar",
    "sex":"male",
    "designation":"CTO",
    "department":"Consulting",
    "attendancemonthly":[
    {
      "monthno":"1",
      "days":[
      {
        "day":"1",
        "datetime":"2015-10-05 08:50:26",
        "intime":"2015-10-05 08:50:26",
        "outtime":"2015-10-05 17:50:26",
        "hours":"8.5"
      },
      {"day":"2", "datetime":"2015-10-06 10:50:26", "intime":"2015-10-06 08:50:26","outtime":"2015-10-06 17:50:26","hours":"8.5"},
      {"day":"3", "datetime":"2015-10-07 10:50:26", "intime":"2015-10-07 08:50:26","outtime":"2015-10-07 17:50:26","hours":"8.5"},
      {"day":"4", "datetime":"2015-10-08 10:50:26", "intime":"2015-10-08 08:50:26","outtime":"2015-10-08 17:50:26","hours":"8.5"},
      {"day":"5", "datetime":"2015-10-09 10:50:26", "intime":"2015-10-09 08:50:26","outtime":"2015-10-09 17:50:26","hours":"8.5"},
      {"day":"6", "datetime":"2015-09-25 10:50:26", "intime":"2015-09-25 08:50:26","outtime":"2015-09-25 17:50:26","hours":"8.5"}
      ]
    }
    ]
  }
  ]
}



EmployeeInfo class needs [assets] empinfo/<empid>.json file to exist.

the json contains the following subsections.

Personal : This contains information liken name, dob, sex
Employment : contains employment details like empid, designation, status, startdate, supervisor & work location
Contact : Mobile, email, current address, permanent address.
Training : title, description, status, start date

These json files must be pulled when the user does a get from server sync.



