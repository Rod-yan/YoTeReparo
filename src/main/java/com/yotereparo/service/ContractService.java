package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.Contract;
import com.yotereparo.model.Quote;

public interface ContractService {
    
    void createContract(Quote quote);
    
    void refreshContractStatus(Contract contract);
    
    void setContractAsFinishedById(Integer contract);
    
    void customerCancelsContractById(Integer contract);
    
    void providerCancelsContractById(Integer contract);
    
    void rateContractById(Integer contractId, Integer rate, String description);
    
    void archiveContractById(Integer id);
    	
    void deleteContractById(Integer id);
    
    Contract getContractById(Integer id);
 
    List<Contract> getAllContracts();
}