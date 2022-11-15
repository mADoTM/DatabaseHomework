package ru.mail.dto;

import org.junit.jupiter.api.Test;
import ru.mail.dto.entity.Product;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class PerDayProductsReportTest {
    @Test
    void checkCountOfProducts() {
        final var report = new PerDayProductsReport();

        final var product1 = new Product(1, "1");
        final var product2 = new Product(1, "2");
        report.add(Date.valueOf("2022-11-08"), product1, 4, 500);
        report.add(Date.valueOf("2022-11-08"), product2, 3, 200);
        report.add(Date.valueOf("2022-07-25"), product1, 15, 2500);

        final var reportMap = report.totalCountForProducts();
        assertEquals(19, (int) reportMap.get(product1));
        assertEquals(3, (int) reportMap.get(product2));
    }

    @Test
    void checkSumOfProducts() {
        final var report = new PerDayProductsReport();

        final var product1 = new Product(1, "1");
        final var product2 = new Product(1, "2");
        report.add(Date.valueOf("2022-11-08"), product1, 4, 500);
        report.add(Date.valueOf("2022-11-08"), product2, 3, 200);
        report.add(Date.valueOf("2022-07-25"), product1, 15, 2500);

        final var reportMap = report.totalSumForProducts();
        assertEquals(3000, (int) reportMap.get(product1));
        assertEquals(200, (int) reportMap.get(product2));
    }
}