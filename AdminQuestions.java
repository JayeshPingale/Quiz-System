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

@WebServlet("/AdminQuestions")
public class AdminQuestions extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>All Questions</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; justify-content: center; align-items: center; }");
        out.println(".container { max-width: 96%; width: 1100px; margin: 30px auto; background: #ffffffcc; padding: 40px 30px; border-radius: 20px; box-shadow: 0 15px 45px rgba(0,0,0,0.15); backdrop-filter: blur(12px); box-sizing: border-box; }");
        out.println("h1, h2 { text-align: center; color: #0b3c8c; font-weight: 900; margin-bottom: 30px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
        out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; border-radius: 14px; overflow: hidden; box-shadow: 0 10px 35px rgba(0,0,0,0.15); }");
        out.println("th, td { border: 1px solid #ddd; padding: 12px 18px; font-size: 16px; font-weight: 600; color: #1e293b; text-align: center; }");
        out.println("th { background-color: #0b66c2; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f8ff; }");
        out.println("tr:hover { background-color: #d0e4ff; transition: background-color 0.3s ease; }");
        out.println(".action-btns a { display: inline-block; margin: 0 6px; padding: 6px 14px; font-size: 14px; color: white; background-color: #0b66c2; border-radius: 12px; text-decoration: none; cursor: pointer; transition: background-color 0.3s ease; }");
        out.println(".action-btns a:hover { background-color: #084a8a; }");
        out.println(".action-btns a.delete { background-color: #dc3545; }");
        out.println(".action-btns a.delete:hover { background-color: #a71d2a; }");
        out.println(".back-btn { margin-top: 30px; padding: 14px 38px; font-size: 16px; font-weight: 700; color: white; background-color: #6c757d; border: none; border-radius: 16px; cursor: pointer; transition: background-color 0.3s ease, transform 0.2s ease; display: block; width: 160px; margin-left: auto; margin-right: auto; box-shadow: 0 7px 25px rgba(108,117,125,0.45); }");
        out.println(".back-btn:hover { background-color: #5a6268; transform: scale(1.05); box-shadow: 0 10px 35px rgba(90,98,104,0.65); }");
        out.println("</style>");
        out.println("<script>");
        out.println("function confirmDelete(id) {");
        out.println("  if(confirm('Are you sure you want to delete this question?')) {");
        out.println("    window.location.href = 'DeleteQuestion?id=' + id;");
        out.println("  }");
        out.println("}");
        out.println("</script>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h2>All Questions</h2>");

        try (Connection con = DBConnection.getInstance().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id, question, option_1, option_2, option_3, option_4, answer, category, total_marks FROM quiz_questions ORDER BY id");
            ResultSet rs = ps.executeQuery();

            out.println("<table>");
            out.println("<thead><tr><th>ID</th><th>Question</th><th>Option 1</th><th>Option 2</th><th>Option 3</th><th>Option 4</th><th>Answer</th><th>Category</th><th>Marks</th><th>Actions</th></tr></thead><tbody>");
            while (rs.next()) {
                int id = rs.getInt("id");
                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("question")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("option_1")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("option_2")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("option_3")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("option_4")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("answer")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("category")) + "</td>");
                out.println("<td>" + rs.getInt("total_marks") + "</td>");
                out.println("<td class='action-btns'>");
                out.println("<a href='UpdateQuestion?questionId=" + id + "'>Edit</a>");
                out.println("<a href='#' class='delete' onclick='confirmDelete(" + id + ")'>Delete</a>");
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table>");
        } catch (Exception e) {
            out.println("<p style='color:red'>Error loading questions.</p>");
            e.printStackTrace(out);
        }

        out.println("<button class='back-btn' onclick=\"window.location.href='admin.html'\">Back to Admin Panel</button>");
        out.println("</div></body></html>");
    }
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
