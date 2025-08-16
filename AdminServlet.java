package com.testlab.servlets.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
@Override
protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	resp.setContentType("text/html");
	
	HttpSession session= req.getSession(false);
	String name =(String) session.getAttribute("userName");
	String email =(String) session.getAttribute("userEmail");
	
	PrintWriter out = resp.getWriter();
	
	
	if(name.equals("admin")|| email.equals("admin@gmail.com")) {
		out.print("<h1> welcome Admin</h1>");
		resp.sendRedirect("admin.html");
	}
	
	
	
	
	
	
	
	
	
}
}
