package ru.mail.dto;

import ru.mail.dto.entity.Company;
import ru.mail.dto.entity.Product;

import java.util.HashMap;
import java.util.Map;

public final class CompaniesWithAmountOfProductsReport {
    private final HashMap<Company, HashMap<Product, Integer>> companies = new HashMap<>();

    public void add(Company company, Product product, int amount) {
        if(!companies.containsKey(company)) {
            companies.put(company, new HashMap<>());
        }
        companies.get(company).put(product, amount);
    }

    public Map<Product, Integer> getProductsByCompany(Company company) {
        return companies.get(company);
    }

    @Override
    public String toString() {
        return "CompaniesWithAmountOfProductsReport{" +
                "companies=" + companies +
                '}';
    }
}
