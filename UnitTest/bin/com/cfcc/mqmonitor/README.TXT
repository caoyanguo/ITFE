

MQSeries PCF classes examples
-----------------------------

This directory contains a set of example programs that demonstrate the use 
of the MS0B PCF classes for common tasks. 

Getting started examples: 

  PCFMessageListQueueNames ......... Displays the list of queues on the 
                                     target queue manager
  PCFMessageListQueueDepth ......... Displays the current queue depth for 
                                     each queue
  PCFMessageListQManagerAttrs ...... Displays the queue manager settings
  
More specialized examples: 

  PCFMessageListQueueDepthNoAgent .. Lists queue depths, using PCFMessage 
                                     but leaving the application to manage 
                                     the interaction with the queue manager
  ListActiveQueues ................. Uses PCF filters to list queues that 
                                     have non-zero queue depth
  ReadPCFMessages .................. Reads and displays PCF messages 
                                     including accounting, statistics and 
                                     activity report messages
  TraceRoute ....................... Generates a trace-route message and 
                                     displays the response
  
These four examples show lower-level mechanisms to create PCF requests 
(most applications will not need to use the approaches illustrated here): 

  ListQManagerAttrs ......... performs an InquireQueueManager command
  ListQueueNames ............ performs an InquireQueueNames command
  ListQueueDepth ............ performs an InquireQueue command
  ListQueueDepthNoAgent ..... performs an InquireQueue command


Java requirements
-----------------

  - Java 1.4 or later runtime environment (JRE/JDK)
  - MQSeries v5.1 o later Java classes


MQSeries requirements
---------------------

  - a PCF-capable queue manager either locally installed, or on a machine 
    accessible over TCP/IP from the machine where the Java environment is 
    installed (for MQSeries Client connections)
  - the queue manager and its command server must be active
  - the PCF command queue as defined by the COMMANDQ attribute of the 
    queue manager must be defined and accessible (generally this is 
    SYSTEM.ADMIN.COMMAND.QUEUE)
  - for MQSeries Client connections, a client channel (type SVRCONN) must be 
    defined on the queue manager, and an inetd entry configured or listener 
    started to accept connections to the queue manager


Step-by-step instructions for running the examples
--------------------------------------------------

1. Ensure that the CLASSPATH environment variable on the Java machine 
   includes a reference to the jar file com.ibm.mq.pcf.jar containing the 
   com.ibm.mq.pcf classes, as well as to the examples directory containing 
   the PCF examples classes

     e.g. on AIX, if the jar file is in /usr/local/pcf and the examples 
       directory is the current directory:

     export CLASSPATH=$CLASSPATH:/usr/local/pcf/com.ibm.mq.pcf.jar:.

   (The classpath will also need to include the base MQSeries Java 
   classes in com.ibm.mq.jar, as with running any MQSeries Java 
   application.)

2. Start the queue manager (if not already running)

     strmqm <queue_manager>

3. Start the PCF command server (if not already running)

     strmqcsv <queue_manager>

Steps 4 and 5 are only necessary for MQSeries Client connections: 

4. (PC platforms only) Start the listener, if not already running

     in runmqsc: start listener

5. Define a client channel (if not already defined)

     in runmqsc: define channel (<channel>) chltype (svrconn) trptype (tcp)

6. Run one of the example programs: 

     java PCFMessageListQueueDepth <queue-manager> | <hostname/IP address> <port> <channel>
     java PCFMessageListQueueNames <queue-manager> | <hostname/IP address> <port> <channel>
     java PCFMessageListQManagerAttrs <queue-manager> | <hostname/IP address> <port> <channel>
     java PCFMessageListQueueDepthNoAgent <queue-manager> | <hostname/IP address> <port> <channel>

     java ReadPCFMessages <queue-manager> <queue-name>
     java ListActiveQueues <queue-manager> | <hostname/IP address> <port> <channel>
     java TraceRoute <queue-manager> <target-queue> <target-queue-manager> <reply-queue>

     java ListQueueDepth <queue-manager> | <hostname/IP address> <port> <channel>
     java ListQueueNames <queue-manager> | <hostname/IP address> <port> <channel>
     java ListQManagerAttrs <queue-manager> | <hostname/IP address> <port> <channel>
     java ListQueueDepthNoAgent <queue-manager> | <hostname/IP address> <port> <channel>

Chris Markes   November 27, 2008 (updated for MS0B version 6.1)

cmarkes@uk.ibm.com