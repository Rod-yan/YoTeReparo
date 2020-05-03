package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.Contract;
import com.yotereparo.model.Quote;

public interface ContractService {
    
    void createContract(Quote quote);
    
    void refreshContractStatus(Contract contract);
    
    void setContractAsFinishedById(Integer id);
    
    void customerCancelsContractById(Integer id);
    
    void providerCancelsContractById(Integer id);
    
    void rateContractById(Integer id, Integer rate, String description);
    
    void archiveContractById(Integer id);
    	
    void deleteContractById(Integer id);
    
    Contract getContractById(Integer id);
 
    List<Contract> getAllContracts();
}