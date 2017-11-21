/**
 * File Name: org.jeasypoi.bean.Reports.java

 * @Date:2017年12月5日下午2:20:47
 */
package org.jeasypoi.bean;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;

/**
 * File Name: org.jeasypoi.bean.Reports.java
 * 
 * @Date:2017年12月5日下午2:20:47
 */
@ExcelTarget("reports")
public class Reports {

	@Excel(name = "序号")
	private Long id;
	@Excel(name = "姓名")

	private String name;
	@Excel(name = "性别")

	private String sex;
	@Excel(name = "费用")

	private BigDecimal fee;
	@Excel(name = "描述")

	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return JSONObject.toJSONString(this);
	}
}
