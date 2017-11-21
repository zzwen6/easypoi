package org.jeasypoi.bean;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;



@ExcelTarget("financeReports")
public class FinanceReports {
	/**
	 * 主键
	 */
	@Excel(name = "序号")
	private String id;

	/**
	 * 交易流水号
	 */
	@Excel(name = "交易流水号")
	private String tradeCode;

	/**
	 * 产品代码
	 */
	@Excel(name = "产品代码")
	private String productCode;

	/**
	 * 名称
	 */
	@Excel(name = "名称")
	private String productName;

	/**
	 * 产品类型
	 */
	@Excel(name = "产品类型")
	private String type;

	/**
	 * 产品风险等级
	 */
	@Excel(name = "产品风险等级")
	// @Column(name = "p_risk_level")
	private String pRiskLevel;

	/**
	 * 客户姓名
	 */
	@Excel(name = "客户姓名")
	// @Column(name = "customer_name")
	private String customerName;

	/**
	 * 客户号
	 */
	@Excel(name = "客户号")
	// @Column(name = "customer_no")
	private String customerNo;

	/**
	 * 客户风险等级
	 */
	@Excel(name = "客户风险等级")
	// @Column(name = "c_risk_level")
	private String cRiskLevel;

	/**
	 * 产品适合度评估结果
	 */
	@Excel(name = "产品适合度评估结果")
	// @Column(name = "s_result")
	private String sResult;

	/**
	 * 客户经理号
	 */
	@Excel(name = "客户经理号")
	// @Column(name = "c_manager_no")
	private String cManagerNo;

	/**
	 * 业务种类
	 */
	@Excel(name = "业务种类")
	// @Column(name = "buss_type")
	private String bussType;

	/**
	 * 卡号/帐号
	 */
	@Excel(name = "卡号/帐号")
	// @Column(name = "bank_card_no")
	private String bankCardNo;

	/**
	 * 交易帐号
	 */
	@Excel(name = "交易帐号")
	// @Column(name = "trade_account_no")
	private String tradeAccountNo;

	/**
	 * TA帐号
	 */
	@Excel(name = "TA帐号")
	// @Column(name = "ta_no")
	private String taNo;

	/**
	 * 申请日期
	 */
	@Excel(name = "申请日期")
	// @Column(name = "apply_date")
	private String applyDate;

	/**
	 * 确认日期
	 */
	@Excel(name = "确认日期")
	// @Column(name = "confirm_date")
	private String confirmDate;

	/**
	 * 投资人类型
	 */
	@Excel(name = "投资人类型")
	// @Column(name = "investor_type")
	private String investorType;

	/**
	 * 手续费
	 */
	@Excel(name = "手续费",useStr=false)
	private Double fee;

	/**
	 * 申请份额
	 */
	@Excel(name = "申请份额",useStr=false)
	// @Column(name = "apply_share")
	private Double applyShare;

	/**
	 * 申请金额
	 */
	@Excel(name = "申请金额",useStr=false)
	// @Column(name = "apply_amount")
	private Double applyAmount;

	/**
	 * 确认份额
	 */
	@Excel(name = "确认份额",useStr=false)
	// @Column(name = "confirm_share")
	private Double confirmShare;

	/**
	 * 确认金额
	 */
	@Excel(name = "确认金额",useStr=false)
	// @Column(name = "confirm_amount")
	private Double confirmAmount;

	/**
	 * 投资收益
	 */
	@Excel(name = "投资收益",useStr=false)
	// @Column(name = "investment_income")
	private Double investmentIncome;

	/**
	 * 钞汇标识
	 */
	@Excel(name = "钞汇标识")
	// @Column(name = "anknbote_logo")
	private String anknboteLogo;

	/**
	 * 币种
	 */
	@Excel(name = "币种")
	private String currency;

	/**
	 * 交易状态
	 */
	@Excel(name = "交易状态")
	// @Column(name = "trading_status")
	private String tradingStatus;

	/**
	 * 失败原因
	 */
	@Excel(name = "失败原因")
	// @Column(name = "fail_reason")
	private String failReason;

	/**
	 * 交易支行名称
	 */
	@Excel(name = "交易支行名称")
	// @Column(name = "trade_bank_name")
	private String tradeBankName;

