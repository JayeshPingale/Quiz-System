package com.testlab.servlets.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.testlab.connection.DBConnection;

@WebServlet("/ViewTestQuestions")
public class ViewTestQuestions extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            resp.sendRedirect("Login.html");
            return;
        }
        String testIdStr = req.getParameter("testId");
        if (testIdStr == null || testIdStr.trim().isEmpty()) {
            resp.getWriter().println("Test ID missing.");
            return;
        }
        int testId = Integer.parseInt(testIdStr);
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Test Questions</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".container { max-width: 850px; width: 95%; margin: 40px auto; background: #ffffffcc; padding: 40px 35px; border-radius: 20px; box-shadow: 0 12px 30px rgba(0,0,0,0.12); text-align: center; backdrop-filter: blur(10px); box-sizing: border-box; }");
        out.println("h2 { color: #0b3c8c; margin-bottom: 35px; font-weight: 900; font-size: 30px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
        out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; border-radius: 14px; overflow: hidden; box-shadow: 0 8px 30px rgba(0,0,0,0.12); }");
        out.println("th, td { border: 1px solid #ddd; padding: 14px 18px; text-align: left; font-size: 16px; font-weight: 600; color: #1e293b; }");
        out.println("th { background-color: #0b66c2; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f8ff; }");
        out.println("tr:hover { background-color: #d4e8ff; transition: background-color 0.3s ease; }");
        out.println(".back-btn { background-color: #0b66c2; color: white; border: none; border-radius: 14px; font-size: 18px; padding: 14px 28px; cursor: pointer; margin: 30px auto 0; display: block; width: 220px; font-weight: 700; box-shadow: 0 8px 25px rgba(11,102,194,0.5); transition: background-color 0.35s ease, transform 0.25s ease; }");
        out.println(".back-btn:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 12px 35px rgba(8,74,138,0.65); }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h2>📝 Questions for Test ID: " + testId + "</h2>");
        out.println("<table>");
        out.println("<tr><th>ID</th><th>Question</th><th>Answer</th><th>Marks</th></tr>");
        try (Connection con = DBConnection.getInstance().getConnection()) {
            String sql = "SELECT id, question, answer, total_marks FROM quiz_questions WHERE test_id=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, testId);
                ResultSet rs = ps.executeQuery();
                boolean hasRows = false;
                while (rs.next()) {
                    hasRows = true;
                    out.println("<tr>");
                    out.println("<td>" + rs.getInt("id") + "</td>");
                    out.println("<td>" + escapeHtml(rs.getString("question")) + "</td>");
                    out.println("<td>" + escapeHtml(rs.getString("answer")) + "</td>");
                    out.println("<td>" + rs.getInt("total_marks") + "</td>");
                    out.println("</tr>");
                }
                if (!hasRows) {
                    out.println("<tr><td colspan='4' style='text-align:center;'>No questions found for this test.</td></tr>");
                }
            }
        } catch (Exception e) {
            out.println("<tr><td colspan='4' style='color:red;'>Error: " + escapeHtml(e.getMessage()) + "</td></tr>");
        }
        out.println("</table>");
        out.println("<button class='back-btn' onclick=\"window.location.href='AdminDashboardServlet'\">⬅️ Back to Admin Panel</button>");
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
