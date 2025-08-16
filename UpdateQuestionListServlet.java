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

@WebServlet("/UpdateQuestionList")
public class UpdateQuestionListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try (Connection con = DBConnection.getInstance().getConnection()) {
            String sql = "SELECT id, question FROM quiz_questions";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            out.println("<!DOCTYPE html><html><head><title>Update Question</title>");
            out.println("<style>");
            out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; justify-content: center; align-items: center; }");
            out.println(".container { max-width: 520px; width: 90%; margin: 60px auto; background: #ffffffcc; padding: 40px 35px; border-radius: 20px; box-shadow: 0 12px 35px rgba(0,0,0,0.12); text-align: center; backdrop-filter: blur(10px); box-sizing: border-box; }");
            out.println("h2 { color: #0b3c8c; font-weight: 900; font-size: 28px; margin-bottom: 30px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
            out.println("label { display: block; margin-top: 20px; font-weight: 700; color: #334155; font-size: 16px; text-align: left; }");
            out.println("select { width: 100%; padding: 14px 18px; border-radius: 12px; border: 1px solid #aacde8; margin-top: 10px; font-size: 16px; box-sizing: border-box; transition: border-color 0.3s ease, box-shadow 0.3s ease; }");
            out.println("select:focus { border-color: #0b66c2; box-shadow: 0 0 8px #0b66c2aa; outline: none; }");
            out.println("input[type=submit], button.back-btn { margin-top: 30px; background-color: #0b66c2; color: white; border: none; width: 100%; padding: 14px 0; font-size: 18px; border-radius: 14px; cursor: pointer; font-weight: 700; box-shadow: 0 8px 25px rgba(11,102,194,0.5); transition: background-color 0.35s ease, transform 0.25s ease; }");
            out.println("input[type=submit]:hover, button.back-btn:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 12px 35px rgba(8,74,138,0.65); }");
            out.println("</style>");
            out.println("</head><body>");
            out.println("<div class='container'>");
            out.println("<h2>Select Question to Update</h2>");
            out.println("<form method='get' action='UpdateQuestion'>");
            out.println("<label for='questionId'>Questions:</label>");
            out.println("<select name='questionId' id='questionId' required>");
            out.println("<option value=''>-- Select --</option>");
            while (rs.next()) {
                int id = rs.getInt("id");
                String question = rs.getString("question");
                out.println("<option value='" + id + "'>" + escapeHtml(question) + "</option>");
            }
            out.println("</select>");
            out.println("<input type='submit' value='Edit Question'>");
            out.println("</form>");
            out.println("<button type='button' onclick=\"window.location.href='admin.html'\" class='back-btn'>Back to Admin Panel</button>");
            out.println("</div></body></html>");
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
