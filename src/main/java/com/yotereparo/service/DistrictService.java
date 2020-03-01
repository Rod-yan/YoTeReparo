package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.District;

public interface DistrictService {
 
    List<District> getAllDistricts(); 
     
    District getDistrictById(Integer id);
}
