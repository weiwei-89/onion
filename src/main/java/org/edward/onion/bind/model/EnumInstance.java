package org.edward.onion.bind.model;

import java.util.HashMap;

public class EnumInstance extends HashMap<String, Enum> {
    private static final int DEFAULT_SIZE = 10;

    public EnumInstance() {
        super(DEFAULT_SIZE);
    }

    public EnumInstance(int size) {
        super(size);
    }
}