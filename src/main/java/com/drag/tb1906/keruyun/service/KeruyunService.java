package com.drag.tb1906.keruyun.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drag.tb1906.common.Constant;
import com.drag.tb1906.common.exception.AMPException;
import com.drag.tb1906.keruyun.util.SignUtil;
import com.drag.tb1906.pay.resp.PayResp;
import com.drag.tb1906.store.form.OrderDetailForm;
import com.drag.tb1906.store.form.OrderInfoForm;
import com.drag.tb1906.utils.HttpsUtil;
import com.drag.tb1906.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KeruyunService {
	
	private static String keruyunurl;
	private static String shopIdenty;
	private static String appKey;
	private static String secretKey;
	
	@Value("${keruyun.url.appKey}")
    public void seAppKey(String value) {
		appKey = value;
    }
	@Value("${keruyun.url.secretKey}")
    public void setSecretKey(String value) {
		secretKey = value;
    }
	@Value("${keruyun.url.url}")
    public void setKeruyunurl(String value) {
		keruyunurl = value;
    }
	@Value("${keruyun.url.shopIdenty}")
    public void setShopIdenty(String value) {
		shopIdenty = value;
    }
	
	
	
	
	/**
	 * 堂食下单
	 * @param form
	 * @return
	 */
	public PayResp createDinnerOrder(OrderInfoForm form) {
		PayResp resp = new PayResp();
		try {
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/dinner/order/create?%s",keruyunurl,commParam);
			log.info("【客如云正餐下单传入参数】:PayForm= {}",JSON.toJSONString(form));
			BigDecimal price = form.getPrice();
			String tpOrderId = StringUtil.uuid();
			List<OrderDetailForm> productList = form.getOrderDetail();
			int peopleCount = form.getPeopleCount();
			//转换为分
			price = price.multiply(new BigDecimal(100));
			Date now = new Date(System.currentTimeMillis());
			JSONObject json = new JSONObject();
			json.put("tpOrderId", tpOrderId);
			json.put("shopIdenty", shopIdenty);
			json.put("shopName", "泰北1906");
			json.put("status", "2");
			json.put("productCategorySize", "1");
			//订单总价为元
			json.put("totalPrice", price);
			json.put("discountAmount", "0");
			json.put("userFee", price);
			json.put("createTime", now.getTime());
			json.put("peopleCount", peopleCount);
			
			JSONArray products = new JSONArray();
			JSONArray tables = new JSONArray();
			
			if(productList != null && productList.size() > 0) {
				for(OrderDetailForm detail : productList) {
					int dprice = detail.getPrice().intValue() * 100;
					int dtotalFee = (detail.getPrice().intValue() * detail.getNumber()) * 100;
					JSONObject jsonPro = new JSONObject();
					jsonPro.put("tpId", detail.getGoodsId());
					jsonPro.put("id", detail.getGoodsId());
					jsonPro.put("name", detail.getGoodsName());
					jsonPro.put("unit", "份");
					jsonPro.put("price", dprice);
					jsonPro.put("quantity", detail.getNumber());
					jsonPro.put("totalFee", dtotalFee);
					jsonPro.put("tableId", form.getTableId());
					products.add(jsonPro);
				}
			}
			
			
			JSONObject jsonTable = new JSONObject();
			jsonTable.put("tableId", form.getTableId());
			jsonTable.put("tableName", form.getTableName());
			tables.add(jsonTable);
			
			JSONArray customers = new JSONArray();
			JSONObject jsonCust = new JSONObject();
			jsonCust.put("id", "");
			jsonCust.put("phoneNumber", "");
			jsonCust.put("name", "1906消费客户");
			jsonCust.put("gender", "0");
			customers.add(jsonCust);
			
			json.put("products", products);
			json.put("tables", tables);
			json.put("customers", customers);
			
			//桌台开台
			this.openTable(form);
			
			log.info("【客如云正餐下单传入参数】:json={}",json);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云正餐下单返回参数】:result={}",httpresult);
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				int code = jsonResult.getInteger("code");
				String message = jsonResult.getString("message");
				JSONObject result = (JSONObject) jsonResult.get("result");
				if (code == 0) {
					//成功
					String orderId = result.getString("orderId");
					resp.setOrderId(orderId);
					resp.setReturnCode(Constant.SUCCESS);
					resp.setErrorMessage("客如云下单成功！");
					return resp;
				} else {
					resp.setReturnCode(String.valueOf(code));
					resp.setErrorMessage(message);
					log.error("【下单，异常】jsonResult:{}",JSON.toJSONString(jsonResult));
					return resp;
				}
			}
		} catch (Exception e) {
			log.error("【下单异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("下单成功!");
		return resp;
	}
	
	
	/**
	 * 桌台开台
	 * @param form
	 * @return
	 */
	public JSONObject openTable(OrderInfoForm form){
		JSONObject resp = new JSONObject();
		try {
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/dinner/order/table/open?%s",keruyunurl,commParam);
			JSONObject json = new JSONObject();
			json.put("shopIdenty", form.getShopIdenty());
			json.put("tableId", form.getTableId());
			json.put("tableName", form.getTableName());
			log.info("【客如云桌台开台传入参数】:json={}",json);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云桌台开台返回参数】:httpresult={}",httpresult);
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				resp = (JSONObject) jsonResult.get("result");
			}
		} catch (Exception e) {
			log.error("【桌台开台，异常】e:{}",e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
	}
	
	
	/**
	 * 桌台编号查询
	 * @return
	 */
	public JSONArray fetchTables(List<Long> ids){
		JSONArray resp = new JSONArray();
		try {
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/table/fetchTables?%s",keruyunurl,commParam);
			JSONObject json = new JSONObject();
			json.put("ids", ids);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云桌台开台查询返回参数】:httpresult={}",httpresult);
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				resp = (JSONArray) jsonResult.get("result");
			}
		} catch (Exception e) {
			log.error("【桌台开台查询，异常】e:{}",e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
	}
	
	
	/**
	 * 订单详情
	 * @param id
	 * @return
	 */
	public JSONObject detail(String id){
		JSONObject resp = new JSONObject();
		try {
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/dinner/order/detail?%s",keruyunurl,commParam);
			JSONObject json = new JSONObject();
			json.put("id", id);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云订单查询返回参数】:httpresult={}",httpresult);
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				resp = (JSONObject) jsonResult.get("result");
			}
		} catch (Exception e) {
			log.error("【订单查询查询，异常】e:{}",e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
	}
}
