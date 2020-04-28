package com.jkojote.diffhell.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String message;
    private LocalDateTime dateTime;

    public Message(String message, LocalDateTime dateTime) {
        this.message = message;
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return
            DateTimeFormatter.ISO_DATE_TIME.format(dateTime) +
            "\n---------------------------------------------\n" +
            message;
    }
}
