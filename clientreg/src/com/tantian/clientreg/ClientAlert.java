package com.tantian.clientreg;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * �ͻ���ע�����
 */
public class ClientAlert implements Serializable {

	private static final long serialVersionUID = -2813120366138988480L;
	private String clientInfo;

	ClientAlert(String clientInfo) {
		this.clientInfo = clientInfo;
	}

	/*
	 * ���Ǹ÷����������ڲ���ʹ�á�
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + clientInfo + "ע�ᣡ";
	}

}
