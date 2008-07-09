/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.testsuite;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author <A HREF="mailto:jean.deruelle@gmail.com">Jean Deruelle</A> 
 *
 */
public class NotifierSipServlet extends SipServlet {

	private static Log logger = LogFactory.getLog(NotifierSipServlet.class);
	
	
	/** Creates a new instance of SimpleProxyServlet */
	public NotifierSipServlet() {
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		logger.info("the notifier sip servlet has been started");
		super.init(servletConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doSubscribe(SipServletRequest request) throws ServletException,
			IOException {

		logger.info("Got Subscribe: "
				+ request.getMethod());
		SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_OK);
		sipServletResponse.addHeader("Expires", request.getHeader("Expires"));
		sipServletResponse.addHeader("Event", request.getHeader("Event"));
		sipServletResponse.send();		
		// send notify		
		SipServletRequest notifyRequest = request.getSession().createRequest("NOTIFY");
		if(request.isInitial()) {
			notifyRequest.addHeader("Subscription-State", "pending");
			notifyRequest.addHeader("Event", "reg");
			notifyRequest.send();
			notifyRequest = request.getSession().createRequest("NOTIFY");
		}		
		if(request.getHeader("Expires").trim().equals("0")) {
			notifyRequest.addHeader("Subscription-State", "terminated");
		} else {			
			notifyRequest.addHeader("Subscription-State", "active");
		}
		notifyRequest.addHeader("Event", "reg");
		notifyRequest.send();
	}

}