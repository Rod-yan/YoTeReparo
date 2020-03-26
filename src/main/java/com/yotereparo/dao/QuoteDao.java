package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.Quote;

public interface QuoteDao {
	
	Quote getQuoteById(Integer id);
	 
    void createQuote(Quote quote);
     
    void deleteQuoteById(Integer id);
     
    List<Quote> getAllQuotes();
}
