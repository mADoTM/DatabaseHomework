package ru.mail.dao;

import org.junit.jupiter.api.*;
import ru.mail.commons.DbConnectionHelper;
import ru.mail.commons.FlywayInitializer;
import ru.mail.dto.entity.Company;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CompanyDAOTest {
    private CompanyDAO dao;

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
        DbConnectionHelper.closeConnection();
    }

    @BeforeEach
    public void beforeEach() throws SQLException {
        dao = new CompanyDAO();
        DbConnectionHelper.setAutoCommit(false);
    }

    @BeforeAll
    public static void beforeAll() {
        FlywayInitializer.initDb();
    }
}