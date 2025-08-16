package com.testlab.servlets.admin;


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

import com.testlab.connection.DBConnection;

@WebServlet("/TestResults")
public class TestResultsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html><head><title>Test Attempts and Scores</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin:0; padding:0; min-height:100vh; display:flex; justify-content:center; align-items:center; }");
        out.println(".container { max-width: 900px; width: 95%; background: #ffffffcc; padding: 40px 30px; border-radius: 20px; box-shadow: 0 15px 45px rgba(0,0,0,0.15); backdrop-filter: blur(10px); box-sizing: border-box; }");
        out.println("h2 { text-align: center; color: #0b3c8c; margin-bottom: 40px; font-size: 32px; font-weight: 900; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
        out.println("table { width: 100%; border-collapse: collapse; border-radius: 14px; overflow: hidden; box-shadow: 0 10px 45px rgba(0,0,0,0.17); }");
        out.println("th, td { padding: 16px 20px; border: 1px solid #ddd; font-weight: 600; font-size: 16px; color: #1e293b; text-align: center; }");
        out.println("th { background-color: #0b66c2; color: white; }");
        out.println("tbody tr:nth-child(even) { background-color: #f2faff; }");
        out.println("tbody tr:hover { background-color: #d0e4ff; transition: background-color 0.3s ease; }");
        out.println("button.back-btn { background-color: #0b66c2; color: white; border: none; padding: 14px 32px; font-size: 18px; font-weight: 700; border-radius: 16px; cursor: pointer; margin-top: 30px; display: block; width: 180px; margin-left: auto; margin-right: auto; box-shadow: 0 10px 40px rgba(11,102,194,0.6); transition: background-color 0.3s ease, transform 0.25s ease; }");
        out.println("button.back-btn:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 14px 50px rgba(8,74,138,0.8); }");
        out.println("</style>");
        out.println("</head><body><div class='container'>");
        out.println("<h2>Test Attempts and Scores</h2>");

        try (Connection con = DBConnection.getInstance().getConnection()) {
            String sql = "SELECT email, score, quiz_date FROM results ORDER BY quiz_date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            out.println("<table>");
            out.println("<thead><tr><th>User Email</th><th>Score</th><th>Date of Test</th></tr></thead><tbody>");
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                out.println("<tr>");
                out.println("<td>" + escapeHtml(rs.getString("email")) + "</td>");
                out.println("<td>" + rs.getInt("score") + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("quiz_date")) + "</td>");
                out.println("</tr>");
            }
            if (!hasData) {
                out.println("<tr><td colspan='3'>No results found.</td></tr>");
            }
            out.println("</tbody></table>");
        } catch (Exception e) {
            out.println("<p style='color:red'>Error: " + e.getMessage() + "</p>");
        }

        out.println("<button class='back-btn' onclick=\"window.location.href='admin.html'\">Back to Admin Panel</button>");
        out.println("</div></body></html>");
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
