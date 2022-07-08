package com.graph.graph.graphcore;

public class InvalidVertexException extends RuntimeException {

    public InvalidVertexException() {
        super("The vertex is invalid or does not belong to this graph.");
    }

    public InvalidVertexException(String string) {
        super(string);
    }

}