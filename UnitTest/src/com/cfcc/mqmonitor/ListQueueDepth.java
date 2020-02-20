package com.cfcc.mqmonitor;

import java.io.*;
import com.ibm.mq.*;
import com.ibm.mq.pcf.*;

/**
 * PCF example class showing use of PCFAgent and com.ibm.mq.pcf structure types 
 * to generate and parse a PCF query.
 * 
 * @author Chris Markes
 */

public class ListQueueDepth
{
	final public static String copyright = "Copyright (c) IBM Corp. 1998, 2000   All rights reserved.";

	public static void main (String [] args)
	{
		PCFAgent 		agent;
		int [] 			attrs = 
					{
						CMQC.MQCA_Q_NAME, 
						CMQC.MQIA_CURRENT_Q_DEPTH
					};
		PCFParameter [] 	parameters = 
					{
						new MQCFST (CMQC.MQCA_Q_NAME, "*"), 
						new MQCFIN (CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL), 
						new MQCFIL (CMQCFC.MQIACF_Q_ATTRS, attrs)
					};
		MQMessage [] 		responses;
		MQCFH 			cfh;
		PCFParameter 		p;
		String 			name = null;
		Integer 		depth = null;

		try
		{
			// Connect a PCFAgent to the specified queue manager

			if (args.length == 1)
			{
				System.out.print ("Connecting to local queue manager " + 
					args [0] + "... ");
				agent = new PCFAgent (args [0]);
			}
			else
			{
				System.out.print ("Connecting to queue manager at " + 
					args [0] + ":" + args [1] + " over channel " + args [2] + "... ");
				agent = new PCFAgent (args [0], Integer.parseInt (args [1]), args [2]);
			}

			System.out.println ("Connected.");

			// Use the agent to send the request

			System.out.print ("Sending PCF request... ");
			responses = agent.send (CMQCFC.MQCMD_INQUIRE_Q, parameters);
			System.out.println ("Received reply.");

			for (int i = 0; i < responses.length; i++)
			{
				cfh = new MQCFH (responses [i]);

				// Check the PCF header (MQCFH) in the response message

				if (cfh.reason == 0)
				{
					for (int j = 0; j < cfh.parameterCount; j++)
					{
						// Extract what we want from the returned attributes

						p = PCFParameter.nextParameter (responses [i]);

						switch (p.getParameter ())
						{
						case CMQC.MQCA_Q_NAME:
							name = (String) p.getValue ();
							break;
						case CMQC.MQIA_CURRENT_Q_DEPTH:
							depth = (Integer) p.getValue ();
							break;
						default:
						}
					}

					System.out.println ("Queue " + name + " curdepth " + depth);
				}
				else
				{
					System.out.println ("PCF error:\n" + cfh);

					// Walk through the returned parameters describing the error

					for (int j = 0; j < cfh.parameterCount; j++)
					{
						System.out.println (PCFParameter.nextParameter (responses [0]));
					}
				}
			}

			// Disconnect

			System.out.print ("Disconnecting... ");
			agent.disconnect ();
			System.out.println ("Done.");
		}

		catch (ArrayIndexOutOfBoundsException abe)
		{
			System.out.println ("Usage: \n" + 
				"\tjava ListQueueDepth queue-manager\n" + 
				"\tjava ListQueueDepth host port channel");
		}

		catch (NumberFormatException nfe)
		{
			System.out.println ("Invalid port: " + args [1]);
			System.out.println ("Usage: \n" + 
				"\tjava ListQueueDepth queue-manager\n" + 
				"\tjava ListQueueDepth host port channel");
		}

		catch (MQException mqe)
		{
			System.err.println (mqe);
		}

		catch (IOException ioe)
		{
			System.err.println (ioe);
		}
	}
}