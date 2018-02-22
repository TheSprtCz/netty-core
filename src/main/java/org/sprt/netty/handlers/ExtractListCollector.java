package org.sprt.netty.handlers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ExtractListCollector<Type> implements Collector<List<Type>, CopyOnWriteArrayList<Type>, Set<Type>> {

    @Override
    public BiConsumer<CopyOnWriteArrayList<Type>, List<Type>> accumulator() {
        return (builder, t) -> builder.addAll(t);
    }

    @Override
    public Set<Characteristics> characteristics() {
        Characteristics[] chars = {Characteristics.CONCURRENT, Characteristics.UNORDERED};
        return new HashSet<Characteristics>(Arrays.asList(chars));
    }

    @Override
    public BinaryOperator<CopyOnWriteArrayList<Type>> combiner() {
        return (left, right) -> {
            left.addAll(right);
            return left;
        };
    }

    @Override
    public Function<CopyOnWriteArrayList<Type>, Set<Type>> finisher() {
        return (builder) -> {
            return new HashSet<Type>(builder);
        };
    }

    @Override
    public Supplier<CopyOnWriteArrayList<Type>> supplier() {
        return () -> new CopyOnWriteArrayList<Type>();
    }

}
