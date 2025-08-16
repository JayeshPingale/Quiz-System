package com.testlab.servlets.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.testlab.connection.DBConnection;

@WebServlet("/DeleteQuestionList")
public class DeleteQuestionList extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html><html><head><title>Delete Questions</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; justify-content: center; align-items: center; }");
        out.println(".container { max-width: 640px; width: 90%; margin: 50px auto; background: #ffffffcc; padding: 40px 35px; border-radius: 20px; box-shadow: 0 12px 35px rgba(0,0,0,0.12); text-align: center; backdrop-filter: blur(10px); box-sizing: border-box; }");
        out.println("h2 { color: #0b3c8c; font-weight: 900; font-size: 30px; margin-bottom: 30px; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }");
        out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; border-radius: 14px; overflow: hidden; box-shadow: 0 8px 30px rgba(0,0,0,0.12); }");
        out.println("th, td { border: 1px solid #ddd; padding: 14px 18px; font-size: 16px; font-weight: 600; color: #1e293b; text-align: left; }");
        out.println("th { background-color: #0b66c2; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f2f8ff; }");
        out.println("tr:hover { background-color: #d4e8ff; transition: background-color 0.3s ease; cursor: default; }");
        out.println("a.delete-link { color: #dc3545; cursor: pointer; text-decoration: none; font-weight: 700; }");
        out.println("a.delete-link:hover { text-decoration: underline; }");
        out.println("button.back-btn { margin-top: 30px; background-color: #0b66c2; border: none; color: white; padding: 14px 28px; font-size: 18px; font-weight: 700; border-radius: 14px; cursor: pointer; box-shadow: 0 8px 25px rgba(11,102,194,0.5); transition: background-color 0.35s ease, transform 0.25s ease; width: 200px; }");
        out.println("button.back-btn:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 12px 35px rgba(8,74,138,0.65); }");
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
        out.println("<h2>Delete Questions</h2>");
        
        try (Connection con = DBConnection.getInstance().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id, question FROM quiz_questions");
            ResultSet rs = ps.executeQuery();
            
            out.println("<table>");
            out.println("<tr><th>Question</th><th>Action</th></tr>");
            
            while(rs.next()) {
                int id = rs.getInt("id");
                String question = rs.getString("question");
                out.println("<tr>");
                out.println("<td>" + escapeHtml(question) + "</td>");
                out.println("<td><a class='delete-link' onclick='confirmDelete(" + id + ")'>Delete</a></td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
        } catch (Exception e) {
            e.printStackTrace(out);
        }
        
        out.println("<button class='back-btn' onclick=\"window.location.href='admin.html'\">Back to Admin Panel</button>");
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
