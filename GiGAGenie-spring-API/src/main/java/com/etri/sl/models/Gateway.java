package com.etri.sl.models;

public class Gateway {
	private int gid;
	
	private String gpid;
	
	private String productCode;
	
	private String serialNumber;

	private String iblid;
	
	private String ip;
	
	private String tcpPort;
	
	public Gateway() {
		gid = 0;
	}

	public Gateway(int gid, String ip, String port) {
		this.gid = gid;
		this.ip = ip;
		this.tcpPort = port;
	}
	
	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public String getGpid() {
		return gpid;
	}

	public void setGpid(String gpid) {
		this.gpid = gpid;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(String tcpPort) {
		this.tcpPort = tcpPort;
	}
	
	
}
