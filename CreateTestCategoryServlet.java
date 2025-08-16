package com.testlab.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.testlab.connection.DBConnection;

@WebServlet("/CreateTestCategoryServlet")
public class CreateTestCategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            resp.sendRedirect("Login.html");
            return;
        }
        String category = req.getParameter("category");
        String createdBy = (String) session.getAttribute("userEmail");
        String testCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        try (Connection con = DBConnection.getInstance().getConnection()) {
            String insertSQL = "INSERT INTO tests (test_code, test_name, created_by, status) VALUES (?, ?, ?, 'ACTIVE')";
            try (PreparedStatement ps = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, testCode);
                ps.setString(2, category);
                ps.setString(3, createdBy);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int testId = rs.getInt(1);
                    resp.sendRedirect("AddQuestionsToTest?testId=" + testId + "&testCode=" + URLEncoder.encode(testCode, "UTF-8"));
                } else {
                    resp.getWriter().println("Test creation failed.");
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
