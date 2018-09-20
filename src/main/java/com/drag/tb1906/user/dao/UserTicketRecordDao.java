package com.drag.tb1906.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.drag.tb1906.user.entity.UserTicketRecord;


public interface UserTicketRecordDao extends JpaRepository<UserTicketRecord, String>, JpaSpecificationExecutor<UserTicketRecord> {
	
	
	
}
