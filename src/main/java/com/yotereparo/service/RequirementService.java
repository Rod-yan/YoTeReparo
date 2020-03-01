package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.Requirement;

public interface RequirementService {
	
	 List<Requirement> getAllRequirements(); 
     
	 Requirement getRequirementById(Integer id);
	    
	 Requirement getRequirementByDescription(String description);
}
