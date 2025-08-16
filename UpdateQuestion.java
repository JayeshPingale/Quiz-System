package com.testlab.servlets.admin;

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

import com.testlab.connection.DBConnection;

@WebServlet("/UpdateQuestion")
public class UpdateQuestion extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String qidStr = req.getParameter("questionId");
        if (qidStr == null || qidStr.isEmpty()) {
            resp.sendRedirect("UpdateQuestionList");
            return;
        }
        int questionId = Integer.parseInt(qidStr);
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            String sql = "SELECT question, option_1, option_2, option_3, option_4, answer, total_marks, category FROM quiz_questions WHERE id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                out.println("<!DOCTYPE html><html><head><title>Edit Question</title>");
                out.println("<style>");
                out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
                out.println(".container { width: 520px; margin: 50px auto; background: #ffffffcc; padding: 40px 35px; border-radius: 20px; box-shadow: 0 12px 35px rgba(0,0,0,0.12); text-align: left; backdrop-filter: blur(10px); box-sizing: border-box; }");
                out.println("h2 { color: #0b3c8c; font-weight: 900; font-size: 30px; margin-bottom: 30px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); text-align: center; }");
                out.println("label { margin-top: 20px; display: block; font-weight: 700; color: #334155; font-size: 16px; }");
                out.println("input[type=text], select { width: 100%; padding: 14px 18px; margin-top: 10px; border-radius: 12px; border: 1px solid #aacde8; font-size: 16px; box-sizing: border-box; transition: border-color 0.3s ease, box-shadow 0.3s ease; }");
                out.println("input[type=text]:focus, select:focus { border-color: #0b66c2; box-shadow: 0 0 8px #0b66c2aa; outline: none; }");
                out.println("input[type=submit], button { margin-top: 30px; background-color: #0b66c2; color: white; padding: 14px 0; font-size: 18px; font-weight: 700; border-radius: 14px; cursor: pointer; border: none; width: 100%; box-shadow: 0 8px 25px rgba(11,102,194,0.5); transition: background-color 0.35s ease, transform 0.25s ease; }");
                out.println("input[type=submit]:hover, button:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 12px 35px rgba(8,74,138,0.65); }");
                out.println("</style>");
                out.println("</head><body>");
                out.println("<div class='container'>");
                out.println("<h2>Edit Question ID: " + questionId + "</h2>");
                out.println("<form method='post' action='UpdateQuestion'>");
                out.println("<input type='hidden' name='questionId' value='" + questionId + "'>");
                out.println("<label for='question'>Question:</label>");
                out.println("<input type='text' id='question' name='question' value='" + escapeHtml(rs.getString("question")) + "' required>");
                out.println("<label for='option_1'>Option 1:</label>");
                out.println("<input type='text' id='option_1' name='option_1' value='" + escapeHtml(rs.getString("option_1")) + "' required>");
                out.println("<label for='option_2'>Option 2:</label>");
                out.println("<input type='text' id='option_2' name='option_2' value='" + escapeHtml(rs.getString("option_2")) + "' required>");
                out.println("<label for='option_3'>Option 3:</label>");
                out.println("<input type='text' id='option_3' name='option_3' value='" + escapeHtml(rs.getString("option_3")) + "' required>");
                out.println("<label for='option_4'>Option 4:</label>");
                out.println("<input type='text' id='option_4' name='option_4' value='" + escapeHtml(rs.getString("option_4")) + "' required>");
                out.println("<label for='answer'>Answer:</label>");
                out.println("<input type='text' id='answer' name='answer' value='" + escapeHtml(rs.getString("answer")) + "' required>");
                out.println("<label for='marks'>Marks:</label>");
                out.println("<input type='text' id='marks' name='marks' value='" + rs.getInt("total_marks") + "' required>");
                out.println("<label for='category'>Category:</label>");
                out.println("<input type='text' id='category' name='category' value='" + escapeHtml(rs.getString("category")) + "' required>");
                out.println("<input type='submit' value='Update Question'>");
                out.println("</form>");
                out.println("<button onclick=\"window.location.href='UpdateQuestionList'\">Back to Question List</button>");
                out.println("</div></body></html>");
            } else {
                out.println("Question not found.");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String qidStr = req.getParameter("questionId");
        if (qidStr == null || qidStr.isEmpty()) {
            resp.sendRedirect("UpdateQuestionList");
            return;
        }
        int questionId = Integer.parseInt(qidStr);
        String question = req.getParameter("question");
        String option_1 = req.getParameter("option_1");
        String option_2 = req.getParameter("option_2");
        String option_3 = req.getParameter("option_3");
        String option_4 = req.getParameter("option_4");
        String answer = req.getParameter("answer");
        String marksStr = req.getParameter("marks");
        String category = req.getParameter("category");
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            String sql = "UPDATE quiz_questions SET question=?, option_1=?, option_2=?, option_3=?, option_4=?, answer=?, total_marks=?, category=? WHERE id=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, question);
                ps.setString(2, option_1);
                ps.setString(3, option_2);
                ps.setString(4, option_3);
                ps.setString(5, option_4);
                ps.setString(6, answer);
                ps.setInt(7, Integer.parseInt(marksStr));
                ps.setString(8, category);
                ps.setInt(9, questionId);
                int updated = ps.executeUpdate();
                resp.setContentType("text/html");
                PrintWriter out = resp.getWriter();
                if (updated > 0) {
                    out.println("<script>alert('Question updated successfully!'); window.location='UpdateQuestion?questionId=" + questionId + "';</script>");
                } else {
                    out.println("<script>alert('Update failed!'); window.location='UpdateQuestionList';</script>");
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // Utility to escape HTML
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
