package com.currency_prediction.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.currency_prediction.backend.model.CurrencyRate;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Integer> {
    List<CurrencyRate> findAll();

    @Query(nativeQuery = true, value = "select * from currency_rates where code in ('USD', 'EUR', 'JPY', 'THB') order by id desc limit 4")
    List<CurrencyRate> findLatestRecord();

    List<CurrencyRate> findByCode(String code);
}
