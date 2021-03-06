/**
 * $Id: SaveServlet.java,v 1.2 2012-03-22 09:16:11 gaudenz Exp $
 * Copyright (c) 2011-2012, JGraph Ltd
 */
package com.mxgraph.examples.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SaveServlet.
 * 
 * The SaveDialog in Dialogs.js implements the user interface. Editor.saveFile
 * in Editor.js implements the request to the server. Note that this request
 * is carried out in a separate iframe in order to allow for the response to
 * be handled by the browser. (This is required in order to bring up a native
 * Save dialog and save the file to the local filestyem.) Finally, the code in
 * this servlet echoes the XML and sends it back to the client with the
 * required headers (see Content-Disposition in RFC 2183).
 */
public class SaveServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5308353652899057537L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE)
		{
			String filename = request.getParameter("filename");
			String xml = request.getParameter("xml");

			if (xml != null && xml.length() > 0)
			{
				String format = request.getParameter("format");

				if (format == null)
				{
					format = "xml";
				}

				if (!filename.toLowerCase().endsWith("." + format))
				{
					filename += "." + format;
				}

				response.setContentType("application/xml");
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + filename + "\"");
				response.setStatus(HttpServletResponse.SC_OK);

				OutputStream out = response.getOutputStream();
				String encoding = request.getHeader("Accept-Encoding");

				// Supports GZIP content encoding
				if (encoding != null && encoding.indexOf("gzip") >= 0)
				{
					response.setHeader("Content-Encoding", "gzip");
					out = new GZIPOutputStream(out);
				}

				out.write(URLDecoder.decode(xml, "UTF-8").getBytes("UTF-8"));
				out.flush();
				out.close();
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		}
	}

}
