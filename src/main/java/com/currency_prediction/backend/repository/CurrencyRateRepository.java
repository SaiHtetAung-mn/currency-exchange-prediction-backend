package com.currency_prediction.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.currency_prediction.backend.model.CurrencyRate;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Integer> {
    List<CurrencyRate> findAll();

    @Query("select c from CurrencyRate c order by id desc limit 1")
    CurrencyRate findLatestRecord();
}
