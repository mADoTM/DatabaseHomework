package ru.mail.dto.entity;

import java.sql.Date;

public record Consingment(int consingmentId, Date orderDate, int companyId) {
}
