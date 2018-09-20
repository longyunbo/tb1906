package com.drag.tb1906.pay.resp;

import com.drag.tb1906.common.BaseResponse;

import lombok.Data;

@Data
public class PayResp extends BaseResponse{
	
	private static final long serialVersionUID = -2350517572294913622L;

	private String balance;
	
	private String orderId;

}
