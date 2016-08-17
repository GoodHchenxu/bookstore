package cn.itcast.bookstore.user.web.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.cart.domain.Cart;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.service.UserException;
import cn.itcast.bookstore.user.service.UserService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;



public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	public String active(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*1，获取code参数
		 *2，调用service中的方法来完成激活
		 *3，保存信息到request中，转发到msg中
		 * 
		 * */
		String code  = request.getParameter("code");
		try {
			userService.active(code);
			request.setAttribute("msg", "恭喜，您已经激活了！请马上登录");
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}
	
	public String regist(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*封装表单数据
		 *补全uid和激活码
		 *进行输入验证
		 *	保存信息到request
		 *	保存表单数据(回显)
		 *	转发
		 *调用Servlce的regist方法进行验证
		 *发邮件
		 *保存成功信息
		 *转发到msg.jsp
		 * 
		 * */
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
		form.setUid(CommonUtils.uuid());
		/*
		 * 使用一个Map集合来保存错误信息
		 * */
		Map<String, String> errors = new HashMap<String, String>();
		String username = form.getUsername();
		if(username == null || username.trim().isEmpty()){
			errors.put("username", "用户名不能为空");
		}else if(username.length() >10 || username.length() <3){
			errors.put("username","用户名长度必须在3~10之间");
		}
		
		String password = form.getPassword();
		if(password == null || password.trim().isEmpty()){
			errors.put("password", "密码不能为空");
		}else if(password.length() >10 || password.length() <3){
			errors.put("password","密码长度必须在3~10之间");
		}
		
		String email = form.getEmail();
		if(email == null || email.trim().isEmpty()){
			errors.put("email", "邮箱不能为空");
		}else if(!email.matches("\\w+@\\w+\\.\\w+")){
			errors.put("email","邮箱格式错误");
		}
		
		/*
		 判断是否存在错误信息
		 * */
		if(errors.size() > 0){
			request.setAttribute("errors", errors);
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 调用service的regist方法
		 * */
		try {
			userService.regist(form);
		} catch (UserException e) {
			/*
			 * 保存异常信息
			 * 回显
			 * 转发到regist.jsp
			 * */
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		/*执行到此步说明注册成功
		 * 发邮件
		 * 保存成功信息到request
		 * 转发到msg.jsp
		 * */
		/*
		 * 准备配置文件
		 * 获取文件的内容
		 * */
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader()
				.getResourceAsStream("email_template.properties"));
		String host = props.getProperty("host");//获取服务器主机
		String uname = props.getProperty("uname");//获取用户名
		String pwd = props.getProperty("pwd");//获取密码
		String from = props.getProperty("from");//获取发件人
		String to = form.getEmail();//获取收件人
		String subject = props.getProperty("subject");//获取主题
		String content = props.getProperty("content");//获取邮件内容
		content = MessageFormat.format(content, form.getCode());//替换{0}
		
		Session session = MailUtils.createSession(host, uname, pwd);//得到session
		Mail mail = new Mail(from, to, subject, content);//创建邮件对象
		try {
			MailUtils.send(session, mail);//发邮件！
		} catch (MessagingException e) {
		}
		
		request.setAttribute("msg", "恭喜注册成功！请马上到邮箱激活");
		return "f:/jsps/msg.jsp";
	}
	/*登录功能
	 *1，一键封装保存表单信息
	 *2，调用service的方法完成登录
	 *3，保存各种信息
	 *4，重定向到index.jsp
	 * 
	 * */
	
	public String login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		try {
			User user = userService.login(form);
			//获取session
			/*
			 * 在登录成功后，立刻给用户在session中添加一辆车
			 * */
			request.getSession().setAttribute("cart", new Cart());
			request.getSession().setAttribute("session_user", user);
			return "r:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);//回显
			return "f:/jsps/user/login.jsp";
		}
	}
	/*退出功能
	 *就是把session销毁
	 * 
	 * */
	public String quit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		return "r:/index.jsp";
	}
}
