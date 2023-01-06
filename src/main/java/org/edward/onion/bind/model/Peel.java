package org.edward.onion.bind.model;

import java.util.HashMap;

public class Peel extends HashMap<String, Object> {
    private static final int DEFAULT_SIZE = 10;

    public Peel() {
        super(DEFAULT_SIZE);
    }

    public Peel(int size) {
        super(size);
    }
}