package com.testlab.servlets;

import java.io.IOException;
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

@WebServlet("/EvaulateServlet")
public class EvaulateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String selectedAnswer = request.getParameter("answer");
		int questionId = Integer.parseInt(request.getParameter("questionId"));

		int mark = 0;
		String sql = "SELECT answer FROM questions WHERE id=?";
		try (Connection connection = DBConnection.getInstance().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			
			ps.setInt(1, questionId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String correctAnswer = rs.getString("answer");
				if (correctAnswer.equals(selectedAnswer)) {
					mark = 5; 
				}
			}

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpSession session = request.getSession();
		Integer totalMarks = (Integer) session.getAttribute("marks");
		if (totalMarks == null)
			totalMarks = 0;
		totalMarks += mark;
		session.setAttribute("marks", totalMarks);
		

		response.sendRedirect("question_2_servlet");
	}
}
