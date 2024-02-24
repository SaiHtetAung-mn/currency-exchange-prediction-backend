package com.currency_prediction.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.currency_prediction.backend.model.Currency;
import com.currency_prediction.backend.repository.CurrencyRepository;

@RestController
@RequestMapping("/api/currencies")
@CrossOrigin(origins = "*")
public class CurrencyController {
    @Autowired
    CurrencyRepository currencyRepo;

    @GetMapping(path = "/", produces = "application/json")
    public @ResponseBody List<Currency> getCurrencyList() {
        List<Currency> currencies = this.currencyRepo.findAll();
        return currencies;
    }
}
