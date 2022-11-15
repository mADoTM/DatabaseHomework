package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import ru.mail.commons.DAO;
import ru.mail.commons.DbConnectionHelper;
import ru.mail.dto.CompaniesWithAmountOfProductsReport;
import ru.mail.dto.PerDayProductsReport;
import ru.mail.dto.entity.Company;
import ru.mail.dto.entity.Position;
import ru.mail.dto.entity.Product;

import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

public final class PositionDAO implements DAO<Position> {
    private final @NotNull String SELECT_PRODUCTS_WITH_AVERAGE_COST = """
            SELECT product.inner_code, product.name, t.by_one\s
                FROM product,(SELECT position.inner_code, AVG((cost / amount)) as by_one\s
                                FROM position
                                JOIN consingment ON consingment.consingment_id = position.consingment_id
                                WHERE consingment.order_date BETWEEN ? AND ?
                                GROUP BY position.inner_code) as t
                WHERE product.inner_code = t.inner_code""";

    private final @NotNull String SELECT_EVERY_DAY_PRODUCT_WITH_COUNT_AND_SUM = """
            SELECT t.order_date, product.inner_code, product.name, t.count, t.sum\s
                        FROM product, (SELECT consingment.order_date, product.inner_code, SUM(amount) as count, SUM(cost)\s
                                        FROM position\s
                                        JOIN consingment ON consingment.consingment_id = position.consingment_id AND order_date BETWEEN ? AND ?
                                        JOIN product ON product.inner_code = position.inner_code
                                        GROUP BY consingment.order_date, product.inner_code) as t
                       WHERE t.inner_code = product.inner_code""";

    private final @NotNull String SELECT_FIRST_TEN_COMPANIES_WITH_BIGGEST_AMOUNT = """
            SELECT company.company_id, company.name, company.tin, company.checking_account, t.total
                            FROM company
                            LEFT JOIN (SELECT consingment.company_id, SUM(amount) as total from consingment
                                                JOIN position ON consingment.consingment_id = position.consingment_id
                                                GROUP BY company_id) as t\s
                            ON company.company_id = t.company_id
                            ORDER BY t.total
                            LIMIT 10""";

    private final @NotNull String SELECT_COMPANIES_WITH_AMOUNT_SATISFIED_CONDITION = """
            SELECT company.company_id, company.name, company.TIN, company.checking_account, t.inner_code, t.count\s
            FROM company, (SELECT consingment.company_id, position.inner_code, SUM(position.amount) as count
                            FROM consingment
                            JOIN position ON consingment.consingment_id = position.consingment_id
                            GROUP BY consingment.company_id, position.inner_code) as t
            WHERE company.company_id = t.company_id""";

    private final @NotNull String SELECT_COMPANIES_WITH_PRODUCTS_IN_PERIOD = """
            SELECT company.company_id, company.name, company.TIN, company.checking_account, product.inner_code, product.name as product_name FROM consingment
                LEFT JOIN position ON consingment.consingment_id = position.consingment_id AND order_date BETWEEN ? AND ?
                LEFT JOIN product ON product.inner_code = position.inner_code
                RIGHT JOIN company ON consingment.company_id = company.company_id""";

