package com.drag.tb1906.kj.resp;

import java.math.BigDecimal;

import com.drag.tb1906.common.BaseResponse;

import lombok.Data;

@Data
public class KjGoodsResp extends BaseResponse{
	
	private static final long serialVersionUID = -4761015373320632665L;

	private int kjgoodsId;
	
	private String kjcode;
	
	private BigDecimal price;
}
