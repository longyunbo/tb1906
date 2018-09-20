package com.drag.tb1906.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.tb1906.store.entity.OrderRemind;



public interface OrderRemindDao extends JpaRepository<OrderRemind, String>, JpaSpecificationExecutor<OrderRemind> {
	
	@Query(value = "select max(number) as  number from t_order_remind where left(order_time,10) = ?1", nativeQuery = true)
	String findMaxNumber(String orderTime);
	
	@Query(value = "select * from t_order_remind where id = ?1", nativeQuery = true)
	OrderRemind findOne(int id);
	
	List<OrderRemind> findByOpenid(String openid);
}
