package com.drag.tb1906.store.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.drag.tb1906.common.BaseResponse;
import com.drag.tb1906.store.form.OrderCommentForm;
import com.drag.tb1906.store.form.OrderInfoForm;
import com.drag.tb1906.store.form.OrderRemindForm;
import com.drag.tb1906.store.resp.OrderResp;
import com.drag.tb1906.store.resp.RemindResp;
import com.drag.tb1906.store.service.OrderService;
import com.drag.tb1906.store.vo.OrderCommentVo;
import com.drag.tb1906.store.vo.OrderDetailVo;
import com.drag.tb1906.store.vo.OrderInfoVo;
import com.drag.tb1906.store.vo.OrderRemindVo;




@RestController
@RequestMapping(value = "/tb1906/order")
public class OrderController {

	@Autowired
	private OrderService orderInfoService;
	
	/**
	 * 堂食购买下单
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/purchase", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<OrderResp> purchase(@RequestBody OrderInfoForm form) {
		OrderResp detailVo = orderInfoService.purchase(form);
		return new ResponseEntity<OrderResp>(detailVo, HttpStatus.OK);
	}
	
	
	/**
	 * 获取我的订单
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/myorders", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<OrderInfoVo>> myorders(@RequestParam(required = true)  String openid,@RequestParam String type) {
		List<OrderInfoVo> list = orderInfoService.myOrders(openid,type);
		return new ResponseEntity<List<OrderInfoVo>>(list, HttpStatus.OK);
	}
	
	/**
	 * 订单详情
	 * @param orderid
	 * @return
	 */
	@RequestMapping(value = "/orderdetail", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<OrderDetailVo>> orderDetail(@RequestParam(required = true)  String orderid) {
		List<OrderDetailVo> list = orderInfoService.orderDetail(orderid);
		return new ResponseEntity<List<OrderDetailVo>>(list, HttpStatus.OK);
	}
	
	/**
	 * 申请退款
	 * @param orderid
	 * @return
	 */
	@RequestMapping(value = "/applyreturn", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<BaseResponse> applyReturn(@RequestParam(required = true)  String orderid) {
		BaseResponse resp = orderInfoService.applyReturn(orderid);
		return new ResponseEntity<BaseResponse>(resp, HttpStatus.OK);
	}
	
	/**
	 * 堂食新增评论
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/comment", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<OrderResp> comment(@RequestBody OrderCommentForm form) {
		OrderResp detailVo = orderInfoService.comment(form);
		return new ResponseEntity<OrderResp>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 商品评价集合
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value = "/commentlist", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<OrderCommentVo>> orderDetail(@RequestParam(required = true)  int goodsId) {
		List<OrderCommentVo> list = orderInfoService.goodsComment(goodsId);
		return new ResponseEntity<List<OrderCommentVo>>(list, HttpStatus.OK);
	}
	
	/**
	 * 预约
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/remind", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<RemindResp> remind(@RequestBody OrderRemindForm form) {
		RemindResp detailVo = orderInfoService.remind(form);
		return new ResponseEntity<RemindResp>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 修改预约
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/remindupdate", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<RemindResp> remindupdate(@RequestBody OrderRemindForm form) {
		RemindResp detailVo = orderInfoService.remindupdate(form);
		return new ResponseEntity<RemindResp>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 查询预约信息
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/remindquery", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<OrderRemindVo>> remindquery(@RequestParam String openid) {
		List<OrderRemindVo> detailVo = orderInfoService.remindQuery(openid);
		return new ResponseEntity<List<OrderRemindVo>>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 取消预约
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/remindcancel", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<RemindResp> remindcancel(@RequestParam int id) {
		RemindResp detailVo = orderInfoService.remindcancel(id);
		return new ResponseEntity<RemindResp>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 用户上传图片
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/picture", method = {RequestMethod.POST,RequestMethod.GET})
    public void uploadPicture(HttpServletRequest request, HttpServletResponse response) throws Exception {
		orderInfoService.uploadPicture(request, response);
	}
}
