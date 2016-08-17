package cn.itcast.bookstore.book.service;

import java.util.List;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.book.domain.Book;

public class BookService {
	private BookDao bookDao = new BookDao();
	/*
	 * 查询所有图书
	 * */
	public List<Book> findAll() {
		return bookDao.findAll();
	}
	public List<Book> findByCategory(String cid) {
		return bookDao.findByCategory(cid);
	}
	public Book load(String bid) {
		return bookDao.findByBid(bid);
	}
	
}
