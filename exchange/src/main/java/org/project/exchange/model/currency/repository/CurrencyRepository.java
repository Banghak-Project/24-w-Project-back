package org.project.exchange.model.currency.repository;

import org.project.exchange.model.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
