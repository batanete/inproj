# inproj
My Business Intelligence project, from 2017. It is comprised of two main parts(the implementation of a data warehouse, and the implementing of various machine learning models on the data present on it).

Dataset chosen: Adverse drug events from openfda.org.

The data warehouse implemented aimed to answer various questions that interest both final users and clinics, about the adverse effects of several drug types on users of different age, sex and weight. For this purpose, we first performed ETL on the data present in the website in order to obtain the necessary information and saved in the warehouse(a process that was eventually automated using cron jobs), and then implemented an OLAP dashboard using Java Swing, in order to allow the user easy access to the information he wanted to obtain.

In the data mining portion of project, we used Weka to answer two classification problems and one forecasting one on the dataset.

(Much) more info can be found on the final report, including a general description of the whole project and choices made by the group regarding technology choices and more subtle details such as index choices on the DW.

Technologies used:
Python  
JSON  
SQLAlchemy  

Java  
Java Swing  
MySQL  

Weka  
