package com.drag.tb1906.store.form;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

/**
 * 订单form
 * @author longyunbo
 *
 */
@Data
public class OrderInfoForm {
	
	/**
	 * 订单号
	 */
	private String orderid;
	/**
	 * 用户id
	 */
	private String openid;
	/**
	 * 商品类型
	 */
	private String type;
	/**
	 * 商品编号
	 */
	private int goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 总数量
	 */
	private int number;
	/**
	 * 总金额
	 */
	private BigDecimal price;
	/**
	 * 优惠金额
	 */
	private BigDecimal dicprice;
	/**
	 * 订单总金额
	 */
	private BigDecimal tolprice;
	/**
	 * 商户订单号
	 */
	private String outTradeNo;
	/**
	 * 规格
	 */
	private String norms;
	/**
	 * 买家姓名
	 */
	private String buyName;
	/**
	 * 买家手机号
	 */
	private String phone;
	/**
	 * formId
	 */
	private String formId;
	
	// 桌号名称
	private String remark;
	
	// 桌号
	private String tableId;

	// 桌号名称
	private String tableName;
	
	//用餐人数
	private int peopleCount;
		
	// 购买订单号
//	private String tpOrderId;
	
	
	private String shopIdenty;
	
	/**
	 * 支付类型,lpk-礼品卡
	 */
	private String payType;
	/**
	 * 卡券卡数组
	 */
	private List<JSONObject> ticketJson;
	/**
	 * 营销卡券组
	 */
	private List<JSONObject> couponsJson;
	
	/**
	 * 订单详情
	 */
	List<OrderDetailForm> orderDetail;
}
