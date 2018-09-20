package com.drag.tb1906.keruyun.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drag.tb1906.keruyun.service.KeruyunService;
import com.drag.tb1906.user.dao.UserDao;


@RestController
@RequestMapping(value = "/tb1906/keruyun")
public class KeruyunController {
	
	@Autowired
	KeruyunService keruyunService;
	@Autowired
	UserDao userDao;
	
	private final static Logger log = LoggerFactory.getLogger(KeruyunController.class);
	
	/**
	 * 查询客如云桌台号
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/fetchtables", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<JSONArray> fetchTables(@RequestParam List<Long> ids) {
		JSONArray Json = keruyunService.fetchTables(ids);
		return new ResponseEntity<JSONArray>(Json, HttpStatus.OK);
	}
	
	/**
	 * 订单详情
	 * @param orderid
	 * @return
	 */
	@RequestMapping(value = "/detail", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<JSONObject> detail(@RequestParam String orderid) {
		JSONObject Json = keruyunService.detail(orderid);
		return new ResponseEntity<JSONObject>(Json, HttpStatus.OK);
	}
}
