package com.sandrimar.currencyconverter.repositories;

import com.sandrimar.currencyconverter.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
}
