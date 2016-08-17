package cn.itcast.bookstore.user.service;

import cn.itcast.bookstore.user.dao.UserDao;
import cn.itcast.bookstore.user.domain.User;

/*
 * 
 * */
public class UserService {
	private UserDao userDao = new UserDao();
	
	public void regist(User form) throws UserException{
		User user = userDao.findByUsername(form.getUsername());
		if(user != null) throw new UserException("用户名已经被注册");
		
		user = userDao.findByEmail(form.getEmail());
		if(user != null) throw new UserException("邮箱已经被注册");
		
		userDao.add(form);
	}
	
	public void active(String code) throws UserException{
		//使用code查询数据库得到user
		User user = userDao.findByCode(code);
		//当user不存在时，说明激活码无效
		if(user == null) throw new UserException("激活码无效");
		//当用户状态已经是激活时，抛出异常
		if(user.isState()) throw new UserException("您已经激活了，请不要再次激活");
		//更改用户状态
		userDao.updateState(user.getUid(), true);
	}
	/*1，通过username调用dao方法查询数据库
	 *2，如果为user为空，抛出异常，用户名不存在
	 *3，比较form的密码与查出的数据的密码是否相同，不同则抛出异常，密码错误
	 *4，查看用户的状态，是否激活
	 *5，返回user
	 * */
	public User login(User form) throws UserException{
		//查询数据
		User user = userDao.findByUsername(form.getUsername());
		//当user不存在时，抛出异常
		if(user == null) throw new UserException("用户名不存在");
		//当密码不相同时，抛出异常
		if(!user.getPassword().equals(form.getPassword())) throw new UserException("密码错误");
		//查看用户的状态
		if(!user.isState()) throw new UserException("您是死的");
		return user;
		
	}
}
