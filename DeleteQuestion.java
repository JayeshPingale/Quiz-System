package com.testlab.servlets.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.testlab.connection.DBConnection;

@WebServlet("/DeleteQuestion")
public class DeleteQuestion extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if(idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect("DeleteQuestionList");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            try (Connection con = DBConnection.getInstance().getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM quiz_questions WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.setContentType("text/html");
        resp.getWriter().println("<script>alert('Question deleted successfully!'); window.location='DeleteQuestionList';</script>");
    }
}
