package com.drag.tb1906.store.form;

import lombok.Data;

/**
 * 订单form
 * @author longyunbo
 *
 */
@Data
public class OrderRemindForm {
	
	
	private int id;
	/**
	 * 用户编号
	 */
	private String openid;
	/**
	 * 真实姓名
	 */
	private String realname;
	/**
	 * 电话
	 */
	private String mobile;
	/**
	 * 性别，0-男，1-女
	 */
	private int sex;
	/**
	 * 预约时间
	 */
	private String orderTime;
	/**
	 * 预约人数
	 */
	private int peoplecount;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 门店地址
	 */
	private String address;
	/**
	 * 位置
	 */
	private String position;
	/**
	 * formId
	 */
	private String formId;
	
}
