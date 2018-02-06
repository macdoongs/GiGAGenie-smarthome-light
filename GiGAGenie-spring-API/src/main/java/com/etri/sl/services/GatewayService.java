package com.etri.sl.services;

import java.util.List;

import com.etri.sl.models.Gateway;

public interface GatewayService {
	Gateway findByGid(int gid);
	
	void saveGateway(Gateway gateway);
	
	void updateGateway(Gateway gateway);
	
	void deleteGatewayByGid(int gid);
	
	List<Gateway> findAllGateways();
	
	void deleteAllGateways();
	
	boolean isGatewayExist(Gateway gateway);
}
