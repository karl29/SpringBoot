package com.i2mago.entity;

import java.util.Date;

/**
 *@author Karl.Qin
 *@version ����ʱ�䣺2017��10��17�� ����6:19:16
 *ΨƷ��Ʒ���ۺϷ���ʵ��
 */
public class VipBrandAnaylsis {
	private String brandId;//Ʒ��Id
	private String brandName;//Ʒ������
	private Date dataTime;//����
	private Float totalAmount;//�����ܶ�
	private Integer totalQuantity;//��������
	private Integer uv;
	private Float coverRate;//ת����
	private Integer buyerNum;//�������
	private Float MORate;//�ƶ���ռ��
	
	
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
