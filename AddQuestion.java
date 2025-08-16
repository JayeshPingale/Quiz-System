package com.testlab.servlets.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.testlab.connection.DBConnection;

@WebServlet("/AddQuestion")
public class AddQuestion extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userEmail") == null) {
            resp.sendRedirect("Login.html");
            return;
        }

        String testIdStr = req.getParameter("testId");
        if (testIdStr == null || testIdStr.trim().isEmpty()) {
            out.println("Test ID is missing.");
            return;
        }
        int testId = Integer.parseInt(testIdStr);
        
        String question = req.getParameter("Question");
        String option_1 = req.getParameter("option_1");
        String option_2 = req.getParameter("option_2");
        String option_3 = req.getParameter("option_3");
        String option_4 = req.getParameter("option_4");
        String answer = req.getParameter("answer");
        String marks = req.getParameter("marks");
        String category = req.getParameter("typeofquestion");

        try (Connection connection = DBConnection.getInstance().getConnection()) {
            String sql = "INSERT INTO quiz_questions (question, option_1, option_2, option_3, option_4, answer, total_marks, category, test_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, question);
                ps.setString(2, option_1);
                ps.setString(3, option_2);
                ps.setString(4, option_3);
                ps.setString(5, option_4);
                ps.setString(6, answer);
                ps.setString(7, marks);
                ps.setString(8, category);
                ps.setInt(9, testId);
                ps.executeUpdate();
            }
            resp.sendRedirect("addquestion.html?testId=" + testId + "&success=1");
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
}
