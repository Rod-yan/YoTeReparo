package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.PaymentMethod;

public interface PaymentMethodDao {
	
	PaymentMethod getPaymentMethodById(Integer id);
	
	PaymentMethod getPaymentMethodByDescription(String description);
	 
	List<PaymentMethod> getAllPaymentMethods();
}
