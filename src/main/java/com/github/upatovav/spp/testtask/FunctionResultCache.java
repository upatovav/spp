package com.github.upatovav.spp.testtask;

import com.sun.el.lang.FunctionMapperImpl;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Function;

@Log
public class FunctionResultCache <K, V> {
    private ConcurrentHashMap<Key<K, V>, Future<V>> map = new ConcurrentHashMap<>();

    public Future<V> compute(K k, Function<K, V> f){
        Key<K, V> key = new Key<>(k, f);
        return map.computeIfAbsent(key, kv -> CompletableFuture.supplyAsync(()-> kv.getFunction().apply(key.getArg())));
    }

    /**
     * Function class implementing equals and hashCode based on function class name instead of pointer
     * thus allowing correct caching of results
     * @param <K>
     * @param <V>
     */
    @EqualsAndHashCode
    public static abstract class CacheableFunction<K, V> implements Function<K, V>{
        protected String functionName = this.getClass().getName();
    }


    @AllArgsConstructor
    @Value
    private static class Key<K, V> {
        private K arg;
        private Function<K,V> function;
    }
}
