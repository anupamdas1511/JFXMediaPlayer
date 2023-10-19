package com.example.jmediaplayer.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Slider;

public class SeekableProgressBar extends Slider {
    private final DoubleProperty seekValue = new SimpleDoubleProperty(0.0);

    public SeekableProgressBar() {
        super();
        setMin(0);
        setMax(1);

        valueProperty().bind(seekValue);
        setOnMousePressed(event -> handleMouseEvent(event.getX()));
        setOnMouseDragged(event -> handleMouseEvent(event.getX()));
    }

    private void handleMouseEvent(double x) {
        double newValue = x / getWidth();
        if (newValue >= 0 && newValue <= 1) {
            seekValue.set(newValue);
        }
    }
}
