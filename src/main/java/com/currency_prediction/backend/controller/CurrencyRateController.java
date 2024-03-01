package com.currency_prediction.backend.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.currency_prediction.backend.model.CurrencyRate;
import com.currency_prediction.backend.repository.CurrencyRateRepository;
import com.currency_prediction.backend.service.CurrencyRateService;

@RestController
@RequestMapping("/api/currency-rates")
@CrossOrigin(origins = "*")
public class CurrencyRateController {
    @Autowired
    CurrencyRateRepository currencyRateRepo;

    @Autowired
    CurrencyRateService currencyRateService;
    
    @GetMapping("/")
    public List<CurrencyRate> getAllRate() {
        return this.currencyRateRepo.findAll();
    }

    @GetMapping("/latest")
    public List<CurrencyRate> getLatestRate() {
        return this.currencyRateRepo.findLatestRecord();
    }

    @GetMapping("/predict")
    public double predictExchangeRate(@RequestParam("date") String date, @RequestParam("currency") String currency) {
        double result = 1.0;
        try {
            result = this.currencyRateService.predictExchangeRate2(currency, LocalDate.parse(date));
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
