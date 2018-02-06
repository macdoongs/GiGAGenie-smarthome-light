package com.etri.sl.models;

public class Device {
	private int did;
	
	private String dpid;
	
	private String productCode;
	
	private String serialNumber;

	private String iblid;

	public int getDid() {
		return did;
	}

	public void setDid(int did) {
		this.did = did;
	}

	public String getDpid() {
		return dpid;
	}

	public void setDpid(String dpid) {
		this.dpid = dpid;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getIblid() {
		return iblid;
	}

	public void setIblid(String iblid) {
		this.iblid = iblid;
	}
}
