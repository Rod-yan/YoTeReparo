package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.District;

public interface DistrictDao {
	
	District getDistrictById(Integer id);
	 
	List<District> getAllDistricts();
}
