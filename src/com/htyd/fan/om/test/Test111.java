package com.htyd.fan.om.test;

import com.htyd.fan.om.service.OMService;

import android.test.AndroidTestCase;

public class Test111 extends AndroidTestCase {

	public int  add(int i ,int j){
		return i+j;
	}

	public void testAdd(){
		assertEquals(4, OMService.add(1, 2));
	}
	
}
