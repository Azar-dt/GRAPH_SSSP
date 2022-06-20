package com.graph.graph.context;

import com.graph.graph.algorithm.Algorithm;

public class Context {
    private Algorithm algorithm;

    public void play() {
        algorithm.run();
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }
}
