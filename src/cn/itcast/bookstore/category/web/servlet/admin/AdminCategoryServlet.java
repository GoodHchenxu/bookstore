package cn.itcast.bookstore.category.web.servlet.admin;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminCategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	/**
	 * 修改分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*
		 * 1，封装表单数据
		 * 2，调用service的方法进行修改
		 * 3，调用findAll
		 */
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		categoryService.edit(category);
		return findAll(request, response);
	}
	/**
	 * 修改之前的加载工作
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editPre(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*
		 * 获取cid
		 * 通过cid来调用service方法，得到category对象
		 * 保存到request中，转发到mod.jsp
		 * mod.jsp:把当前的category对象显示到表单中
		 */
		String cid = request.getParameter("cid");
		Category category = categoryService.load(cid);
		request.setAttribute("category", category);
		return "f:/adminjsps/admin/category/mod.jsp";
	}
	/**
	 * 删除分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*
		 * 获取参数cid
		 * 调用service中的方法进行删除
		 * 		如果出现异常，保存异常信息，转发到msg.jsp
		 * 调用findAll方法
		 */
		String cid = request.getParameter("cid");
		try {
			categoryService.delete(cid);
			return findAll(request,response);
		} catch (CategoryException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
		
		
		
	}
	public String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*
		 * 封装表单数据
		 * 补全cid
		 * 调用service中的方法完成
		 * 调用findAll方法
		 */
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		category.setCid(CommonUtils.uuid());
		
		categoryService.add(category);
		
		return findAll(request, response);
	}
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1，调用service方法，得到所有分类
		 * 2，保存到request，转发到/adminjsps/admin/category/list.jsp
		 */
		request.setAttribute("categoryList", categoryService.findAll());
		return "f:/adminjsps/admin/category/list.jsp";
	}
}
