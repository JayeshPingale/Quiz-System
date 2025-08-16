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

@WebServlet("/EnterTestCodeServlet")
public class EnterTestCodeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String testCode = req.getParameter("testCode");
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userEmail") == null) {
            resp.sendRedirect("Login.html");
            return;
        }

        try (Connection con = DBConnection.getInstance().getConnection()) {
            String sql = "SELECT test_id FROM tests WHERE test_code=? AND status='ACTIVE'";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, testCode);
                ResultSet rs = ps.executeQuery();

                resp.setContentType("text/html");
                PrintWriter out = resp.getWriter();

                if (rs.next()) {
                    int testId = rs.getInt("test_id");
                    session.setAttribute("testId", testId);
                    resp.sendRedirect("Question_1_servlet");
                } else {
                    out.println("<!DOCTYPE html><html><head><title>Enter Test Code</title></head><body>");
                    out.println("<h3>Invalid or Inactive Test Code.</h3>");
                    out.println("<a href='EnterTestCode.html'>Try again</a>");
                    out.println("</body></html>");
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
