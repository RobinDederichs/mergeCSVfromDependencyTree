package org.example;

import java.util.List;

public class dependency {
    private String name;
    private List<dependency> dependencies;

    public dependency(String line) {
        name = line;
    }
}
