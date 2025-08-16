package com.testlab.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/logout")
public class logout extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Logout Successful</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%); margin: 0; padding: 0; height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".container { max-width: 500px; background: #ffffffcc; padding: 35px 40px; border-radius: 22px; box-shadow: 0 12px 40px rgba(0,0,0,0.12); text-align: center; backdrop-filter: blur(10px); }");
        out.println("h1 { color: #28a745; margin-bottom: 20px; font-size: 32px; font-weight: 900; text-shadow: 1px 1px 2px rgba(0,0,0,0.1);} ");
        out.println("p { font-size: 18px; color: #1e293b; margin-bottom: 30px; }");
        out.println(".btn { background-color: #0b66c2; border: none; color: white; padding: 14px 28px; font-size: 18px; font-weight: 700; border-radius: 12px; cursor: pointer; text-decoration: none; box-shadow: 0 8px 25px rgba(11,102,194,0.5); transition: background-color 0.35s ease, transform 0.25s ease; display: inline-block; }");
        out.println(".btn:hover { background-color: #084a8a; transform: scale(1.05); box-shadow: 0 12px 35px rgba(8,74,138,0.7); }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h1>🚪 You have logged out successfully! ✅</h1>");
        out.println("<p>Thank you for visiting. See you soon! 🎉</p>");
        out.println("<a class='btn' href='Login.html'>🔑 Login Again</a>");
        out.println("</div>");
        out.println("</body></html>");
    }
}
