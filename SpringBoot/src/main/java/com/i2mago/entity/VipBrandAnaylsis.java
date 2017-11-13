package com.i2mago.entity;

import java.util.Date;

/**
 *@author Karl.Qin
 *@version 创建时间：2017年10月17日 下午6:19:16
 *唯品会品牌综合分析实体
 */
public class VipBrandAnaylsis {
	private String brandId;//品牌Id
	private String brandName;//品牌名称
	private Date dataTime;//日期
	private Float totalAmount;//销售总额
	private Integer totalQuantity;//总销售量
	private Integer uv;
	private Float coverRate;//转化率
	private Integer buyerNum;//买家人数
	private Float MORate;//移动端占比
	
	
	public String getBrandId() {
		return brandId;
	}
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public Date getDataTime() {
		return dataTime;
	}
	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}
	public Float getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Integer getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public Integer getUv() {
		return uv;
	}
	public void setUv(Integer uv) {
		this.uv = uv;
	}
	public Float getCoverRate() {
		return coverRate;
	}
	public void setCoverRate(Float coverRate) {
		this.coverRate = coverRate;
	}
	public Integer getBuyerNum() {
		return buyerNum;
	}
	public void setBuyerNum(Integer buyerNum) {
		this.buyerNum = buyerNum;
	}
	public Float getMORate() {
		return MORate;
	}
	public void setMORate(Float mORate) {
		MORate = mORate;
	}
	
	
	
}
