package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
	private Map<String, CartItem> map = new LinkedHashMap<String,CartItem>();
	
	/*计算合计
	 * 处理了二进制计算的误差问题
	 * */
	public double getTotal(){
		//合计=所有条目的小计之和
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem : map.values()){
			BigDecimal subtotal = new BigDecimal("" + cartItem.getSubtotal());
			total = total.add(subtotal);
		}
		return total.doubleValue();
	}
	/*添加条目
	 * */
	public void add(CartItem cartItem){
		if(map.containsKey(cartItem.getBook().getBid())){
			CartItem _cartItem = map.get(cartItem.getBook().getBid());
			_cartItem.setCount(_cartItem.getCount() + cartItem.getCount());
			map.put(cartItem.getBook().getBid(), _cartItem);
		}else {
			map.put(cartItem.getBook().getBid(), cartItem);
		}
	}
	/*
	 * 删除条目
	 * */
	public void delete(String bid){
		map.remove(bid);
	}
	/*
	 * 清空购物车
	 * */
	public void clear(){
		map.clear();
	}
	/*
	 * 获取所有的条目
	 * */
	public Collection<CartItem> getCartItems(){
		return map.values();
	}
}	
