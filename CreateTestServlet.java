package com.testlab.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.testlab.connection.DBConnection;

@WebServlet("/CreateTestServlet")
public class CreateTestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String testName = req.getParameter("testName");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            resp.sendRedirect("Login.html");
            return;
        }
        String createdBy = (String) session.getAttribute("userEmail");

        String testCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        try (Connection con = DBConnection.getInstance().getConnection()) {
            String sql = "INSERT INTO tests (test_code, test_name, created_by) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, testCode);
                ps.setString(2, testName);
                ps.setString(3, createdBy);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int testId = rs.getInt(1);
                    resp.sendRedirect("addquestion.html?testId=" + testId + "&testCode=" + testCode);
                } else {
                    resp.getWriter().println("Error creating test");
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
