package com.drag.tb1906.store.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 排队预约表
 * @author longyunbo
 *
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_order_remind")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class OrderRemind implements Serializable {
	
	// 预约状态,0-预约成功,1-预约失败,2-已到店,3-已过期，4-未确认，5-取消预约
	public static final int ORDERSTATUS_SUCC = 0;
	public static final int ORDERSTATUS_FAIL = 1;
	public static final int ORDERSTATUS_ALREADY = 2;
	public static final int ORDERSTATUS_EXPIRED = 3;
	public static final int ORDERSTATUS_ON = 4;
	public static final int ORDERSTATUS_CANCEL = 5;
	
	// 开抢状态,0-开抢提醒,1-取消提醒
	public final static int REMINDSTATUS_YES = 0;
	public final static int REMINDSTATUS_NO = 1;
	
	//消息发送状态:0-未发送,1-已发送,2-发送失败
	public final static int SENDSTATUS_NO = 0;
	public final static int SENDSTATUS_YES = 1;
	public final static int REMINDSTATUS_FAIL = 2;
	
	private static final long serialVersionUID = 3081292703176412600L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	/**
	 * 用户编号
	 */
	private String openid;
	/**
	 * 预约号码
	 */
	private int number;
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
	private Date orderTime;
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
	/**
	 * 0-预约成功,1-预约失败,2-已到店,3-已过期
	 */
	private int status;
	/**
	 * 0-预约提醒,1-取消提醒
	 */
	private int remindstatus;
	/**
	 * 消息发送状态:0-未发送,1-已发送,2-发送失败
	 */
	private int sendstatus;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
