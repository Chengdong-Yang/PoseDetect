package com.google.mlkit.vision.demo.java;

public class squart_model {
    private int id;
    private int count;

    public squart_model(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "squart_model{" +
                "id=" + id +
                ", count=" + count +
                '}';
    }
}
