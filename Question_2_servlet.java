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

@WebServlet("/Question_2_servlet")
public class Question_2_servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            out.println("<h3>Session expired. Please <a href='Login.html'>login</a> again.</h3>");
            return;
        }
        String email = (String) session.getAttribute("userEmail");
        String name = (String) session.getAttribute("userName");
        Integer testId = (Integer) session.getAttribute("testId");
        if (testId == null) {
            out.println("<h3>No test selected. Please enter the test code first.</h3>");
            return;
        }
        // Save previous answer
        String prevQuestionIdStr = req.getParameter("questionId");
        String selectedAnswer = req.getParameter("answer");
        if (prevQuestionIdStr != null && selectedAnswer != null) {
            session.setAttribute("answer_" + prevQuestionIdStr, selectedAnswer);
        }
        int questionNumber = 2;
        String sql = "SELECT id, question, option_1, option_2, option_3, option_4 FROM quiz_questions WHERE test_id=? ORDER BY id LIMIT 1 OFFSET ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, testId);
            ps.setInt(2, questionNumber - 1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int questionId = rs.getInt("id");
                out.println("<!DOCTYPE html><html><head><title>Question " + questionNumber + "</title>");
                out.println("<style>");
                out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
                out.println(".container { max-width: 600px; margin: 40px auto; background: #ffffffcc; padding: 30px 35px; border-radius: 20px; box-shadow: 0 12px 30px rgba(0,0,0,0.12); text-align: center; backdrop-filter: blur(10px); box-sizing: border-box; }");
                out.println("h2 { color: #0b3c8c; font-weight: 900; font-size: 28px; margin-bottom: 30px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
                out.println(".user-info { font-size: 14px; color: #555; margin-bottom: 15px; }");
                out.println("label { display: block; padding: 12px; margin: 10px 0; background: #f9f9f9; border-radius: 8px; cursor: pointer; transition: background-color 0.3s ease; font-size: 16px; text-align: left; }");
                out.println("label:hover { background: #eef6ff; }");
                out.println("input[type=radio] { margin-right: 14px; cursor: pointer; vertical-align: middle; }");
                out.println(".submit-btn { background-color: #28a745; border: none; color: white; padding: 14px 30px; font-size: 18px; border-radius: 12px; cursor: pointer; font-weight: 700; margin-top: 20px; box-shadow: 0 7px 20px rgba(40,167,69,0.4); transition: background-color 0.3s ease, transform 0.2s ease; width: 100%; }");
                out.println(".submit-btn:hover { background-color: #218838; transform: scale(1.05); }");
                out.println("</style></head><body>");
                out.println("<div class='container'>");
                out.println("<div class='user-info'>Logged in as: " + escapeHtml(name) + " (" + escapeHtml(email) + ")</div>");
                out.println("<h2>Q" + questionNumber + ") " + escapeHtml(rs.getString("question")) + "</h2>");
                String nextServlet = "Question_3_servlet";
                out.println("<form action='" + nextServlet + "' method='post'>");
                out.println("<label><input type='radio' name='answer' value='" + escapeHtml(rs.getString("option_1")) + "' required> " + escapeHtml(rs.getString("option_1")) + "</label>");
                out.println("<label><input type='radio' name='answer' value='" + escapeHtml(rs.getString("option_2")) + "'> " + escapeHtml(rs.getString("option_2")) + "</label>");
                out.println("<label><input type='radio' name='answer' value='" + escapeHtml(rs.getString("option_3")) + "'> " + escapeHtml(rs.getString("option_3")) + "</label>");
                out.println("<label><input type='radio' name='answer' value='" + escapeHtml(rs.getString("option_4")) + "'> " + escapeHtml(rs.getString("option_4")) + "</label>");
                out.println("<input type='hidden' name='questionId' value='" + questionId + "'>");
                out.println("<br><button class='submit-btn' type='submit'>Next</button>");
                out.println("</form>");
                out.println("</div></body></html>");
            } else {
                out.println("<h3>No questions found for this test.</h3>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p style='color:red'>Error: " + escapeHtml(e.getMessage()) + "</p>");
        }
    }
    
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
