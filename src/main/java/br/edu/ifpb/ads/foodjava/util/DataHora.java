package br.edu.ifpb.ads.foodjava.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataHora {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final LocalDateTime dh;

    // Construtor gera o momento atual
    public DataHora() {
        this.dh = LocalDateTime.now();
    }

    public LocalDateTime getLocalDateTime() {
        return dh;
    }
}