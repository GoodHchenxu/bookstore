package cn.itcast.bookstore.category.service;

import java.util.List;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.category.dao.CategoryDao;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.web.servlet.admin.CategoryException;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	private BookDao bookDao = new BookDao();

	public List<Category> findAll() {
		return categoryDao.findAll();
	}
	/**
	 * 添加分类
	 * @param category
	 */
	public void add(Category category) {
		categoryDao.add(category);
	}
	/**
	 * 删除分类
	 * @param cid
	 * @throws CategoryException 
	 */
	public void delete(String cid) throws CategoryException {
		//获取该分类下图书的本数
		int count = bookDao.getCountByCid(cid);
		//如果分类下有图书，则不让删除
		if(count > 0) throw new CategoryException("该分类有图书，不能删除");
		//没有图书，则可以删除
		categoryDao.delete(cid);
	}
	/**
	 * 加载分类
	 * @param cid
	 * @return
	 */
	public Category load(String cid) {
		return categoryDao.load(cid);
	}
	/**
	 * 修改分类
	 * @param category
	 */
	public void edit(Category category) {
		categoryDao.edit(category);
	}
}
