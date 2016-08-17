package cn.itcast.bookstore.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

/*
 * User持久层
 * */
public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	/*
	 * 通过用户名查询
	 * */
	public User findByUsername(String username){
		String sql = "select * from tb_user where username=?";
		try {
			return qr.query(sql, new BeanHandler<User>(User.class),username);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
	}
	/*
	 * 通过email查询
	 * */
	public User findByEmail(String email){
		String sql = "select * from tb_user where email=?";
		try {
			return qr.query(sql, new BeanHandler<User>(User.class),email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
	}
	
	/*
	 * 添加用户
	 * */
	public void add(User user){
		try {
			String sql = "insert into tb_user values(?,?,?,?,?,?)";
			Object [] params = {user.getUid(),user.getUsername(),user.getPassword(),
					user.getEmail(),user.getCode(),user.isState()};
			qr.update(sql, params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 通过code查询
	 * */
	public User findByCode(String code){
		String sql = "select * from tb_user where code=?";
		try {
			return qr.query(sql, new BeanHandler<User>(User.class),code);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
	}
	/*
	 * 更改用户的状态 是否激活
	 * */
	public void updateState(String uid,boolean state){
		String sql = "update tb_user set state=? where uid=?";
		try {
			qr.update(sql,state,uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 
	 * */
}
