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

@WebServlet("/UserChoiceServlet")
public class UserChoiceServlet extends HttpServlet {
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

        boolean showCreatedTests = req.getParameter("showCreatedTests") != null;

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>User Choice</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".container { max-width: 850px; width: 95%; margin: 30px auto; background: #ffffffcc; padding: 40px 35px; border-radius: 20px; box-shadow: 0 12px 30px rgba(0,0,0,0.12); text-align: center; backdrop-filter: blur(10px); box-sizing: border-box; }");
        out.println("h2 { color: #0b3c8c; margin-bottom: 40px; font-weight: 900; font-size: 32px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
        out.println("a.btn, button.btn { display: inline-block; background: #0b66c2; color: white; padding: 15px 24px; margin: 15px 10px; border-radius: 12px; font-size: 20px; font-weight: 700; text-decoration: none; cursor: pointer; box-shadow: 0 8px 25px rgba(11,102,194,0.5); transition: background-color 0.35s ease, transform 0.25s ease; }");
        out.println("a.btn:hover, button.btn:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 12px 35px rgba(8,74,138,0.65); }");
        out.println("form.logout-form button { background: #dc3545; border: none; color: white; padding: 14px 28px; font-size: 18px; font-weight: 700; border-radius: 12px; cursor: pointer; box-shadow: 0 8px 25px rgba(220,53,69,0.5); transition: background-color 0.3s ease, transform 0.25s ease; margin-top: 25px; }");
        out.println("form.logout-form button:hover { background-color: #b02a37; transform: scale(1.05); box-shadow: 0 12px 35px rgba(176,42,55,0.65); }");
        out.println(".emoji { margin-right: 8px; }");

        out.println("table { border-collapse: collapse; width: 100%; margin-top: 40px; border-radius: 14px; overflow: hidden; box-shadow: 0 8px 30px rgba(0,0,0,0.12); }");
        out.println("th, td { border: 1px solid #ddd; padding: 14px 18px; text-align: center; font-size: 16px; color: #1e293b; font-weight: 600; }");
        out.println("th { background-color: #0b66c2; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f8ff; }");
        out.println("tr:hover { background-color: #d4e8ff; transition: background-color 0.3s ease; }");
        out.println("td a { color: #0b66c2; font-weight: 700; text-decoration: none; }");
        out.println("td a:hover { text-decoration: underline; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h2><span class='emoji'>👋</span>Welcome, " + escapeHtml(name) + "!</h2>");

        // Main UI buttons
        out.println("<a href='CreateTest.html' class='btn'><span class='emoji'>✍️</span>Create New Test</a>");
        out.println("<a href='EnterTestCode.html' class='btn'><span class='emoji'>📝</span>Give a Test</a>");
        out.println("<a href='userDashBoard' class='btn'><span class='emoji'>📊</span>View History</a>");

        // Form button to toggle created tests view
        out.println("<form method='post' action='UserChoiceServlet' style='display:inline;'>");
        out.println("<button type='submit' name='showCreatedTests' class='btn'><span class='emoji'>🧑‍💻</span>View My Created Tests</button>");
        out.println("</form>");

        out.println("<form class='logout-form' action='logout' method='post'>");
        out.println("<button type='submit'><span class='emoji'>🚪</span>Logout</button>");
        out.println("</form>");

        // Conditionally show created tests table
        if (showCreatedTests) {
            out.println("<h3 style='margin-top: 50px; color: #0b3c8c;'>🧑‍💻 My Created Tests</h3>");
            out.println("<table>");
            out.println("<tr><th>Test Code</th><th>Category</th><th>Actions</th></tr>");
            boolean hasCreatedTests = false;
            try (Connection connection = DBConnection.getInstance().getConnection()) {
                String sql = "SELECT test_id, test_code, test_name FROM tests WHERE created_by=?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, email);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        hasCreatedTests = true;
                        int testId = rs.getInt("test_id");
                        String testCode = rs.getString("test_code");
                        String testName = rs.getString("test_name");
                        out.println("<tr>");
                        out.println("<td>" + escapeHtml(testCode) + "</td>");
                        out.println("<td>" + escapeHtml(testName) + "</td>");
                        out.println("<td><a href='ViewTestQuestions?testId=" + testId + "'>View Questions</a></td>");
                        out.println("</tr>");
                    }
                }
            } catch (Exception e) {
                out.println("<tr><td colspan='3' style='color:red;'>Error: " + escapeHtml(e.getMessage()) + "</td></tr>");
            }
            if (!hasCreatedTests) {
                out.println("<tr><td colspan='3'>No tests created yet.</td></tr>");
            }
            out.println("</table>");
        }

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
