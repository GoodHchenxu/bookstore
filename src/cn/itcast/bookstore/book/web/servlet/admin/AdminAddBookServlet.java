package cn.itcast.bookstore.book.web.servlet.admin;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.book.service.BookService;

public class AdminAddBookServlet extends HttpServlet {
	private BookService bookService = new BookService();
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		
		/*
		 * 1,使用上传三步
		 * 把表单数据封装到Book对象中
		 * 创建工厂，得到解析器，解析request得到表达字段List<FileItem>
		 *
		 */
	}
}
