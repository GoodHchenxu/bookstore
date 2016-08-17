package cn.itcast.bookstore.order.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.bookstore.order.dao.OrderDao;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.jdbc.JdbcUtils;

public class OrderService {
	private OrderDao orderDao = new OrderDao();
	/*添加订单
	 *处理事务
	 * 
	 * */
	public void add(Order order){
		try {
			//开启事务
			JdbcUtils.beginTransaction();
			
			orderDao.addOrder(order);//插入订单
			orderDao.addOrderItemList(order.getOrderItemList());//插入订单条目
			
			//提交事务
			JdbcUtils.commitTransaction();
		} catch (Exception e) {
			//回滚事务
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}
	/**
	 * 获取list
	 * @param uid
	 * @return
	 */
	public List<Order> myOrders(String uid) {
		return orderDao.findByUid(uid);
	}
	/**
	 * 加载订单
	 * @param oid
	 * @return
	 */
	public Order load(String oid) {
		return orderDao.load(oid);
	}
	/**
	 * 使用oid查询订单状态
	 * 判断状态是否为3，如果不是则抛出异常
	 * 如果是，则调用Dao中的方法更改状态
	 * @param oid
	 */
	public void confirm(String oid) throws OrderException{
		/*
		 * 得到状态
		 */
		int state = orderDao.getStateByOid(oid);
		/*
		 * 校验状态是否为3
		 */
		if(state != 3) throw new OrderException("订单确认失败，你不是好人");
		/*
		 * 如果是3，调用Dao方法修改订单状态为4
		 */
		orderDao.updateState(oid,4);
	}
	/**
	 * 支付方法
	 * @param oid
	 */
	public void zhiFu(String oid) {
		/*
		 * 1. 获取订单的状态
		 *   * 如果状态为1，那么执行下面代码
		 *   * 如果状态不为1，那么本方法什么都不做
		 */
		int state = orderDao.getStateByOid(oid);
		if(state == 1) {
			// 修改订单状态为2
			orderDao.updateState(oid, 2);
		}
	}

}
