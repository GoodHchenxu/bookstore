package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;

import cn.itcast.bookstore.book.domain.Book;

public class CartItem {
	private Book book;
	private int count;
	/*
	 * 小计方法，处理二进制运算误差问题
	 * */
	public double getSubtotal(){//小计方法，但是他没有对应的成员
		BigDecimal d1 = new BigDecimal(book.getPrice() + "");
		BigDecimal d2 = new BigDecimal(count + "");
		return d1.multiply(d2).doubleValue();
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "CartItem [count=" + count + "]";
	}
	
}
