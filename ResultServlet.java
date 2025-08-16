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

@WebServlet("/ResultServlet")
public class ResultServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null || session.getAttribute("testId") == null) {
            out.println("Session expired or test not selected. Please login and enter a test code.");
            return;
        }

        Integer testId = (Integer) session.getAttribute("testId");
        String email = (String) session.getAttribute("userEmail");
        String name = (String) session.getAttribute("userName");

        String questionIdStr = req.getParameter("questionId");
        String selectedAnswer = req.getParameter("answer");
        if (questionIdStr != null && selectedAnswer != null) {
            session.setAttribute("answer_" + questionIdStr, selectedAnswer);
        }

        int score = 0;
        int totalMarks = 0;

        try (Connection connection = DBConnection.getInstance().getConnection()) {
            String qSql = "SELECT id, answer, total_marks FROM quiz_questions WHERE test_id=? ORDER BY id";
            try (PreparedStatement ps = connection.prepareStatement(qSql)) {
                ps.setInt(1, testId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int qid = rs.getInt("id");
                        String correctAnswer = rs.getString("answer");
                        int marksForQuestion = rs.getInt("total_marks");
                        totalMarks += marksForQuestion;

                        String userAnswer = (String) session.getAttribute("answer_" + qid);
                        if (correctAnswer != null && correctAnswer.equals(userAnswer)) {
                            score += marksForQuestion;
                        }
                    }
                }
            }

            String insertSql = "INSERT INTO results (email, score, test_id) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setString(1, email);
                ps.setInt(2, score);
                ps.setInt(3, testId);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error: " + escapeHtml(e.getMessage()));
            return;
        }

        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Quiz Results</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".container { max-width: 520px; background: #ffffffcc; padding: 40px 35px; border-radius: 20px; box-shadow: 0 12px 40px rgba(0,0,0,0.12); text-align: center; backdrop-filter: blur(10px); box-sizing: border-box; }");
        out.println("h2 { color: #0b3c8c; margin-bottom: 30px; font-weight: 900; font-size: 28px; text-shadow: 1px 1px 1px rgba(0,0,0,0.1); }");
        out.println(".score { font-size: 24px; color: #28a745; font-weight: 900; margin-bottom: 35px; }");
        out.println(".user-info { font-size: 14px; color: #555; margin-bottom: 25px; }");
        out.println(".btn { background-color: #0b66c2; border: none; color: white; padding: 14px 28px; font-size: 18px; font-weight: 700; border-radius: 14px; cursor: pointer; margin: 10px 8px; box-shadow: 0 8px 25px rgba(11,102,194,0.5); transition: background-color 0.35s ease, transform 0.25s ease; display: inline-block; text-decoration: none; width: 160px; }");
        out.println(".btn:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 12px 35px rgba(8,74,138,0.65); }");
        out.println("a.btn-link { color: white; text-decoration: none; font-weight: 700; display: inline-block; width: 100%; }");
        out.println("</style>");
        out.println("</head><body><div class='container'>");
        out.println("<div class='user-info'>Logged in as: " + escapeHtml(name) + " (" + escapeHtml(email) + ")</div>");
        out.println("<h2>Quiz Completed!</h2>");
        out.println("<div class='score'>Your Final Score: " + score + " / " + totalMarks + "</div>");

        out.println("<form action='logout' method='post' style='display:inline-block;'>");
        out.println("<button class='btn' type='submit'>Logout</button>");
        out.println("</form>");

        out.println("<button class='btn'>");
        out.println("<a href='logoutPageDashBoard' class='btn-link'>View History</a>");
        out.println("</button>");

        out.println("<button class='btn'>");
        out.println("<a href='EnterTestCode.html' class='btn-link'>Retest/Enter New Code</a>");
        out.println("</button>");

        out.println("</div></body></html>");
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
