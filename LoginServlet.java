package com.testlab.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.testlab.connection.DBConnection;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email != null) email = email.trim();
        if (password != null) password = password.trim();

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            out.println("<html><body><script>alert('Email and password must not be empty.'); window.location.href='Login.html';</script></body></html>");
            return;
        }

        if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
            out.println("<html><body><script>alert('Please enter a valid email address.'); window.location.href='Login.html';</script></body></html>");
            return;
        }

        if (email.equals("admin@gmail.com") && password.equals("admin")) {
            HttpSession session = req.getSession();
            session.setAttribute("userEmail", email);
            session.setAttribute("userName", "Admin");
            resp.sendRedirect("admin.html");
            return;
        }

        String sql = "SELECT email, name FROM user WHERE email=? AND password=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = req.getSession();
                session.setAttribute("userEmail", rs.getString("email"));
                session.setAttribute("userName", rs.getString("name"));
                resp.sendRedirect("UserChoiceServlet");
            } else {
                out.println("<html><body><script>alert('Login failed. Invalid email or password.'); window.location.href='Login.html?error=1&email="
                        + java.net.URLEncoder.encode(email, "UTF-8") + "';</script></body></html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html><body><p style='color:red;'>Error: " + escapeHtml(e.getMessage()) + "</p><a href='Login.html'>Back to Login</a></body></html>");
        }
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
