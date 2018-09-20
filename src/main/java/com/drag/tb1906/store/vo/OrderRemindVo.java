package com.drag.tb1906.store.vo;

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
@EqualsAndHashCode(callSuper=false)
public class OrderRemindVo implements Serializable {
	
	private static final long serialVersionUID = -1543164314800485012L;
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
	private String createTime;

}
