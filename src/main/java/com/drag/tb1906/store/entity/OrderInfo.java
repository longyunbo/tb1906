package com.drag.tb1906.store.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * 商品信息表
 * @author longyunbo
 *
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_order_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class OrderInfo implements Serializable {

	private static final long serialVersionUID = 8976480717545183113L;
	
	// 订单状态0-未付款，1-已付款，2-已退款
	public static final int ORDERSTATUS_UNPAY = 0;
	public static final int ORDERSTATUS_SUCCESS = 1;
	public static final int ORDERSTATUS_RETURN_SUCC = 2;
	public static final int ORDERSTATUS_RETURN_ON = 3;
	
	public static final int ISBILLING_NO = 0;
	public static final int ISBILLING_YES = 1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	/**
	 * 订单编号
	 */
	private String orderid;
	/**
	 * 客如云订单号
	 */
	private String kryorderid;
	/**
	 * 商品类型,rx-热销,zk-折扣,xy-鲜鸭,ms-美素
	 */
	private String type;
	/**
	 * 商品编号(有机食品不需要传，其他的类型需传)
	 */
	private int goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 商品图片
	 */
	private String goodsImg;
	/**
	 * 用户编号
	 */
	private int uid;
	/**
	 * 买家姓名
	 */
	private String buyName;
	/**
	 * 买家电话
	 */
	private String phone;
	/**
	 * 商品总数量
	 */
	private int number;
	/**
	 * 消耗金额
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
	 * 礼品卡使用卡券编号，多个用逗号分隔
	 */
	private String ticketId;
	/**
	 * 优惠券使用数组，多个用逗号分隔
	 */
	private String coupons;
	/**
	 * 订单状态，0:未付款,1:已付款, 2:已退款,3:已评价'
	 */
	private int orderstatus;
	/**
	 * 支付方式,wx-微信,lpk-礼品卡菜品券,cash-礼品卡现金券
	 */
	private String payType;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 桌台编号
	 */
	private String tableId;
	/**
	 * 桌号名称
	 */
	private String tableName;
	/**
	 * 退款单号
	 */
	private String refundcode;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;

}
