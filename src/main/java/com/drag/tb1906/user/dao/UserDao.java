package com.drag.tb1906.user.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.tb1906.user.entity.User;

public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

	@Query(value = "select * from t_user where id = ?1 ", nativeQuery = true)
	User findOne(int id);
	
	@Query(value = "select * from t_user where openid = ?1 ", nativeQuery = true)
	User findByOpenid(String openid);
	
	@Query(value = "select * from t_user where id in (?1) ", nativeQuery = true)
	List<User> findByIdIn(Set<Integer> id);
	
	@Query(value = "select * from t_user where id != (?1) ", nativeQuery = true)
	List<User> findByIdNotIn(int id);
}
