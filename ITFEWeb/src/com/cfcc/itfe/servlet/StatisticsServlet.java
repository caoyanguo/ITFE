package com.cfcc.itfe.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cfcc.jaf.web.servlet.statistics.ServiceStatisticsServlet;

public class StatisticsServlet extends ServiceStatisticsServlet

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2763010060642117361L;

	// Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		super.doGet(request, response);
	}

}
