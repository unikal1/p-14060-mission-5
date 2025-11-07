package com.quoteBoard;


import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        App application = new App(System.in);
        try {
            application.run();
        } catch (IOException ignore) {}
    }
}