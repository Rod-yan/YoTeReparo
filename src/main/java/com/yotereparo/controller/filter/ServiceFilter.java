package com.yotereparo.controller.filter;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

@Component
public class ServiceFilter {
	
	public static final String[] FILTERS = {"user", "district", "city"};
	
	public Boolean contains(String key) {
		if (key == null || key.isEmpty())
			return false;
		for (String filter : FILTERS)
			if (filter.equalsIgnoreCase(key))
				return true;
		return false;
	}
	
	public Boolean contains(Map<String,String> map) {
		if (map == null || map.isEmpty())
			return false;
		for (Entry<String, String> entry : map.entrySet())
			if (!this.contains(entry.getKey()))
				return false;
		return true;
	}
}
