package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.Requirement;

public interface RequirementDao {
	
	Requirement getRequirementById(Integer id);
	
	Requirement getRequirementByDescription(String description);
	 
	List<Requirement> getAllRequirements();

}
