package com.sandrimar.currencyconverter.repositories;

import com.sandrimar.currencyconverter.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    List<Currency> findByAvailableTrue();
}
