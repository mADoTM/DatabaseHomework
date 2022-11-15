package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import ru.mail.commons.DAO;
import ru.mail.commons.DbConnectionHelper;
import ru.mail.dto.entity.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ProductDAO implements DAO<Product> {
    @Override
    public @NotNull Product get(int id) {
        try(var statement = DbConnectionHelper.getConnection().createStatement()) {
            try(var resultSet = statement.executeQuery("SELECT inner_code, name FROM product WHERE inner_code = " + id)) {
                if (resultSet.next()) {
                    return new Product(resultSet.getInt("inner_code"), resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @Override
    public @NotNull List<@NotNull Product> all() {
        final var result = new ArrayList<Product>();
        try (var statement = DbConnectionHelper.getConnection().createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT * FROM product")) {
                while (resultSet.next()) {
                    result.add(new Product(resultSet.getInt("inner_code"), resultSet.getString("name")));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(@NotNull Product entity) {
        try (var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("INSERT INTO product (inner_code, name) VALUES(?,?)")) {
            preparedStatement.setInt(1, entity.innerCode());
            preparedStatement.setString(2, entity.name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull Product entity) {
        try(var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("UPDATE product SET name = ? WHERE inner_code = ?")) {
            preparedStatement.setString(1, entity.name());
            preparedStatement.setInt(2, entity.innerCode());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(@NotNull Product entity) {
        try(var preparedStatement = DbConnectionHelper.getConnection().prepareStatement("DELETE FROM product WHERE inner_code = ?")) {
            preparedStatement.setInt(1, entity.innerCode());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.innerCode() + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
