package cn.itcast.bookstore.order.dao;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.domain.OrderItem;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/*
	 * 添加订单
	 *
	 * */
	public void addOrder(Order order){
		try {
			String sql = "insert into orders values(?,?,?,?,?,?)";
			/*
			 * 处理util的Date转换成sql的Timestamp
			 * */
			Timestamp timestamp = new Timestamp(order.getOrdertime().getTime());
			Object[] params = {order.getOid(),order.getOrdertime(),
					order.getTotal(),order.getState(),order.getOwner().getUid(),
					order.getAddress()};
			qr.update(sql,params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 插入订单条目
	 * */
	public void addOrderItemList(List<OrderItem> orderItemList){
		/*
		 * batch中的params是一个二维数组 object[][] params
		 * 也就是多个一维数组
		 * 每个一维数组都与sql在一起执行一次，多个一维数组执行多次
		 * */
		try {
			String sql = "insert into orderitem values(?,?,?,?,?)";
			/*
			 * 把传递过来的参数orderItemList转换成二维数组，这样就可以进行批处理
			 * 把一个orderItem对象转换成一个一维数组
			 * */
			Object[][] params = new Object[orderItemList.size()][];
			//循环遍历orderItemList，使用每个orderItem为params中的每个一维数组赋值
			for(int i = 0; i<orderItemList.size();i++){
				OrderItem item = orderItemList.get(i);
				params[i] = new Object[]{item.getIid(),item.getCount(),
						item.getSubtotal(),item.getOrder().getOid(),
						item.getBook().getBid()};
			}
			qr.batch(sql, params);//执行批处理
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 按uid来查询订单
	 * @param uid
	 * @return
	 */
	public List<Order> findByUid(String uid) {
		/*
		 * 1，通过uid来查询当前用户的所有List<Order>
		 * 2，循环遍历每个order,为其加载他的所有orderItem
		 */
		String sql = "select * from orders where uid=?";
		try {
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class),uid);
			/*
			 * 2,循环遍历所有的order，为其加载他的所有的orderItem
			 */
			for(Order order : orderList){
				loadOrderItems(order);
			}
			/*
			 * 返回订单列表
			 */
			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 为其加载指定的订单中所有的订单条目
	 * @param order
	 */
	private void loadOrderItems(Order order) {
		String sql = "select * from orderitem i,book b where i.bid=b.bid and oid=?";
		try {
			/*
			 * 因为一行结果集对应的不再是一个javabean，所以不能在使用BeanListHandler，而是要用MapListHandler
			 */
			List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(),order.getOid());
			/*
			 * 因为mapList对应的是多个map，每个map对应一个结果集
			 * 我们需要使用一个map生成两个对象，一个是orderitem,一个是Book,然后在建立两者的关系
			 * 把book设置给orderitem
			 */
			/*
			 * 循环遍历每个map，使map生成两个对象，并且建立关系
			 * 最终只有一个orderitem,并把他保存到order中
			 */
			List<OrderItem> orderItemList = toOrderItemList(mapList);
			order.setOrderItemList(orderItemList);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 把mapList中的每个Map转换成两个对象，并建立关系
	 * @param mapList
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map : mapList){
			OrderItem item = toOrderItem(map);
			orderItemList.add(item);
		}
		return orderItemList;
	}
	/**
	 * 把一个map对象转换成OrderItem对象
	 * @param map
	 * @return
	 */
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}
	/**
	 * 加载订单
	 * @param oid
	 * @return
	 */
	public Order load(String oid) {
		String sql = "select * from orders where oid=?";
		try {
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class),oid);
			/*
			 * 2,加载它自己所有的订单条目
			 */	
			loadOrderItems(order);
			/*
			 * 返回订单列表
			 */
			return order;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 通过oid来查询订单的状态
	 * @param oid
	 * @return
	 */
	public int getStateByOid(String oid) {
		try {
			String sql = "select state from orders where oid=?";
			return (Integer)qr.query(sql, new ScalarHandler(),oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 修改订单状态
	 * @param oid
	 * @param state
	 */
	public void updateState(String oid, int state) {
		try {
			String sql = "update orders set state=? where oid=?";
			qr.update(sql,state,oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
