package com.fourteen.outersource;
/**
 * 网络状态变化:OnNetworkStatusChanged类
 */
public interface OnNetworkStatusChanged {

	/**
	 * 
	 * @param status
	 * when network status changed, do operation
	 */
	public void networkStatusChanged(int status);

}
