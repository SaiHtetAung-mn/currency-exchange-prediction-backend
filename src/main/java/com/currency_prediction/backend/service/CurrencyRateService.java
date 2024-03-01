package com.currency_prediction.backend.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.currency_prediction.backend.model.CurrencyRate;
import com.currency_prediction.backend.repository.CurrencyRateRepository;

import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;

@Service
public class CurrencyRateService {
    @Autowired
    private CurrencyRateRepository curRateRepo;

    public double predictExchangeRate(String currencyCode, Date futureDate) throws Exception {
        List<CurrencyRate> curRates = this.curRateRepo.findByCode(currencyCode);
        Instances instances = createInstances(curRates);

        LinearRegression model = new LinearRegression();
        model.buildClassifier(instances);

        // Create an instance for the prediction date
        DenseInstance instance = new DenseInstance(2);
        instance.setValue(0, futureDate.getTime());
        instances.add(instance);

        // Make the prediction
        double[] predictions = model.distributionForInstance(instances.lastInstance());

        return predictions[0]; // Assuming the exchange rate is the first attribute
    }

    private Instances createInstances(List<CurrencyRate> data) {
        // Define attributes
        Attribute dateAttribute = new Attribute("date");
        Attribute exchangeRateAttribute = new Attribute("rate");

        // Create Instances object
        Instances instances = new Instances("ExchangeRatePrediction", new ArrayList(List.of(dateAttribute, exchangeRateAttribute)), data.size());

        // Add instances to the dataset
        for (CurrencyRate rate : data) {
            DenseInstance instance = new DenseInstance(2);

            Date date;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Adjust format accordingly
                sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone to UTC
                date = sdf.parse(rate.getDate());
            } catch (ParseException e) {
                e.printStackTrace(); // Handle parsing exception
                continue; // Skip to the next iteration
            }

            instance.setValue(dateAttribute, date.getTime());
            instance.setValue(exchangeRateAttribute, rate.getRate());
            instances.add(instance);
        }

        // Set class index
        instances.setClassIndex(instances.numAttributes() - 1);

        return instances;
    }

    public double predictExchangeRate2(String currencyCode, LocalDate futureDate) {
        try {
            // Get historical exchange rate data for the specified currency code
            List<CurrencyRate> historicalData = this.curRateRepo.findByCode(currencyCode);

            if (historicalData.isEmpty()) {
                throw new RuntimeException("No historical data found for the specified currency code.");
            }

            // Load historical data into Weka Instances
            Instances wekaInstances = loadWekaInstances(historicalData);

            // Build and train linear regression model
            LinearRegression model = new LinearRegression();
            model.buildClassifier(wekaInstances);

            // Prepare attributes for the future date
            Attribute daysSinceEpoch = new Attribute("days_since_epoch");
            Instance futureInstance = new DenseInstance(1);
            int attributeIndex = wekaInstances.attribute("days_since_epoch").index();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Adjust format accordingly
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone to UTC
            LocalDate startDate = sdf.parse(historicalData.get(0).getDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = futureDate;

            long daysBetween = ChronoUnit.DAYS.between(startDate, currentDate);

            futureInstance.setValue(attributeIndex, daysBetween);

            // Predict exchange rate for the future date
            double predictedRate = model.classifyInstance(futureInstance);
            return predictedRate;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while predicting exchange rate: " + e.getMessage());
        }
    } 

    private Instances loadWekaInstances(List<CurrencyRate> historicalData) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("days_since_epoch"));
        attributes.add(new Attribute("rate"));

        Instances instances = new Instances("exchange_rates", attributes, historicalData.size());
        instances.setClassIndex(instances.numAttributes() - 1);

        // Add historical data to Instances
        for (CurrencyRate rate : historicalData) {
            Instance instance = new DenseInstance(2);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Adjust format accordingly
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone to UTC

            LocalDate startDate;
            LocalDate currentDate;
            try {
                startDate = sdf.parse(historicalData.get(0).getDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                currentDate = sdf.parse(rate.getDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (ParseException e) {
                e.printStackTrace(); // Handle parsing exception
                continue; // Skip to the next iteration
            }

            long daysBetween = ChronoUnit.DAYS.between(startDate, currentDate);

            instance.setValue(attributes.get(0), daysBetween);
            instance.setValue(attributes.get(1), rate.getRate());
            instances.add(instance);
        }

        return instances;
    }
}
