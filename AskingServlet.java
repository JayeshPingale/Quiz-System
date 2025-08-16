package com.testlab.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AskingServlet")
public class AskingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        HttpSession session = req.getSession(false);
        PrintWriter out = resp.getWriter();

        if (session == null || session.getAttribute("userEmail") == null) {
            out.println("<h3>Session expired. Please <a href='Login.html'>login</a> again.</h3>");
            return;
        }

        String email = (String) session.getAttribute("userEmail");
        String name = (String) session.getAttribute("userName");

        // == HTML + CSS ==
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset=\"UTF-8\"><title>Quiz Options</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; }");
        out.println(".container { max-width: 600px; margin: 100px auto; background: #fff; padding: 30px; "
                + "border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1); text-align: center; }");
        out.println("h2 { color: #333; margin-bottom: 20px; }");
        out.println(".user-info { font-size: 16px; color: #555; margin-bottom: 20px; }");
        out.println(".btn { display: inline-block; margin: 10px; padding: 12px 25px; font-size: 16px; "
                + "border-radius: 5px; cursor: pointer; text-decoration: none; color: #fff; background-color: #007BFF; }");
        out.println(".btn:hover { background-color: #0056b3; }");
        out.println(".btn-secondary { background-color: #28a745; }");
        out.println(".btn-secondary:hover { background-color: #1e7e34; }");
        out.println(".btn-logout { background-color: #dc3545; }");
        out.println(".btn-logout:hover { background-color: #b02a37; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<div class='user-info'>Welcome, <strong>" + name + "</strong> (" + email + ")</div>");
        out.println("<h2>What would you like to do?</h2>");
        out.println("<a href='DashboardServlet' class='btn'>📜 Check History</a>");
        out.println("<a href='Question_1_servlet' class='btn btn-secondary'>📝 Give Test</a>");
        out.println("<form action='logout' method='post' style='display:inline;'>");
        out.println("<button type='submit' class='btn btn-logout'>🚪 Logout</button>");
        out.println("</form>");
        out.println("</div>");
        out.println("</body></html>");
    }
}
