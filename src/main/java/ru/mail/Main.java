package ru.mail;

import ru.mail.commons.FlywayInitializer;
import ru.mail.dao.CompanyDAO;
import ru.mail.dao.PositionDAO;
import ru.mail.dao.ProductDAO;
import ru.mail.dto.entity.Company;
import ru.mail.dto.entity.Product;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static PositionDAO positionDAO;

    public static void main(String[] args) {
        FlywayInitializer.initDb();
        positionDAO = new PositionDAO();

        printProducts();
        printCompanies();

        System.out.println("---First 10 companies with biggest delivery---");
        System.out.println(positionDAO.getFirstTenCompaniesWithBiggestAmountOfProducts());

        System.out.println("---Companies with product with amount greater than in map");
        final var map = getMapOfProductsAndTheirCounts();
        System.out.println(positionDAO.getCompaniesWithAmountSatisfiedCondition(map));

        final var date1 = getDateForReport(1);
        final var date2 = getDateForReport(2);

        System.out.println("---Products per day in period---");
        final var products = positionDAO.getPerDayProductsReportInPeriod(date1, date2);
        System.out.println(products);
        System.out.println("TOTAL SUM FOR PRODUCTS");
        System.out.println(products.totalSumForProducts());
        System.out.println("TOTAL AMOUNT FOR PRODUCTS");
        System.out.println(products.totalCountForProducts());

        System.out.println("---Product with average cost in period---");
        System.out.println(positionDAO.getAverageProductCostInPeriod(date1, date2));

        System.out.println("---Companies with optional delivered products in period---");
        var companiesWithProductsInPeriod = positionDAO.getCompaniesWithProductsInPeriod(date1, date2);
        printCompaniesWithProducts(companiesWithProductsInPeriod);
    }

    private static String getUserInput() {
        Scanner scn = new Scanner(System.in);
        return scn.next();
    }

    private static Date getDateForReport(int n) {
        System.out.println("Enter date " + (n == 1 ? "earliest" : "latest") + " in form YYYY-MM-DD for reports");
        return Date.valueOf(getUserInput());
    }

    private static Map<Product, Integer> getMapOfProductsAndTheirCounts() {
        final var map = new HashMap<Product, Integer>();

        String input;
        while (true) {
            System.out.println("Enter product inner_code or enter STOP if you want to stop :)");
            input = getUserInput();

            if (input.equals("STOP"))
                break;

            try {
                int productInnerCode = Integer.parseInt(input);

                final var product = new ProductDAO().get(productInnerCode);

                System.out.println("Enter product count");
                input = getUserInput();
                int productCount = Integer.parseInt(input);
                map.put(product, productCount);
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
                System.out.println("Try again");
            }
        }

        return map;
    }

    private static void printProducts() {
        final var products = new ProductDAO().all();
        System.out.println("---ALL PRODUCTS IN BASE--");
        System.out.println("INNER_CODE:\t\tProduct name");
        for (var product :
                products) {
            System.out.println(product.innerCode() + "\t\t" + product.name());
        }
    }

    private static void printCompanies() {
        final var companies = new CompanyDAO().all();
        System.out.println("---ALL COMPANIES IN BASE--");
        System.out.println("ID:\t\tCompany name\t\tTIN\t\tChecking account");
        for (var company :
                companies) {
            System.out.println(company.companyId() + "\t\t" + company.name() + "\t\t" + company.TIN()+ "\t\t" + company.checkingAccount());
        }
    }

    private static void printCompaniesWithProducts(Map<Company, Set<Product>> companiesWithProducts) {
        for(var company : companiesWithProducts.keySet()) {
            System.out.println("ID:\t\tCompany name\t\tTIN\t\tChecking account");
            System.out.println(company.companyId() + "\t\t" + company.name() + "\t\t" + company.TIN()+ "\t\t" + company.checkingAccount());
            if(companiesWithProducts.get(company).isEmpty()) {
                continue;
            }
            System.out.println("--PRODUCTS OF COMPANY " + company.name() + " --");
            System.out.println("INNER_CODE:\t\tProduct name");
            for (var product :
                    companiesWithProducts.get(company)) {
                System.out.println(product.innerCode() + "\t\t" + product.name());
            }
            System.out.println("--PRODUCTS ENDED--");
        }
    }
}