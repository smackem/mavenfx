package net.smackem.mavenfx.model;

public class Functions {
    @FunctionalInterface
    public interface IntegerMapper
    {
        int apply(int value);
    }
}
