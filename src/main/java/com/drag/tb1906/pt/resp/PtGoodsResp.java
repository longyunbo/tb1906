package com.drag.tb1906.pt.resp;

import com.drag.tb1906.common.BaseResponse;

import lombok.Data;

@Data
public class PtGoodsResp extends BaseResponse{
	
	private static final long serialVersionUID = -4195525113654121659L;
	
	private int ptgoodsId;
	
	private String ptcode;
}
