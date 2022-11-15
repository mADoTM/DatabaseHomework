package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import ru.mail.commons.DAO;
import ru.mail.commons.DbConnectionHelper;
import ru.mail.dto.entity.Company;

import java.sql.SQLException;
import java.util.*;

public final class CompanyDAO implements DAO<Company> {

    @Override
    public @NotNull Company get(int id) {
        try (var statement = DbConnectionHelper.getConnection().createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT company_id, name, TIN, checking_account FROM company WHERE company_id = " + id)) {
                if (resultSet.next()) {
                    return new Company(resultSet.getInt("company_id"),
                            resultSet.getString("name"),
                            resultSet.getInt("TIN"),
                            resultSet.getInt("checking_account"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @Override
    public @NotNull List<@NotNull Company> all() {
        final var result = new ArrayList<Company>();
        try (var statement = DbConnectionHelper.getConnection().createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT * FROM company")) {
                while (resultSet.next()) {
                    result.add(new Company(resultSet.getInt("company_id"),
                            resultSet.getString("name"),
                            resultSet.getInt("TIN"),
                            resultSet.getInt("checking_account")));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(@NotNull Company entity) {
        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("INSERT INTO company (company_id, name, TIN, checking_account) VALUES (?,?, ?, ?)")) {
            preparedStatement.setInt(1, entity.companyId());
            preparedStatement.setString(2, entity.name());
            preparedStatement.setInt(3, entity.TIN());
            preparedStatement.setInt(4, entity.checkingAccount());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull Company entity) {
        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("UPDATE company SET name = ?, TIN = ?, checking_account = ? WHERE company_id = ?")) {
            preparedStatement.setString(1, entity.name());
            preparedStatement.setInt(2, entity.TIN());
            preparedStatement.setInt(3, entity.checkingAccount());
            preparedStatement.setInt(4, entity.companyId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(@NotNull Company entity) {
        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("DELETE FROM company WHERE company_id = ?")) {
            preparedStatement.setInt(1, entity.companyId());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.companyId() + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
