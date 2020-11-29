package openwrestling.view.news.controller;

import lombok.Getter;

import java.time.LocalDate;

public class DateRow {
    @Getter
    private LocalDate date;
    private String text;

    public DateRow(LocalDate date, String text) {
        this.date = date;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
