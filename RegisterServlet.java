package com.testlab.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.testlab.connection.DBConnection;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirm_password");

        email = (email != null) ? email.trim() : "";
        username = (username != null) ? username.trim() : "";
        password = (password != null) ? password.trim() : "";
        confirmPassword = (confirmPassword != null) ? confirmPassword.trim() : "";

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            resp.sendRedirect("Register.html?error=empty&email=" + URLEncoder.encode(email, "UTF-8"));
            return;
        }

        if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
            resp.sendRedirect("Register.html?error=invalidemail&email=" + URLEncoder.encode(email, "UTF-8"));
            return;
        }

        if (username.length() < 3) {
            resp.sendRedirect("Register.html?error=invalidusername&email=" + URLEncoder.encode(email, "UTF-8"));
            return;
        }

        if (password.length() < 6) {
            resp.sendRedirect("Register.html?error=weakpass&email=" + URLEncoder.encode(email, "UTF-8"));
            return;
        }

        if (!password.equals(confirmPassword)) {
            resp.sendRedirect("Register.html?error=pass&email=" + URLEncoder.encode(email, "UTF-8"));
            return;
        }

        try (Connection connection = DBConnection.getInstance().getConnection()) {
            String checkSql = "SELECT email FROM user WHERE email=?";
            try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
                checkPs.setString(1, email);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        resp.sendRedirect("Register.html?error=email&email=" + URLEncoder.encode(email, "UTF-8"));
                        return;
                    }
                }
            }

            String insertSql = "INSERT INTO user (email, name, password) VALUES (?, ?, ?)";
            try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                insertPs.setString(1, email);
                insertPs.setString(2, username);
                insertPs.setString(3, password);
                insertPs.executeUpdate();
            }

            resp.sendRedirect("Login.html?register=success");

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println("Error during registration: " + e.getMessage());
        }
    }
}