	/**
	 * 交易归属机构
	 */
	@Excel(name = "交易归属机构")
	// @Column(name = "trade_org")
	private String tradeOrg;

	/**
	 * 分行代码
	 */
	@Excel(name = "分行代码")
	// @Column(name = "b_bank_code")
	private String bBankCode;

	/**
	 * 分行名称
	 */
	@Excel(name = "分行名称")
	// @Column(name = "b_bank_name")
	private String bBankName;

	/**
	 * 渠道标志
	 */
	@Excel(name = "渠道标志")
	// @Column(name = "channel_sign")
	private String channelSign;

	/**
	 * 确认工号
	 */
	@Excel(name = "确认工号")
	// @Column(name = "confirmer_no")
	private String confirmerNo;

	/**
	 * 月份
	 */
	private String month;

	/**
	 * 创建人
	 */
	private String creator;

	/**
	 * 创建日期
	 */
	private Date createtime;

	/**
	 * 更新日期
	 */
	private Date updatetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getpRiskLevel() {
        return pRiskLevel;
    }

    public void setpRiskLevel(String pRiskLevel) {
        this.pRiskLevel = pRiskLevel;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getcRiskLevel() {
        return cRiskLevel;
    }

    public void setcRiskLevel(String cRiskLevel) {
        this.cRiskLevel = cRiskLevel;
    }

    public String getsResult() {
        return sResult;
    }

    public void setsResult(String sResult) {
        this.sResult = sResult;
    }

    public String getcManagerNo() {
        return cManagerNo;
    }

    public void setcManagerNo(String cManagerNo) {
        this.cManagerNo = cManagerNo;
    }

    public String getBussType() {
        return bussType;
    }

    public void setBussType(String bussType) {
        this.bussType = bussType;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getTradeAccountNo() {
        return tradeAccountNo;
    }

    public void setTradeAccountNo(String tradeAccountNo) {
        this.tradeAccountNo = tradeAccountNo;
    }

    public String getTaNo() {
        return taNo;
    }

    public void setTaNo(String taNo) {
        this.taNo = taNo;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getInvestorType() {
        return investorType;
    }

    public void setInvestorType(String investorType) {
        this.investorType = investorType;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getApplyShare() {
        return applyShare;
    }

    public void setApplyShare(Double applyShare) {
        this.applyShare = applyShare;
    }

    public Double getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(Double applyAmount) {
        this.applyAmount = applyAmount;
    }

    public Double getConfirmShare() {
        return confirmShare;
    }

    public void setConfirmShare(Double confirmShare) {
        this.confirmShare = confirmShare;
    }

    public Double getConfirmAmount() {
        return confirmAmount;
    }

    public void setConfirmAmount(Double confirmAmount) {
        this.confirmAmount = confirmAmount;
    }

    public Double getInvestmentIncome() {
        return investmentIncome;
    }

    public void setInvestmentIncome(Double investmentIncome) {
        this.investmentIncome = investmentIncome;
    }

    public String getAnknboteLogo() {
        return anknboteLogo;
    }

    public void setAnknboteLogo(String anknboteLogo) {
        this.anknboteLogo = anknboteLogo;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTradingStatus() {
        return tradingStatus;
    }

    public void setTradingStatus(String tradingStatus) {
        this.tradingStatus = tradingStatus;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getTradeBankName() {
        return tradeBankName;
    }

    public void setTradeBankName(String tradeBankName) {
        this.tradeBankName = tradeBankName;
    }

    public String getTradeOrg() {
        return tradeOrg;
    }

    public void setTradeOrg(String tradeOrg) {
        this.tradeOrg = tradeOrg;
    }

    public String getbBankCode() {
        return bBankCode;
    }

    public void setbBankCode(String bBankCode) {
        this.bBankCode = bBankCode;
    }

    public String getbBankName() {
        return bBankName;
    }

    public void setbBankName(String bBankName) {
        this.bBankName = bBankName;
    }

    public String getChannelSign() {
        return channelSign;
    }

    public void setChannelSign(String channelSign) {
        this.channelSign = channelSign;
    }

    public String getConfirmerNo() {
        return confirmerNo;
    }

    public void setConfirmerNo(String confirmerNo) {
        this.confirmerNo = confirmerNo;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
	/**
	 * 
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new JSONObject().toJSONString(this);
	}
}