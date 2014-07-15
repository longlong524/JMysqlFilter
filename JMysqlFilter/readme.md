#Welcome to JmysqlFilter

##JMysqlFilter's Goal

The mysql replication agent between master and slave, can filter any binary log event that you don't want. The more you 

want to filter, the faster Jmysqlfilter perform.

##Advantages

It is designed fast and small.When Jmysqlfilter is running it will consume about 50MB memory.

Most importantly,it almostly has the same speed with mysql original replication. Below is the test result.

##Some Test Results

Binary log 23.7MB, contains 191880 Rows data. Two Mysql in 10.183.6.32(1) and 192.168.2.53(2). The mysql 2 will connect 

the mysql 1. After test network bandwidth is about 630KB/S

###Mysql replication

use about 55 seconds, and about 441KB/S, achives 70% of bandtidth

###JMysqlFilter

After 10 times tests, it use about 52 seconds evenly, about 466KB/S. It is faster than mysql replication. Because i do 

some work at performance, so the situation can be accepted.
