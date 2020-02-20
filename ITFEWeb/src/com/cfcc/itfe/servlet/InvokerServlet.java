package com.cfcc.itfe.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.MDC;

import com.cfcc.jaf.web.servlet.invoke.HttpInvokeServlet;

public class InvokerServlet extends HttpInvokeServlet {
	private static final long serialVersionUID = -2163010060642117361L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false); // ºÏ—Èsession «∑Ò¥Ê‘⁄
		if (session == null) {
			MDC.put("SessionID", "NO Session");
		} else {
			MDC.put("SessionID", session.getId());
		}

		MDC.put("App", "HttpInvoker");

		super.doPost(request, response);

		MDC.put("App", "");
		MDC.put("SessionID", "");

	}

}
