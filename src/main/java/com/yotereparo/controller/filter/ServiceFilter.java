package com.yotereparo.controller.filter;

import org.springframework.stereotype.Component;

@Component
public class ServiceFilter extends AbstractFilter {
	
	public ServiceFilter() {
		super.filters = new String[] {"user", "city", "district", "title", "description"};
	}
}
