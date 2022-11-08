package ru.mail;

import org.jetbrains.annotations.NotNull;
import ru.mail.commons.FlywayInitializer;
import ru.mail.commons.JDBCCredentials;
import ru.mail.dao.PositionDAO;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Scanner;

public class Main {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static PositionDAO positionDAO;

    public static void main(String[] args) {
        FlywayInitializer.initDb();

        try (var connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password())) {
            positionDAO = new PositionDAO(connection);

            final var date1 = getDateForReport(1);
            final var date2 = getDateForReport(2);

            System.out.println("---Product with average cost in period---");
            System.out.println(positionDAO.getAverageProductCostInPeriod(date1, date2));

            System.out.println("---First 10 companies with biggest delivery---");
            System.out.println(positionDAO.getFirstTenCompaniesWithBiggestAmountOfProducts());

            System.out.println("---Companies with optional delivered products in period---");
            System.out.println(positionDAO.getCompaniesWithProductsInPeriod(date1, date2));

            System.out.println("---Products per in period---");
            final var products = positionDAO.getPerDayProductsReportInPeriod(date1, date2);
            System.out.println(products);
            System.out.println("TOTAL SUM FOR PRODUCTS");
            System.out.println(products.totalSumForProducts());
            System.out.println("TOTAL AMOUNT FOR PRODUCTS");
            System.out.println(products.totalCountForProducts());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static String getUserInput() {
        Scanner scn = new Scanner(System.in);
        return scn.next();
    }

    private static Date getDateForReport(int n) {
        System.out.println("Enter date " + n + " in form YYYY-MM-DD for reports");
        return Date.valueOf(getUserInput());
    }

}