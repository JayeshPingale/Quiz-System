package com.testlab.servlets.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.testlab.connection.DBConnection;

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            resp.sendRedirect("Login.html");
            return;
        }
        String userEmail = (String) session.getAttribute("userEmail");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html><head><title>Your Admin Panel</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".container { max-width: 900px; width: 90%; margin: 30px auto; background: #ffffffcc; padding: 40px 35px; border-radius: 20px; box-shadow: 0 15px 40px rgba(0,0,0,0.15); text-align: center; backdrop-filter: blur(10px); box-sizing: border-box; }");
        out.println("h2 { color: #0b3c8c; margin-bottom: 40px; font-weight: 900; font-size: 32px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
        out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; border-radius: 14px; overflow: hidden; box-shadow: 0 8px 30px rgba(0,0,0,0.12); }");
        out.println("th, td { border: 1px solid #ddd; padding: 14px 18px; text-align: center; font-size: 16px; color: #1e293b; font-weight: 600; }");
        out.println("th { background-color: #0b66c2; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f8ff; }");
        out.println("tr:hover { background-color: #d4e8ff; transition: background-color 0.3s ease; }");
        out.println(".btn { background-color: #6c757d; color: white; border: none; border-radius: 14px; font-size: 18px; padding: 14px 0; cursor: pointer; margin-top: 30px; width: 220px; font-weight: 700; box-shadow: 0 8px 25px rgba(108,117,125,0.45); transition: background-color 0.3s ease, transform 0.25s ease; }");
        out.println(".btn:hover { background-color: #5a6268; transform: scale(1.05); box-shadow: 0 12px 35px rgba(90,98,104,0.65); }");
        out.println("a { text-decoration: none; color: #0b66c2; font-weight: 700; }");
        out.println("a:hover { text-decoration: underline; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h2>Your Created Tests</h2>");
        out.println("<table>");
        out.println("<tr><th>Test Code</th><th>Category</th><th>Actions</th></tr>");
        try (Connection con = DBConnection.getInstance().getConnection()) {
            String sql = "SELECT test_id, test_code, test_name FROM tests WHERE created_by=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, userEmail);
                ResultSet rs = ps.executeQuery();
                boolean hasRows = false;
                while (rs.next()) {
                    hasRows = true;
                    int testId = rs.getInt("test_id");
                    String testCode = rs.getString("test_code");
                    String testName = rs.getString("test_name");
                    out.println("<tr>");
                    out.println("<td>" + testCode + "</td>");
                    out.println("<td>" + testName + "</td>");
                    out.println("<td><a href='ViewTestQuestions?testId=" + testId + "'>View Questions</a></td>");
                    out.println("</tr>");
                }
                if (!hasRows) {
                    out.println("<tr><td colspan='3'>No tests created yet.</td></tr>");
                }
            }
        } catch (Exception e) {
            out.println("<tr><td colspan='3' style='color:red;'>Error: " + escapeHtml(e.getMessage()) + "</td></tr>");
        }
        out.println("</table>");
        out.println("<button class='btn' onclick=\"window.location.href='UserChoiceServlet'\">⬅️ Back</button>");
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
