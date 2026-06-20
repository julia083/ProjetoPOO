package br.edu.ifpb.ads.foodjava.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataHora {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dh = LocalDateTime.now();

        private String dataHora = dh.format(fmt);

        public String getDataHora(){
            return dataHora;
        }

}