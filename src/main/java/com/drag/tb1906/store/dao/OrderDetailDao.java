package com.drag.tb1906.store.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.tb1906.store.entity.OrderDetail;


public interface OrderDetailDao extends JpaRepository<OrderDetail, String>, JpaSpecificationExecutor<OrderDetail> {
	
	@Query(value = "select * from t_order_detail where orderid = ?1", nativeQuery = true)
	List<OrderDetail> findByOrderId(String orderid);
}
