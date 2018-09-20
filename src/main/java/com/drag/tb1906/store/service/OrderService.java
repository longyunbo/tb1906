package com.drag.tb1906.store.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drag.tb1906.common.BaseResponse;
import com.drag.tb1906.common.Constant;
import com.drag.tb1906.common.exception.AMPException;
import com.drag.tb1906.keruyun.service.KeruyunService;
import com.drag.tb1906.pay.resp.PayResp;
import com.drag.tb1906.store.dao.OrderCommentDao;
import com.drag.tb1906.store.dao.OrderDetailDao;
import com.drag.tb1906.store.dao.OrderInfoDao;
import com.drag.tb1906.store.dao.OrderRemindDao;
import com.drag.tb1906.store.dao.ProductInfoDao;
import com.drag.tb1906.store.entity.OrderComment;
import com.drag.tb1906.store.entity.OrderDetail;
import com.drag.tb1906.store.entity.OrderInfo;
import com.drag.tb1906.store.entity.OrderRemind;
import com.drag.tb1906.store.entity.ProductInfo;
import com.drag.tb1906.store.form.OrderCommentForm;
import com.drag.tb1906.store.form.OrderDetailForm;
import com.drag.tb1906.store.form.OrderInfoForm;
import com.drag.tb1906.store.form.OrderRemindForm;
import com.drag.tb1906.store.resp.OrderResp;
import com.drag.tb1906.store.resp.RemindResp;
import com.drag.tb1906.store.vo.OrderCommentVo;
import com.drag.tb1906.store.vo.OrderDetailVo;
import com.drag.tb1906.store.vo.OrderInfoVo;
import com.drag.tb1906.store.vo.OrderRemindVo;
import com.drag.tb1906.user.dao.UserDao;
import com.drag.tb1906.user.dao.UserTicketDetailDao;
import com.drag.tb1906.user.dao.UserTicketRecordDao;
import com.drag.tb1906.user.entity.User;
import com.drag.tb1906.user.entity.UserTicketDetail;
import com.drag.tb1906.user.entity.UserTicketRecord;
import com.drag.tb1906.user.service.UserTicketService;
import com.drag.tb1906.utils.BeanUtils;
import com.drag.tb1906.utils.DateUtil;
import com.drag.tb1906.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

	@Autowired
	private OrderInfoDao orderInfoDao;
	@Autowired
	private OrderCommentDao orderCommentDao;
	@Autowired
	private OrderDetailDao orderDetailDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ProductInfoDao productInfoDao;
	@Autowired
	private UserTicketService userTicketService;
	@Autowired
	private KeruyunService keruyunService;
	@Autowired
	private UserTicketDetailDao userTicketDetailDao;
	@Autowired
	UserTicketRecordDao userTicketRecordDao;
	@Autowired
	OrderRemindDao orderRemindDao;
	/**
	 * 堂食购买下单
	 * @param form
	 * @return
	 */
	@Transactional
	public OrderResp purchase(OrderInfoForm form) {
		log.info("【堂食下单传入参数】:{}",JSON.toJSONString(form));
		OrderResp resp = new OrderResp();
		try {
			String orderid = StringUtil.uuid();
			form.setOrderid(orderid);
			int goodsId = form.getGoodsId();
			String goodsName = form.getGoodsName();
			String type = form.getType();
			//支付类型,lpk-礼品卡,cash-现金券，wx-微信支付
			String payType = form.getPayType();
			//礼品卡使用卡券数组
			List<JSONObject> ticketJson = form.getTicketJson();
			//优惠券使用数组
			List<JSONObject> couponsJson = form.getCouponsJson();
			String outTradeNo = form.getOutTradeNo();
			//购买总数量
			int number = form.getNumber();
			//消耗总金额
			BigDecimal price = form.getPrice();
			BigDecimal tolprice = form.getTolprice();
			BigDecimal dicprice = form.getDicprice();
			String openid = form.getOpenid();
			String buyName = form.getBuyName();
			String phone = form.getPhone();
			User user = userDao.findByOpenid(openid);
			ProductInfo goods = productInfoDao.findGoodsDetail(goodsId);
			if(user != null) {
				int uid = user.getId();
				//验证参数
				resp = this.checkParam(user,goods,form);
				String returnCode = resp.getReturnCode();
				if(!returnCode.equals(Constant.SUCCESS)) {
					return resp;
				}
				//插入订单表
				OrderInfo order = new OrderInfo();
				order.setId(order.getId());
				order.setOrderid(orderid);
				order.setGoodsId(goodsId);
				order.setGoodsName(goodsName + "等商品");
				order.setGoodsImg(goods.getGoodsImgs());
				order.setType(type);
				order.setNumber(number);
				order.setTolprice(tolprice);
				order.setDicprice(dicprice);
				order.setPrice(price);
				order.setOrderstatus(OrderInfo.ORDERSTATUS_SUCCESS);
				order.setOutTradeNo(outTradeNo);
				order.setUid(uid);
				order.setBuyName(buyName);
				order.setPhone(phone);
				order.setRemark(form.getRemark());
				order.setTableId(form.getTableId());
				order.setTableName(form.getTableName());
				
				//选择礼品卡菜品券支付的
				if(payType.equals("lpk")) {
					List<String> ticketIds = new ArrayList<String>();
					for(JSONObject detail : ticketJson) {
						int jsoTicketId = detail.getInteger("ticketId");
						int JsonTicketNumber = detail.getInteger("ticketNumber");
						UserTicketDetail ticketDetail = userTicketDetailDao.findOne(jsoTicketId);
						int ticketNum = ticketDetail.getNumber();
						int newTicketNum =  ticketNum - JsonTicketNumber;
						if(JsonTicketNumber > ticketNum) {
							resp.setReturnCode(Constant.MONEY_NOTENOUGH);
							resp.setErrorMessage("卡券数量不足!");
							log.error("【卡券数量不足!】,ticketId:{}",jsoTicketId);
							return resp;
						}
						ticketDetail.setNumber(newTicketNum);
						userTicketDetailDao.saveAndFlush(ticketDetail);
						
						
						//卡券使用记录
						UserTicketRecord ticketRecord = new UserTicketRecord();
						ticketRecord.setId(ticketRecord.getId());
						ticketRecord.setGoodsId(ticketDetail.getGoodsId());
						ticketRecord.setTicketId(jsoTicketId);
						ticketRecord.setType(payType);
						ticketRecord.setTicketName(ticketDetail.getGoodsName());
						ticketRecord.setUid(ticketDetail.getUid());
						ticketRecord.setNumber(JsonTicketNumber);
						ticketRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
						userTicketRecordDao.save(ticketRecord);
						ticketIds.add(String.valueOf(jsoTicketId));
					}
					//传卡券详情id,多个卡券用逗号分隔
					order.setOutTradeNo(outTradeNo);
					String.join(",", ticketIds);
					order.setTicketId(StringUtil.listToString(ticketIds));
				}else if(payType.equals("cash")){
					List<String> ticketIds = new ArrayList<String>();
					List<String> couponsIds = new ArrayList<String>();
					//选择礼品卡现金支付的
					for(JSONObject detail : ticketJson) {
						int jsoTicketId = detail.getInteger("ticketId");
						BigDecimal jsonTicketPrice = detail.getBigDecimal("ticketPrice");
						
						UserTicketDetail ticketDetail = userTicketDetailDao.findOne(jsoTicketId);
						BigDecimal ticketPrice = ticketDetail.getPrice();
						
						BigDecimal newTicketPrice = ticketPrice.subtract(jsonTicketPrice);
						if(newTicketPrice.compareTo(BigDecimal.ZERO) < 0) {
							resp.setReturnCode(Constant.MONEY_NOTENOUGH);
							resp.setErrorMessage("卡券余额不足!");
							log.error("【卡券余额不足!】,ticketId:{}",jsoTicketId);
							return resp;
						}
						ticketDetail.setPrice(newTicketPrice);
						userTicketDetailDao.saveAndFlush(ticketDetail);
						
						//卡券使用记录
						UserTicketRecord ticketRecord = new UserTicketRecord();
						ticketRecord.setId(ticketRecord.getId());
						ticketRecord.setGoodsId(ticketDetail.getGoodsId());
						ticketRecord.setTicketId(jsoTicketId);
						ticketRecord.setTicketName(ticketDetail.getGoodsName());
						ticketRecord.setUid(ticketDetail.getUid());
						ticketRecord.setType(payType);
						ticketRecord.setNumber(1);
						ticketRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
						userTicketRecordDao.save(ticketRecord);
						ticketIds.add(String.valueOf(jsoTicketId));
					}
					//使用外卖的营销卡券，核销掉
					if(couponsJson != null && couponsJson.size() > 0) {
						for(JSONObject detail : couponsJson) {
							int jsoTicketId = detail.getInteger("ticketId");
							userTicketService.destoryTicket(jsoTicketId);
							couponsIds.add(String.valueOf(jsoTicketId));
						}
					}
					
					//传卡券详情id,多个卡券用逗号分隔
					order.setOutTradeNo(outTradeNo);
					String.join(",", ticketIds);
					String.join(",", couponsIds);
					order.setTicketId(StringUtil.listToString(ticketIds));
					order.setCoupons(StringUtil.listToString(couponsIds));
				}else {
					List<String> ticketIds = new ArrayList<String>();
					//使用外卖的营销卡券，核销掉
					if(couponsJson != null && couponsJson.size() > 0) {
						for(JSONObject detail : couponsJson) {
							int jsoTicketId = detail.getInteger("ticketId");
							userTicketService.destoryTicket(jsoTicketId);
							ticketIds.add(String.valueOf(jsoTicketId));
						}
					}
					//微信支付
					order.setOutTradeNo(outTradeNo);
					String.join(",", ticketIds);
					order.setCoupons(StringUtil.listToString(ticketIds));
				}
				order.setPayType(payType);
				
				
				order.setCreateTime(new Timestamp(System.currentTimeMillis()));
				order.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				orderInfoDao.save(order);
				
				List<OrderDetailForm> orderList = form.getOrderDetail();
				Set<Integer> ids = new HashSet<Integer>();
				if(orderList != null && orderList.size() > 0) {
					for(OrderDetailForm detail : orderList) {
						//插入订单详情
						int dGoodsId = detail.getGoodsId();
						String dGoodsName = detail.getGoodsName();
						String dNorms = detail.getNorms();
						int dNumber = detail.getNumber();
						BigDecimal dPrice  = detail.getPrice();
						OrderDetail orderDetail = new OrderDetail();
						orderDetail.setId(orderDetail.getId());
						orderDetail.setUid(uid);
						orderDetail.setOrderid(orderid);
						orderDetail.setGoodsId(dGoodsId);
						orderDetail.setGoodsName(dGoodsName);
						orderDetail.setNorms(dNorms);
						orderDetail.setPrice(dPrice);
						orderDetail.setNumber(dNumber);
						orderDetail.setType(type);
						orderDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
						orderDetail.setUpdateTime(new Timestamp(System.currentTimeMillis()));
						ids.add(dGoodsId);
						orderDetailDao.save(orderDetail);
					}
				}else {
					resp.setReturnCode(Constant.ORDERNOTEXISTS);
					resp.setErrorMessage("订单详情不存在，请添加商品!");
					log.error("【堂食商品下单订单参数错误】,{}",JSON.toJSONString(orderList));
					return resp;
				}
				
				
				PayResp payResp = keruyunService.createDinnerOrder(form);
				if(!Constant.SUCCESS.equals(payResp.getReturnCode())) {
					resp.setReturnCode(payResp.getReturnCode());
					resp.setErrorMessage(payResp.getErrorMessage());
					log.error("【客如云堂食下单，异常】payResp:{}",JSON.toJSONString(payResp));
					return resp;
				}
				
				String kryorderId = payResp.getOrderId();
				order.setKryorderid(kryorderId);
				orderInfoDao.save(order);
				
				//新增购买人数次数
				this.addSuccTimes(ids);
				
				resp.setReturnCode(Constant.SUCCESS);
				resp.setOrderId(orderid);
				resp.setErrorMessage("下单成功!");
				
			}
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("下单异常!");
		}
		return resp;
	}
	
	
	
	
	/**
	 * 订单详情
	 * @param orderid
	 * @return
	 */
	public List<OrderDetailVo> orderDetail(String orderid){
		log.info("【订单详情传入参数】:{}", orderid);
		List<OrderDetailVo> orderResp = new ArrayList<OrderDetailVo>();
		List<OrderDetail> details = orderDetailDao.findByOrderId(orderid);
		
		List<ProductInfo> products = productInfoDao.findAll();
		Map<Integer,ProductInfo> proMap = new HashMap<Integer,ProductInfo>();
		if(products != null && products.size() > 0) {
			for(ProductInfo pro : products) {
				proMap.put(pro.getGoodsId(), pro);
			}
		}
		if(details != null && details.size() > 0) {
			for (OrderDetail order : details) {
				OrderDetailVo vo = new OrderDetailVo();
				BeanUtils.copyProperties(order, vo,new String[]{"createTime", "updateTime"});
				int goodsid = order.getGoodsId();
				vo.setGoodsThumb(proMap.get(goodsid).getGoodsThumb());
				vo.setCreateTime((DateUtil.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				vo.setUpdateTime((DateUtil.format(order.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
				orderResp.add(vo);
			}
		}
		return orderResp;
	}
	
	/**
	 * 获取个人订单
	 * @param openid
	 * @return
	 */
	public List<OrderInfoVo> myOrders(String openid,String type){
		log.info("【我的订单传入参数】:{}", openid);
		List<OrderInfoVo> orderResp = new ArrayList<OrderInfoVo>();
		User user = userDao.findByOpenid(openid);
		if(user != null) {
			int uid = user.getId();
			List<OrderInfo> orderList = null;
			if(!StringUtil.isEmpty(type)) {
				orderList = orderInfoDao.findByUidAndType(uid,type);
			}else {
				orderList = orderInfoDao.findByUid(uid);
			}
			for (OrderInfo order : orderList) {
				OrderInfoVo vo = new OrderInfoVo();
				BeanUtils.copyProperties(order, vo,new String[]{"createTime", "updateTime","billTime"});
				vo.setCreateTime((DateUtil.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				vo.setUpdateTime((DateUtil.format(order.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
				orderResp.add(vo);
			}
		}
		return orderResp;
	}
	
	/**
	 * 申请退款
	 * @param orderid
	 * @return
	 */
	public BaseResponse applyReturn(String orderid){
		log.info("【申请退款传入参数】:orderid{}", orderid);
		BaseResponse resp = new BaseResponse();
		OrderInfo order = orderInfoDao.findByOrderId(orderid);
		if(order != null) {
			order.setOrderstatus(OrderInfo.ORDERSTATUS_RETURN_ON);
			orderInfoDao.saveAndFlush(order);
			resp.setReturnCode(Constant.SUCCESS);
			resp.setErrorMessage("申请退款成功!");
		}else {
			resp.setReturnCode(Constant.ORDERNOTEXISTS);
			resp.setErrorMessage("订单不存在!");
		}
		return resp;
	}
	
	
	/**
	 * 堂食评论
	 * @param form
	 * @return
	 */
	@Transactional
	public OrderResp comment(OrderCommentForm form) {
		log.info("【堂食评价传入参数】:{}",JSON.toJSONString(form));
		OrderResp resp = new OrderResp();
		try {
			String orderid = form.getOrderid();
			int commentstatus = form.getCommentstatus();
			int commentlevel = form.getCommentlevel();
			String comment = form.getComment();
			String commentimg =form.getCommentimg();
			int goodsId = form.getGoodsId();
			String goodsName = form.getGoodsName();
			String openid = form.getOpenid();
			User user = userDao.findByOpenid(openid);
			int uid = user.getId();
			
			OrderComment order = new OrderComment();
			order.setId(order.getId());
			order.setUid(uid);
			order.setOrderid(orderid);
			order.setGoodsId(goodsId);
			order.setGoodsName(goodsName);
			order.setCommentstatus(commentstatus);
			order.setCommentlevel(commentlevel);
			order.setCommentimg(commentimg);
			order.setComment(comment);
			order.setCreateTime(new Timestamp(System.currentTimeMillis()));
			orderCommentDao.saveAndFlush(order);
			resp.setReturnCode(Constant.SUCCESS);
			resp.setErrorMessage("评价成功!");
				
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("评价异常!");
		}
		return resp;
	}
	
	/**
	 * 查询商品评价
	 * @param goodsId
	 * @return
	 */
	public List<OrderCommentVo> goodsComment(int goodsId){
		log.info("【商品评价查询传入参数】:{}", goodsId);
		List<OrderCommentVo> orderResp = new ArrayList<OrderCommentVo>();
		List<OrderComment> list =  orderCommentDao.findByGoodsId(goodsId);
		if(list != null && list.size() > 0) {
			for(OrderComment com : list) {
				OrderCommentVo vo = new OrderCommentVo();
				BeanUtils.copyProperties(com, vo,new String[]{"createTime"});
				vo.setCreateTime((DateUtil.format(com.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				orderResp.add(vo);
			}
		}
		return orderResp;
	}
	
	
	/**
	 * 排队预约提交
	 * @param form
	 * @return
	 */
	@Transactional
	public RemindResp remind(OrderRemindForm form) {
		log.info("【排队预约传入参数】:{}",JSON.toJSONString(form));
		RemindResp resp = new RemindResp();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(form.getOrderTime());
			String dateStr = DateUtil.format(date, "yyyy-MM-dd");
			OrderRemind order = new OrderRemind();
			order.setId(order.getId());
			String number =  orderRemindDao.findMaxNumber(dateStr);
			if(StringUtil.isEmpty(number)) {
				order.setNumber(1);
			}else {
				order.setNumber(Integer.parseInt(number) + 1);
			}
			BeanUtils.copyProperties(form, order,new String[]{"orderTime"});
			order.setOrderTime(date);
			order.setStatus(OrderRemind.ORDERSTATUS_ON);
			order.setRemindstatus(OrderRemind.REMINDSTATUS_YES);
			order.setSendstatus(OrderRemind.SENDSTATUS_NO);
			order.setCreateTime(new Timestamp(System.currentTimeMillis()));
			orderRemindDao.save(order);
			
			resp.setNumber(order.getNumber());
			resp.setReturnCode(Constant.SUCCESS);
			resp.setErrorMessage("预约成功!");
				
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("评价异常!");
		}
		return resp;
	}
	
	/**
	 * 预约修改
	 * @param form
	 * @return
	 */
	@Transactional
	public RemindResp remindupdate(OrderRemindForm form) {
		log.info("【排队预约修改传入参数】:{}",JSON.toJSONString(form));
		RemindResp resp = new RemindResp();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(form.getOrderTime());
			String dateStr = DateUtil.format(date, "yyyy-MM-dd");
			int id = form.getId();
			
			OrderRemind orderRemind =  orderRemindDao.findOne(id);
			if(orderRemind != null) {
				String number =  orderRemindDao.findMaxNumber(dateStr);
				if(StringUtil.isEmpty(number)) {
					orderRemind.setNumber(1);
				}else {
					orderRemind.setNumber(Integer.parseInt(number) + 1);
				}
				BeanUtils.copyProperties(form, orderRemind,new String[]{"orderTime"});
				orderRemind.setOrderTime(date);
				orderRemind.setStatus(OrderRemind.ORDERSTATUS_ON);
				orderRemind.setRemindstatus(OrderRemind.REMINDSTATUS_YES);
				orderRemind.setSendstatus(OrderRemind.SENDSTATUS_NO);
				orderRemind.setCreateTime(new Timestamp(System.currentTimeMillis()));
				orderRemindDao.saveAndFlush(orderRemind);
			}
			
			resp.setNumber(orderRemind.getNumber());
			resp.setReturnCode(Constant.SUCCESS);
			resp.setErrorMessage("预约修改成功!");
				
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("评价异常!");
		}
		return resp;
	}
	
	/**
	 * 取消预约
	 * @param id
	 * @return
	 */
	@Transactional
	public RemindResp remindcancel(int id) {
		log.info("【排队预约取消传入参数】id:{}",JSON.toJSONString(id));
		RemindResp resp = new RemindResp();
		try {
			OrderRemind orderRemind =  orderRemindDao.findOne(id);
			if(orderRemind != null) {
				orderRemind.setRemindstatus(OrderRemind.REMINDSTATUS_NO);
				orderRemind.setStatus(OrderRemind.ORDERSTATUS_CANCEL);
				orderRemindDao.saveAndFlush(orderRemind);
			}
			resp.setReturnCode(Constant.SUCCESS);
			resp.setErrorMessage("取消预约成功!");
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("预约异常!");
		}
		return resp;
	}
	
	/**
	 * 根据openid获取预约信息
	 * @param openid
	 * @return
	 */
	public List<OrderRemindVo> remindQuery(String openid) {
		log.info("【预约信息查询传入参数】openid:{}", openid);
		List<OrderRemindVo> orderResp = new ArrayList<OrderRemindVo>();
		List<OrderRemind> list = orderRemindDao.findByOpenid(openid);
		if (list != null && list.size() > 0) {
			for (OrderRemind com : list) {
				OrderRemindVo vo = new OrderRemindVo();
				BeanUtils.copyProperties(com, vo, new String[] {"createTime","orderTime"});
				vo.setCreateTime((DateUtil.format(com.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				vo.setOrderTime((DateUtil.format(com.getOrderTime(), "yyyy-MM-dd HH:mm:ss")));
				orderResp.add(vo);
			}
		}
		return orderResp;
	}
	
	
	/**
	 * 用户上传图片
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void uploadPicture(HttpServletRequest request, HttpServletResponse response) throws Exception {
	        //获取文件需要上传到的路径
	        File directory = new File("../tb1906");
	        String path = directory.getCanonicalPath() + "/upload/";
	        String companyname = request.getParameter("name").toString();
	        // 判断存放上传文件的目录是否存在（不存在则创建）
	        File dir = new File(path);
	        if (!dir.exists()) {
	            dir.mkdir();
	        }
	        log.debug("path=" + path);
	        request.setCharacterEncoding("utf-8"); //设置编码
	        JSONArray jsonArray = new JSONArray();
	        try {
	            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
	            Iterator<String> iterator = req.getFileNames();
	            while (iterator.hasNext()) {
	                HashMap<String, Object> res = new HashMap<String, Object>();
	                MultipartFile file = req.getFile(iterator.next());
	                // 获取文件名
	                String fileNames = file.getOriginalFilename();
	                int split = fileNames.lastIndexOf(".");
	                //获取上传文件的后缀
	                String extName = fileNames.substring(split + 1, fileNames.length());
	                //组成新的图片名称
	                String newName = companyname + "." + extName;
	                String destPath = path + newName;
	                log.debug("destPath=" + destPath);
	 
	                //真正写到磁盘上
	                File file1 = new File(destPath);
	                OutputStream out = new FileOutputStream(file1);
	                out.write(file.getBytes());
	                res.put("url", destPath);
	                jsonArray.add(res);
	 
	                out.close();
	            }
	        } catch (Exception e) {
	            log.error("", e);
	        }
	 
	        PrintWriter printWriter = response.getWriter();
	        response.setContentType("application/json");
	        response.setCharacterEncoding("utf-8");
	        printWriter.write(JSON.toJSONString(jsonArray));
	        printWriter.flush();
	 
	    }
	
	
	
	/**
	 * 增加购买次数
	 * @param goods
	 * @param number
	 */
	public void addSuccTimes(Set<Integer> ids) {
		productInfoDao.updateSuccTimes(ids);
	}
	
	
	/**
	 * 验证参数
	 * @param user
	 * @param goods
	 * @param form
	 * @return
	 */
	public OrderResp checkParam(User user,ProductInfo goods,OrderInfoForm form) {
		OrderResp resp = new OrderResp();
		int goodsId = form.getGoodsId();
		String openid = form.getOpenid();
		if(user != null) {
			String phone = user.getMobile();
			String realName = user.getRealname();
			if(StringUtil.isEmpty(phone) && StringUtil.isEmpty(realName)) {
				resp.setReturnCode(Constant.USERINFO_OVER);
				resp.setErrorMessage("用户信息不完善!");
				log.error("【用户信息不完善】，openid={}",openid);
				return resp;
			}
		}else {
			resp.setReturnCode(Constant.USERNOTEXISTS);
			resp.setErrorMessage("用户不存在!");
			log.error("【用户不存在】，openid={}",openid);
			return resp;
		}
		if(goods == null) {
			resp.setReturnCode(Constant.PRODUCTNOTEXISTS);
			resp.setErrorMessage("商品不存在!");
			log.error("【商品不存在】，goodsId={}",goodsId);
			return resp;
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("验证通过！");
		return resp;
	}
	
}
