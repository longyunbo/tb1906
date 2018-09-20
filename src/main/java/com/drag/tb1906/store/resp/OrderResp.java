package com.drag.tb1906.store.resp;

import com.drag.tb1906.common.BaseResponse;

import lombok.Data;

@Data
public class OrderResp extends BaseResponse{
	
	private static final long serialVersionUID = -432268651252268645L;
	
	private int ticketId;
	
	private String orderId;
	
}
