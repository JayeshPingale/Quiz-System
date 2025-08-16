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

@WebServlet("/AddQuestionsToTest")
public class AddQuestionsToTest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String testId = req.getParameter("testId");
        if (testId == null || testId.trim().isEmpty()) {
            resp.getWriter().println("Test ID missing.");
            return;
        }
        resp.sendRedirect("AddQuestionsToTest.html?testId=" + testId);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        HttpSession session = req.getSession(false);
        PrintWriter out = resp.getWriter();

        if (session == null || session.getAttribute("userEmail") == null) {
            resp.sendRedirect("Login.html");
            return;
        }

        String testIdStr = req.getParameter("testId");
        if (testIdStr == null || testIdStr.trim().isEmpty()) {
            out.println("<html><body><p style='color:red;'>Test ID missing in submission.</p></body></html>");
            return;
        }

        int testId;
        try {
            testId = Integer.parseInt(testIdStr.trim());
        } catch (NumberFormatException e) {
            out.println("<html><body><p style='color:red;'>Invalid Test ID value.</p></body></html>");
            return;
        }

        String question = trimParam(req.getParameter("question"));
        String option1 = trimParam(req.getParameter("option_1"));
        String option2 = trimParam(req.getParameter("option_2"));
        String option3 = trimParam(req.getParameter("option_3"));
        String option4 = trimParam(req.getParameter("option_4"));
        String answer = trimParam(req.getParameter("answer"));
        String marksStr = trimParam(req.getParameter("marks"));

        if (question.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty()
                || option4.isEmpty() || answer.isEmpty() || marksStr.isEmpty()) {
            out.println("<html><body><p style='color:red;'>All question fields are required!</p></body></html>");
            return;
        }

        int marks;
        try {
            marks = Integer.parseInt(marksStr);
            if (marks <= 0 || marks > 10) {
                out.println("<html><body><p style='color:red;'>Marks must be between 1 and 10.</p></body></html>");
                return;
            }
        } catch (NumberFormatException e) {
            out.println("<html><body><p style='color:red;'>Invalid marks value.</p></body></html>");
            return;
        }

        try (Connection con = DBConnection.getInstance().getConnection()) {
            String uniqueCheckSql = "SELECT COUNT(*) FROM quiz_questions WHERE test_id=? AND LOWER(TRIM(question))=LOWER(TRIM(?))";
            try (PreparedStatement uniqueStmt = con.prepareStatement(uniqueCheckSql)) {
                uniqueStmt.setInt(1, testId);
                uniqueStmt.setString(2, question);
                try (ResultSet uniqueRs = uniqueStmt.executeQuery()) {
                    if (uniqueRs.next() && uniqueRs.getInt(1) > 0) {
                        out.println("<script>alert('Duplicate question detected for this test! Please enter a unique question.');window.location.href='AddQuestionsToTest.html?testId=" + testId + "';</script>");
                        return;
                    }
                }
            }

            // Insert the question
            String sql = "INSERT INTO quiz_questions (question, option_1, option_2, option_3, option_4, answer, total_marks, test_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, question);
                ps.setString(2, option1);
                ps.setString(3, option2);
                ps.setString(4, option3);
                ps.setString(5, option4);
                ps.setString(6, answer);
                ps.setInt(7, marks);
                ps.setInt(8, testId);
                ps.executeUpdate();
            }

            String countSql = "SELECT COUNT(*) FROM quiz_questions WHERE test_id=?";
            try (PreparedStatement countStmt = con.prepareStatement(countSql)) {
                countStmt.setInt(1, testId);
                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) >= 3) {
                        resp.sendRedirect("AdminDashboardServlet");
                        return;
                    }
                }
            }

            resp.sendRedirect("AddQuestionsToTest.html?testId=" + testId);

        } catch (Exception e) {
            out.println("<html><body><p style='color:red;'>Error: " + escapeHtml(e.getMessage()) + "</p></body></html>");
        }
    }

    private String trimParam(String param) {
        return param == null ? "" : param.trim();
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
