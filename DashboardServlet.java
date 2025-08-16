package com.testlab.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.testlab.connection.DBConnection;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
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

        String sql = "SELECT score, quiz_date FROM results WHERE email=? ORDER BY quiz_date DESC";

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>User Dashboard</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; }");
        out.println(".container { max-width: 800px; margin: 50px auto; background: #fff; padding: 30px; "
                + "border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1); }");
        out.println("h1 { color: #333; text-align: center; margin-bottom: 20px; }");
        out.println(".user-info { font-size: 16px; color: #555; text-align: center; margin-bottom: 20px; }");
        out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
        out.println("th, td { border: 1px solid #ddd; padding: 10px; text-align: center; }");
        out.println("th { background-color: #007BFF; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f2f2; }");
        out.println(".logout-btn { background-color: #dc3545; border: none; color: white; "
                + "padding: 10px 20px; font-size: 14px; border-radius: 5px; cursor: pointer; margin-top: 20px; display: inline-block; }");
        out.println(".logout-btn:hover { background-color: #c82333; }");
        out.println(".back-btn { background-color: #0000FF; border: none; color: white; "
                + "padding: 10px 20px; font-size: 14px; border-radius: 5px; cursor: pointer; margin-top: 20px; display: inline-block; text-decoration: none; }");
        out.println(".back-btn:hover { background-color: #003cb3; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h1>Welcome to Your Dashboard</h1>");
        out.println("<div class='user-info'>Logged in as: <strong>" + escapeHtml(name) + "</strong> (" + escapeHtml(email) + ")</div>");

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            out.println("<table>");
            out.println("<tr><th>Score</th><th>Date</th></tr>");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int score = rs.getInt("score");
                String date = rs.getString("quiz_date");
                out.println("<tr><td>" + score + "</td><td>" + escapeHtml(date) + "</td></tr>");
            }
            if (!hasData) {
                out.println("<tr><td colspan='2'>No quiz history found.</td></tr>");
            }
            out.println("</table>");

        } catch (Exception e) {
            out.println("<p style='color:red'>Error: " + escapeHtml(e.getMessage()) + "</p>");
        }

        out.println("<form action='logout' method='post' style='margin-top: 20px;'>");
        out.println("<button class='logout-btn' type='submit'>Logout</button>");
        out.println("</form>");
        out.println("<a href='AskingServlet' class='back-btn'>Back</a>");

        out.println("</div></body></html>");
    }

    // Simple escaping method to avoid XSS, can be improved
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
