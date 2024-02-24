package com.currency_prediction.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.currency_prediction.backend.model.CurrencyRate;
import com.currency_prediction.backend.repository.CurrencyRateRepository;

@RestController
@RequestMapping("/api/currency-rates")
@CrossOrigin(origins = "*")
public class CurrencyRateController {
    @Autowired
    CurrencyRateRepository currencyRateRepo;
    
    @GetMapping("/")
    public List<CurrencyRate> getAllRate() {
        return this.currencyRateRepo.findAll();
    }

    @GetMapping("/latest")
    public CurrencyRate getLatestRate() {
        return this.currencyRateRepo.findLatestRecord();
    }
}
