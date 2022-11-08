package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mail.commons.JDBCCredentials;
import ru.mail.dto.entity.Company;
import ru.mail.dto.entity.Consingment;
import ru.mail.dto.entity.Position;
import ru.mail.dto.entity.Product;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PositionDAOTest {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;

    private PositionDAO positionDAO;

    private Connection connection;

    private final List<Product> products = List.of(
            new Product(-1, "product name1"),
            new Product(-2, "product name2"),
            new Product(-3, "product name3"),
            new Product(-4, "product name4"),
            new Product(-5, "product name5"),
            new Product(-6, "product name6"),
            new Product(-7, "product name7"),
            new Product(-8, "product name8"),
            new Product(-9, "product name9"),
            new Product(-10, "product name10")
    );

    private final List<Company> companies = List.of(
            new Company(-1, "company name1", -1, -1),
            new Company(-2, "company name2", -2, -2),
            new Company(-3, "company name3", -3, -3),
            new Company(-4, "company name4", -4, -4),
            new Company(-5, "company name5", -5, -5),
            new Company(-6, "company name6", -6, -6),
            new Company(-7, "company name7", -7, -7),
            new Company(-8, "company name8", -8, -8),
            new Company(-9, "company name9", -9, -9),
            new Company(-10, "company name10", -10, -10)

    );

    private final List<Consingment> consingments = List.of(
            new Consingment(-1, Date.valueOf("2022-8-11"), -1),
            new Consingment(-2, Date.valueOf("2022-8-11"), -2),
            new Consingment(-3, Date.valueOf("2022-8-11"), -3),
            new Consingment(-4, Date.valueOf("2022-8-10"), -4),
            new Consingment(-5, Date.valueOf("2022-8-10"), -5),
            new Consingment(-6, Date.valueOf("2022-8-9"), -6),
            new Consingment(-7, Date.valueOf("2022-8-9"), -7),
            new Consingment(-8, Date.valueOf("2022-8-8"), -8),
            new Consingment(-9, Date.valueOf("2022-8-7"), -9),
            new Consingment(-10, Date.valueOf("2022-8-6"), -10)
    );

    @Test
    void getCompaniesWithProductsInPeriod() {
        positionDAO.save(new Position(10, -1, 1, -1));
        positionDAO.save(new Position(40, -2, 3, -1));
        positionDAO.save(new Position(10, -1, 1, -2));

        final var expectedMap = new HashMap<Company, Set<Product>>();
        expectedMap.put(companies.get(0), Set.of(products.get(0), products.get(1)));
        expectedMap.put(companies.get(1), Set.of(products.get(0)));

        final var dbCompanies = positionDAO.getCompaniesWithProductsInPeriod(Date.valueOf("2022-8-10"), Date.valueOf("2022-8-11"));
        assertFalse(Collections.disjoint(companies, dbCompanies.keySet()));

        for(var company : expectedMap.keySet()) {
            for(var product : expectedMap.get(company)) {
                assertTrue(dbCompanies.get(company).contains(product));
            }
        }
    }

    @Test
    void getPerDayProductsReportInPeriod() {
        positionDAO.save(new Position(10, -1, 1, -1));
        positionDAO.save(new Position(10, -1, 20, -1));
        positionDAO.save(new Position(10, -1, 3, -2));
        positionDAO.save(new Position(40, -2, 6, -4));
        positionDAO.save(new Position(40, -2, 6, -5));

        final var dbProducts = positionDAO.getPerDayProductsReportInPeriod(Date.valueOf("2022-8-10"), Date.valueOf("2022-8-11"));
        final var totalSums = dbProducts.totalSumForProducts();
        final var totalCounts = dbProducts.totalCountForProducts();

        assertEquals(30, totalSums.get(products.get(0)));
        assertEquals(24, totalCounts.get(products.get(0)));
    }

    @Test
    void getFirstTenCompaniesWithBiggestAmountOfProducts() {
        positionDAO.save(new Position(10, -1, Integer.MAX_VALUE - 1, -1));
        positionDAO.save(new Position(10, -1, Integer.MAX_VALUE - 2, -2));

        final var dbCompanies = positionDAO.getFirstTenCompaniesWithBiggestAmountOfProducts();

        assertTrue(dbCompanies.size() <= 10);
        assertEquals(-1, dbCompanies.get(0).companyId());
        assertEquals(-2, dbCompanies.get(1).companyId());
    }

    @Test
    void getProductWithAverageCostInPeriod() {
        positionDAO.save(new Position(10, -1, 1, -1));
        positionDAO.save(new Position(10, -1, 1, -2));
        positionDAO.save(new Position(30, -2, 2, -2));
        positionDAO.save(new Position(40, -2, 3, -1));

        final var products = positionDAO.getAverageProductCostInPeriod(Date.valueOf("2022-08-10"), Date.valueOf("2022-08-11"));

        assertEquals(10, products.get(products.keySet().stream().filter(x -> x.innerCode() == -1).findFirst().get()));
        assertEquals(14, products.get(products.keySet().stream().filter(x -> x.innerCode() == -2).findFirst().get()));
    }

    @Test
    void getCompaniesWithAmountSatisfiedCondition() {
        positionDAO.save(new Position(10, -1, Integer.MAX_VALUE - 1, -1));
        positionDAO.save(new Position(10, -2, Integer.MAX_VALUE - 2, -2));


        var map = new HashMap<Product, Integer>();
        map.put(products.get(0), Integer.MAX_VALUE - 1);
        map.put(products.get(1), Integer.MAX_VALUE);

        final var dbCompanies = positionDAO.getCompaniesWithAmountSatisfiedCondition(map);

        assertEquals(Integer.MAX_VALUE - 1, dbCompanies.getProductsByCompany(companies.get(0)).get(products.get(0)));
        assertNull(dbCompanies.getProductsByCompany(companies.get(0)).get(products.get(1)));
    }

    @Test
    void add() {
        Position position = new Position(1, -1, 1, -1);
        positionDAO.save(position);
        assertEquals(position, positionDAO.get(position.consingmentId()));
    }

    @Test
    void all() {
        final var oldSize = positionDAO.all().size();
        Position position = new Position(1, -1, 1, -1);
        positionDAO.save(position);
        assertEquals(oldSize + 1, positionDAO.all().size());
    }

    @Test
    void update() {
        Position position1 = new Position(1, -1, 1, -1);
        positionDAO.save(position1);
        Position position2 = new Position(10, -1, 10, -1);
        positionDAO.update(position2);
        assertEquals(position2, positionDAO.get(position1.consingmentId()));
    }

    @Test
    void delete() {
        Position position = new Position(1, -1, 1, -1);
        positionDAO.save(position);
        positionDAO.delete(position);
        assertThrows(IllegalStateException.class, () -> positionDAO.get(position.consingmentId()));
    }

    @AfterEach
    public void afterEach() throws SQLException {
        connection.close();
    }

    @BeforeEach
    public void beforeAll() throws SQLException {
        connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password());
        positionDAO = new PositionDAO(connection);
        connection.setAutoCommit(false);
        initDefaultProducts(new ProductDAO(connection));
        initDefaultCompanies(new CompanyDAO(connection));
        initDefaultConsingments(new ConsingmentDAO(connection));
    }

    private void initDefaultConsingments(ConsingmentDAO dao) {
        for(Consingment consingment : consingments) {
            dao.save(consingment);
        }
    }

    private void initDefaultProducts(ProductDAO dao) {
        for (Product product : products) {
            dao.save(product);
        }
    }

    private void initDefaultCompanies(CompanyDAO dao) {
        for (Company company : companies) {
            dao.save(company);
        }
    }
}