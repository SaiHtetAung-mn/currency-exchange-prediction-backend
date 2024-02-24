package com.currency_prediction.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.currency_prediction.backend.model.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    
}
