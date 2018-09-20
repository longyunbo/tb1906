package com.drag.tb1906.store.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drag.tb1906.utils.BeanUtils;
import com.drag.tb1906.utils.DateUtil;
import com.drag.tb1906.utils.StringUtil;
import com.drag.tb1906.store.dao.ProductInfoDao;
import com.drag.tb1906.store.entity.ProductInfo;
import com.drag.tb1906.store.vo.ProductInfoVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {

	@Autowired
	private ProductInfoDao productDao;

	/**
	 * 查询商品列表
	 * @return
	 */
	public List<ProductInfoVo> listGoods(String type,String orderby) {
		log.info("【堂食类商品列表查询传入参数】type = {},orderby = {}",type,orderby);
		List<ProductInfoVo> goodsResp = new ArrayList<ProductInfoVo>();
		List<ProductInfo> goodsList = new ArrayList<ProductInfo>();
		if(!StringUtil.isEmpty(orderby)) {
			if("desc".equals(orderby)) {
				goodsList = productDao.findByTypeOrderByPriceDesc(type);
			}else {
				goodsList = productDao.findByTypeOrderByPriceAsc(type);
			}
		}else {
			goodsList = productDao.findByTypeOrderBySuccTimesDesc(type);
		}
		if (goodsList != null && goodsList.size() > 0) {
			for (ProductInfo goods : goodsList) {
				ProductInfoVo resp = new ProductInfoVo();
				this.copyProperties(goods, resp);
				goodsResp.add(resp);
			}
		}
		return goodsResp;
	}
	
	/**
	 * 商品搜索
	 * @param name
	 * @return
	 */
	public List<ProductInfoVo> listGoodsByName(String name) {
		log.info("【堂食类商品列表查询传入参数】name = {}",name);
		List<ProductInfoVo> goodsResp = new ArrayList<ProductInfoVo>();
		List<ProductInfo> goodsList = new ArrayList<ProductInfo>();
		if(!StringUtil.isEmpty(name)) {
			goodsList = productDao.findByNameLike(name);
		}else {
			goodsList = productDao.findByTypeOrderBySuccTimesDesc("rx");
		}
		if (goodsList != null && goodsList.size() > 0) {
			for (ProductInfo goods : goodsList) {
				ProductInfoVo resp = new ProductInfoVo();
				this.copyProperties(goods, resp);
				goodsResp.add(resp);
			}
		}
		return goodsResp;
	}
	
	
	/**
	 * 查询商品详情
	 * @return
	 */
	public ProductInfoVo goodsDetail(int goodsId) {
		log.info("【堂食类商品详情查询传入参数】goodsId = {}",goodsId);
		ProductInfoVo detailVo = new ProductInfoVo();
		ProductInfo goods = productDao.findGoodsDetail(goodsId);
		if(goods != null) {
			this.copyProperties(goods, detailVo);
		}
		return detailVo;
	}
	
	/**
	 * 提取copy方法
	 * @param goods
	 * @param detailVo
	 */
	public void copyProperties(ProductInfo goods,ProductInfoVo detailVo) {
		BeanUtils.copyProperties(goods, detailVo,new String[]{"createTime", "updateTime"});
		detailVo.setCreateTime((DateUtil.format(goods.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
		detailVo.setUpdateTime((DateUtil.format(goods.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
	}
	
}
