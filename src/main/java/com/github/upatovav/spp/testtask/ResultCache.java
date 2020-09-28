package com.github.upatovav.spp.testtask;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.java.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Function;

@Log
public class ResultCache<K, V> {
    private ConcurrentHashMap<K, Future<V>> map = new ConcurrentHashMap<>();

    public Future<V> compute(K k, Function<K, V> f){
        return map.computeIfAbsent(k, kv -> CompletableFuture.supplyAsync(()-> f.apply(k)));
    }

}
