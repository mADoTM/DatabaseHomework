package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mail.commons.FlywayInitializer;
import ru.mail.commons.JDBCCredentials;
import ru.mail.dto.entity.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOTest {

    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;

    private ProductDAO dao;

    private Connection connection;

    @Test
    void add() {
        Product product = new Product(0, "name");
        dao.save(product);
        assertEquals(product, dao.get(product.innerCode()));
    }

    @Test
    void all() {
        final var oldSize = dao.all().size();
        Product product = new Product(0, "name");
        dao.save(product);
        assertEquals(oldSize + 1, dao.all().size());
    }

    @Test
    void update() {
        Product product1 = new Product(0, "name");
        dao.save(product1);
        Product product2 = new Product(0, "another name");
        dao.update(product2);
        assertEquals(product2, dao.get(product1.innerCode()));
    }

    @Test
    void delete() {
        Product product = new Product(0, "name");
        dao.save(product);
        dao.delete(product);
        assertThrows(IllegalStateException.class, () -> dao.get(product.innerCode()));
    }

    @AfterEach
    public void afterEach() throws SQLException {
        connection.close();
    }

    @BeforeEach
    public void beforeEach() throws SQLException {
        connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password());
        dao = new ProductDAO(connection);
        connection.setAutoCommit(false);
    }

    @BeforeAll
    public static void beforeAll() {
        FlywayInitializer.initDb();
    }
}