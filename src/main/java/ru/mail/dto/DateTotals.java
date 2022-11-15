package ru.mail.dto;

import java.sql.Date;

public record DateTotals(Date date, int count, int sum) {
}
