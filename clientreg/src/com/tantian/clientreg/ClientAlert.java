package com.tantian.clientreg;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * 客户端注册对象
 */
public class ClientAlert implements Serializable {

	private static final long serialVersionUID = -2813120366138988480L;
	private String clientInfo;

	ClientAlert(String clientInfo) {
		this.clientInfo = clientInfo;
	}

	/*
	 * 覆盖该方法，仅用于测试使用。
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + clientInfo + "注册！";
	}

}
