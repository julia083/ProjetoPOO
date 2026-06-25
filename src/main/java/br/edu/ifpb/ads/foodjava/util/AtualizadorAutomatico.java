package br.edu.ifpb.ads.foodjava.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class AtualizadorAutomatico {

    private final Timeline timeline;

    public AtualizadorAutomatico(int segundos, Runnable acao) {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(segundos), e -> acao.run())
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void iniciar() {
        timeline.play();
    }

    public void parar() {
        timeline.stop();
    }
}