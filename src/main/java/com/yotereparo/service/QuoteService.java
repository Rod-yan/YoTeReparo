package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;

public interface QuoteService {
    
    void createQuote(Quote quote);
     
    void updateQuote(Quote quote);
    
    void customerAcceptsQuoteById(Integer id);
    
    void customerRejectsQuoteById(Integer id);
    
    void providerRejectsQuoteById(Integer id);
    
    void archiveQuoteById(Integer id);
    
    void deleteQuoteById(Integer id);
     
    Quote getQuoteById(Integer id);
    
    Boolean activeQuoteExistBetween(User user, Service service);
    
    Boolean quoteExistBetween(User user, Service service);
    
    List<Quote> getAllQuotes();
}
