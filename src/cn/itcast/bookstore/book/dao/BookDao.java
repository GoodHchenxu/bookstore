package cn.itcast.bookstore.book.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	/*
	 * 查询所有图书
	 * */
	public List<Book> findAll() {
		String sql = "select * from book";
		try {
			return qr.query(sql, new BeanListHandler<Book>(Book.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 按分类查询图书
	 * */
	public List<Book> findByCategory(String cid) {
		String sql = "select * from book where cid=?";
		try {
			return qr.query(sql, new BeanListHandler<Book>(Book.class),cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 加载图书信息
	 * */
	public Book findByBid(String bid) {
		String sql = "select * from book where bid=?";
		try {
			/*
			 * 我们需要在book中保存categoey的信息，使用一个map映射出两个对象
			 * 然后再给这两个对象建立连接
			 */
			Map<String, Object> map = qr.query(sql, new MapHandler(),bid);
			Category category = CommonUtils.toBean(map, Category.class);
			Book book = CommonUtils.toBean(map, Book.class);
			book.setCategory(category);
			return book;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 查询指定分类下图书的本数
	 * @param cid
	 * @return
	 */
	public int getCountByCid(String cid) {
		String sql = "select count(*) from book where cid=?";
		try {
			Number num = (Number)qr.query(sql, new ScalarHandler(),cid);
			return num.intValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
