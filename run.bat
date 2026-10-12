@echo off
title CampusCare - College Complaint Management System
echo =====================================================================
echo  CampusCare - College Complaint Management System
echo  Starting Embedded Apache Tomcat 9 Server...
echo =====================================================================
echo  Prerequisites: MySQL running with database 'campuscare_db'
echo  Local URL: http://localhost:8080/CampusCare/
echo =====================================================================
mvn compile exec:java
pause