    @Override
    public @NotNull Position get(int consingmentId) {
        try (var statement = DbConnectionHelper.getConnection().createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT cost, inner_code, amount, consingment_id FROM position WHERE consingment_id = " + consingmentId)) {
                if (resultSet.next()) {
                    return new Position(resultSet.getInt("cost"),
                            resultSet.getInt("inner_code"),
                            resultSet.getInt("amount"),
                            resultSet.getInt("consingment_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Record with id " + consingmentId + "not found");
    }

    @Override
    public @NotNull List<@NotNull Position> all() {
        final var result = new ArrayList<Position>();
        try (var statement = DbConnectionHelper.getConnection().createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT * FROM position")) {
                while (resultSet.next()) {
                    result.add(new Position(resultSet.getInt("cost"),
                            resultSet.getInt("inner_code"),
                            resultSet.getInt("amount"),
                            resultSet.getInt("consingment_id")));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(@NotNull Position entity) {
        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("INSERT INTO position (cost, inner_code, amount, consingment_id) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, entity.cost());
            preparedStatement.setInt(2, entity.innerCode());
            preparedStatement.setInt(3, entity.amount());
            preparedStatement.setInt(4, entity.consingmentId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull Position entity) {
        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("UPDATE position SET cost = ?, amount = ? WHERE consingment_id = ? AND inner_code = ?")) {
            preparedStatement.setInt(1, entity.cost());
            preparedStatement.setInt(2, entity.amount());
            preparedStatement.setInt(3, entity.consingmentId());
            preparedStatement.setInt(4, entity.innerCode());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(@NotNull Position entity) {
        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("DELETE FROM position WHERE consingment_id = ? AND cost = ? AND amount = ? AND inner_code = ?")) {
            preparedStatement.setInt(1, entity.consingmentId());
            preparedStatement.setInt(2, entity.cost());
            preparedStatement.setInt(3, entity.amount());
            preparedStatement.setInt(4, entity.innerCode());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.consingmentId() + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public @NotNull Map<Product, Integer> getAverageProductCostInPeriod(Date from, Date to) {
        final var result = new HashMap<Product, Integer>();

        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement(SELECT_PRODUCTS_WITH_AVERAGE_COST)) {
            preparedStatement.setDate(1, from);
            preparedStatement.setDate(2, to);
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.put(new Product(
                                    resultSet.getInt("inner_code"),
                                    resultSet.getString("name")
                            ),
                            resultSet.getInt("by_one"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public @NotNull PerDayProductsReport getPerDayProductsReportInPeriod(Date from, Date to) {
        final var result = new PerDayProductsReport();

        try (var statement = DbConnectionHelper.getConnection().prepareStatement(SELECT_EVERY_DAY_PRODUCT_WITH_COUNT_AND_SUM)) {
            statement.setDate(1, from);
            statement.setDate(2, to);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final var innerCode = resultSet.getInt("inner_code");
                    final var name = resultSet.getString("name");
                    final var orderDate = resultSet.getDate("order_date");
                    final var count = resultSet.getInt("count");
                    final var sum = resultSet.getInt("sum");

                    final var product = new Product(innerCode, name);

                    result.add(orderDate, product, count, sum);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public @NotNull List<Company> getFirstTenCompaniesWithBiggestAmountOfProducts() {
        List<Company> result = new ArrayList<>();

        try (var statement = DbConnectionHelper.getConnection().createStatement()) {
            try (var resultSet = statement.executeQuery(SELECT_FIRST_TEN_COMPANIES_WITH_BIGGEST_AMOUNT)) {
                while (resultSet.next()) {
                    result.add(new Company(resultSet.getInt("company_id"),
                            resultSet.getString("name"),
                            resultSet.getInt("TIN"),
                            resultSet.getInt("checking_account")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public @NotNull CompaniesWithAmountOfProductsReport getCompaniesWithAmountSatisfiedCondition(Map<Product, Integer> products) {
        final var result = new CompaniesWithAmountOfProductsReport();

        try (var statement = DbConnectionHelper.getConnection().createStatement()) {
            try (var resultSet = statement.executeQuery(SELECT_COMPANIES_WITH_AMOUNT_SATISFIED_CONDITION)) {
                while (resultSet.next()) {
                    final var dbInnerCode = resultSet.getInt("inner_code");
                    final var dbAmount = resultSet.getInt("count");

                    final var product = products.keySet().stream().filter(x -> x.innerCode() == dbInnerCode).findFirst();

                    if (product.isEmpty() || products.get(product.get()) > dbAmount) {
                        continue;
                    }

                    result.add(new Company(
                                    resultSet.getInt("company_id"),
                                    resultSet.getString("name"),
                                    resultSet.getInt("TIN"),
                                    resultSet.getInt("checking_account")),
                            product.get(),
                            dbAmount);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public @NotNull Map<Company, Set<Product>> getCompaniesWithProductsInPeriod(Date from, Date to) {
        final var result = new HashMap<Company, Set<Product>>();

        try (var statement = DbConnectionHelper.getConnection().prepareStatement(SELECT_COMPANIES_WITH_PRODUCTS_IN_PERIOD)) {
            statement.setDate(1, from);
            statement.setDate(2, to);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final var company = new Company(resultSet.getInt("company_id"),
                            resultSet.getString("name"),
                            resultSet.getInt("TIN"),
                            resultSet.getInt("checking_account"));
                    if (!result.containsKey(company)) {
                        result.put(company, new HashSet<>());
                    }
                    if (resultSet.getString("product_name") == null) {
                        continue;
                    }

                    final var product = new Product(
                            resultSet.getInt("inner_code"),
                            resultSet.getString("product_name")
                    );
                    result.get(company).add(product);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
