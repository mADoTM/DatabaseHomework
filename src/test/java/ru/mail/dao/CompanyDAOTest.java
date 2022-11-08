package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import ru.mail.commons.JDBCCredentials;
import ru.mail.dto.entity.Company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CompanyDAOTest {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;

    private CompanyDAO dao;

    private Connection connection;

    @Test
    void add() {
        Company company = new Company(0, "name", 1, 0);
        dao.save(company);
        assertEquals(company, dao.get(company.companyId()));
    }

    @Test
    void all() {
        final var oldSize = dao.all().size();
        Company company = new Company(0, "name", 1, 0);
        dao.save(company);
        assertEquals(oldSize + 1, dao.all().size());
    }

    @Test
    void update() {
        Company company1 = new Company(0, "name", 0, 0);
        dao.save(company1);
        Company company2 = new Company(0, "name1", 1, 1);
        dao.update(company2);
        assertEquals(company2, dao.get(company1.companyId()));
    }

    @Test
    void delete() {
        Company company = new Company(0, "name", 0, 0);
        dao.save(company);
        dao.delete(company);
        assertThrows(IllegalStateException.class, () -> dao.get(company.companyId()));
    }

    @AfterEach
    public void afterEach() throws SQLException {
        connection.close();
    }

    @BeforeEach
    public void beforeAll() throws SQLException {
        connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password());
        dao = new CompanyDAO(connection);
        connection.setAutoCommit(false);
    }
}