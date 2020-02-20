package com.cfcc.mqmonitor;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.pcf.CMQC;
import com.ibm.mq.pcf.PCFConstants;
import com.ibm.mq.pcf.activity.ActivityMessage;
import com.ibm.mq.pcf.activity.TraceRouteData;

/**
 * Example showing the generation of a trace-route message and processing of the response. This class 
 * constructs a trace-route message and sends it to a remote queue, then waits for the response on the 
 * specified reply queue.
 * <p>
 * Note: The target queue manager must have its <tt>ROUTEREC</tt> attribute set to <tt>QUEUE</tt> in 
 * order for the trace-route reply message to be returned to the reply queue specified. 
 * 
 * @author Chris Markes
 */
public class TraceRoute
{
	public static void main (String [] args) throws Exception
	{
		// Turn off unnecessary output before we start

		MQEnvironment.disableTracing ();
		MQException.log = null;

		MQQueueManager qmanager = null;

		try
		{
			// Connect to the specified queue manager and open queues

			System.out.print ("Connecting to local queue manager " + args [0] + "... ");
			qmanager = new MQQueueManager (args [0]);
			System.out.println ("Connected.");
			
			// Create the trace-route message ready for sending.
			
			MQMessage message = TraceRouteData.createTraceRouteMessage (10, true, true, true, true, CMQC.MQFMT_STRING);

			message.writeString ("This is a trace-route message.");
			message.report = CMQC.MQRO_PASS_CORREL_ID;
			
			// Open target and report queues and create the trace-route message ready for sending

			MQQueue targetQueue = qmanager.accessQueue (args [1], CMQC.MQOO_OUTPUT, args [2], null, null);
			MQQueue replyQueue = qmanager.accessQueue (args [3], CMQC.MQOO_INPUT_AS_Q_DEF);
			message.replyToQueueName = args [3];
			message.replyToQueueManagerName = qmanager.name;

			System.out.print ("Putting message to target queue " + targetQueue.name.trim () + "... ");
			targetQueue.put (message);
			System.out.println ("Message sent.");
			
			// Get the trace-route reply message.
			
			MQGetMessageOptions gmo = new MQGetMessageOptions ();
			
			gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_CONVERT;
			gmo.waitInterval = 1000;			
			
			System.out.print ("Retrieving trace route report... ");
			message.messageId = null;
			replyQueue.get (message, gmo);
			System.out.println ("Done.");

			// Use the ActivityMessage class to parse the response. 
			
			System.out.println ("Trace-route response: " + new ActivityMessage (message));
		}
		
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println ("Usage: java " + TraceRoute.class.getName () + 
				" qmgr-name target-queue-name target-qmgr-name reply-queue-name");
		}
		
		catch (MQException e)
		{
			System.out.println ("\n" + e + " (" + PCFConstants.lookupReasonCode (e.reasonCode) + ")");
			
			throw e;
		}
		
		finally
		{
			try
			{
				if (qmanager != null) qmanager.disconnect ();
			}
			
			catch (Exception e)
			{
				
			}
		}
	}
}
