package com.tantian.clientreg;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * ά�����ӵ���Ϣ������������
 */
public class KeepAlive implements Serializable {

	private static final long serialVersionUID = -2813120366138988480L;
	private String reg = "";

	public void setReg(String reg) {
		this.reg = reg;
	}

	/*
	 * ���Ǹ÷����������ڲ���ʹ�á�
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\tά�����Ӱ�" + reg;
	}

}