package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.PaymentMethod;

public interface PaymentMethodService {
 
    List<PaymentMethod> getAllPaymentMethods(); 
     
    PaymentMethod getPaymentMethodById(Integer id);
    
    PaymentMethod getPaymentMethodByDescription(String description);
}
