package org.edward.onion.bind.model;

import java.util.HashMap;

public class EnumInfo extends HashMap<String, String> {
    private static final int DEFAULT_SIZE = 10;

    public EnumInfo() {
        super(DEFAULT_SIZE);
    }

    public EnumInfo(int size) {
        super(size);
    }
}