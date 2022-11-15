package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import ru.mail.commons.DAO;
import ru.mail.commons.DbConnectionHelper;
import ru.mail.dto.entity.Consingment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ConsingmentDAO implements DAO<Consingment> {

    @Override
    public @NotNull Consingment get(int id) {
        try(var statement = DbConnectionHelper.getConnection().createStatement()) {
            try(var resultSet = statement.executeQuery("SELECT consingment_id, order_date, company_id FROM consingment WHERE consingment_id = " + id)) {
                if (resultSet.next()) {
                    return new Consingment(resultSet.getInt("consingment_id"),
                            resultSet.getDate("order_date"),
                            resultSet.getInt("company_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @Override
    public @NotNull List<@NotNull Consingment> all() {
        final var result = new ArrayList<Consingment>();
        try (var statement = DbConnectionHelper.getConnection().createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT * FROM consingment")) {
                while (resultSet.next()) {
                    result.add(new Consingment(
                            resultSet.getInt("consingment_id"),
                            resultSet.getDate("order_date"),
                            resultSet.getInt("consingment_id")
                    ));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(@NotNull Consingment entity) {
        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("INSERT INTO consingment (consingment_id, order_date, company_id) VALUES (?, ?, ?)")) {
            preparedStatement.setInt(1, entity.consingmentId());
            preparedStatement.setDate(2, entity.orderDate());
            preparedStatement.setInt(3, entity.companyId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull Consingment entity) {
        try(var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("UPDATE consingment SET order_date = ?, company_id = ? WHERE consingment_id = ?")) {
            preparedStatement.setDate(1, entity.orderDate());
            preparedStatement.setInt(2, entity.companyId());
            preparedStatement.setInt(3, entity.consingmentId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(@NotNull Consingment entity) {
        try(var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("DELETE FROM consingment WHERE consingment_id = ?")) {
            preparedStatement.setInt(1, entity.consingmentId());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.consingmentId() + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
