package com.drag.tb1906.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.tb1906.user.entity.UserReceivingAddress;

public interface UserReceivingAddressDao extends JpaRepository<UserReceivingAddress, String>, JpaSpecificationExecutor<UserReceivingAddress> {

	@Query(value = "select * from t_receiving_address where uid = ?1 ", nativeQuery = true)
	UserReceivingAddress findByUid(int uid);
}
