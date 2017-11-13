package com.i2mago.controller;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *@author Karl.Qin
 *@version 创建时间：2017年10月17日 下午6:15:35
 */

@SpringBootApplication
@MapperScan("com.i2mago.mapper")
public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
