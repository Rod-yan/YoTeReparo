package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.Contract;

public interface ContractDao {
	
	Contract getContractById(Integer id);
	 
    void createContract(Contract contract);
     
    void deleteContractById(Integer id);
    
    List<Contract> getAllContracts();
}
