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

@WebServlet("/userDashBoard")
public class userDashBoard extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
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
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".container { max-width: 820px; width: 90%; margin: 30px auto; background: #ffffffcc; padding: 40px 35px; border-radius: 20px; box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12); text-align: center; backdrop-filter: blur(10px); }");
        out.println("h1 { color: #0b3c8c; margin-bottom: 35px; font-weight: 900; font-size: 32px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
        out.println(".user-info { font-size: 18px; color: #334155; margin-bottom: 25px; }");
        out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; border-radius: 12px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.1);} ");
        out.println("th, td { border: 1px solid #ddd; padding: 14px 18px; text-align: center; font-weight: 600; font-size: 16px; color: #1e293b; }");
        out.println("th { background-color: #0b66c2; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f8ff; }");
        out.println("tr:hover { background-color: #e0efff; transition: background-color 0.3s ease; }");
        out.println(".logout-btn { background-color: #dc3545; border: none; color: white; padding: 14px 28px; font-size: 16px; border-radius: 12px; cursor: pointer; margin-top: 30px; box-shadow: 0 6px 20px rgba(220,53,69,0.5); font-weight: 700; transition: background-color 0.3s ease, transform 0.25s ease; }");
        out.println(".logout-btn:hover { background-color: #b02a37; transform: scale(1.05); box-shadow: 0 10px 30px rgba(176,42,55,0.65); }");
        out.println(".back-link { display: inline-block; margin-left: 20px; font-size: 16px; padding: 14px 28px; background-color: #0b66c2; color: white; text-decoration: none; border-radius: 12px; margin-top: 30px; font-weight: 700; box-shadow: 0 6px 20px rgba(11,102,194,0.5); transition: background-color 0.3s ease, transform 0.25s ease; }");
        out.println(".back-link:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 10px 30px rgba(8,74,138,0.65); }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h1>📊 Welcome to Your Dashboard</h1>");
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
                out.println("<tr><td>" + score + "/30</td><td>" + escapeHtml(date) + "</td></tr>");
            }
            if (!hasData) {
                out.println("<tr><td colspan='2'>No quiz history found.</td></tr>");
            }
            out.println("</table>");

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + escapeHtml(e.getMessage()) + "</p>");
        }

        out.println("<form action='logout' method='post' style='display:inline;'>");
        out.println("<button class='logout-btn' type='submit'>🚪 Logout</button>");
        out.println("</form>");
        out.println("<a href='UserChoiceServlet' class='back-link'>⬅️ Back</a>");
        out.println("</div></body></html>");
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
