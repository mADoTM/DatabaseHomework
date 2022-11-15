package ru.mail.dto;

import org.jetbrains.annotations.NotNull;
import ru.mail.dto.entity.Product;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class PerDayProductsReport {
    private final @NotNull HashMap<Product, Set<DateTotals>> products = new HashMap<>();

    public void add(Date date, Product product, int count, int sum) {
        final var dateTotals = new DateTotals(date, count, sum);

        if(!products.containsKey(product)) {
            products.put(product, new HashSet<>());
        }

        products.get(product).add(dateTotals);
    }

    public @NotNull HashMap<Product, Integer> totalSumForProducts() {
        final var result = new HashMap<Product, Integer>();

        for(var product : products.keySet()) {
            int sum = 0;
            for(var totals : products.get(product)) {
                sum += totals.sum();
            }
            result.put(product, sum);
        }

        return result;
    }

    public @NotNull HashMap<Product, Integer> totalCountForProducts() {
        final var result = new HashMap<Product, Integer>();

        for(var product : products.keySet()) {
            int count = 0;
            for(var totals : products.get(product)) {
                count += totals.count();
            }
            result.put(product, count);
        }

        return result;
    }

    @Override
    public String toString() {
        return "PerDayProductsReport{" +
                "products=" + products +
                '}';
    }
}
