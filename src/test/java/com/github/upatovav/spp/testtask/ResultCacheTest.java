package com.github.upatovav.spp.testtask;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootTest
@Log
public class ResultCacheTest {

    private ResultCache<String, String> functionResultCache;

    Supplier<String> mock = Mockito.mock(Supplier.class);

    @BeforeEach
    void before(){
        functionResultCache = new ResultCache<>();
        Mockito.reset(mock);
    }

    @Test
    void testCaching(){
        Mockito.when(mock.get()).thenReturn("value1");
        Assert.assertTrue(get("key1").equals("value1"));
        Mockito.verify(mock, Mockito.times(1)).get();
        Assert.assertTrue(get("key1").equals("value1"));
        Mockito.verify(mock, Mockito.times(1)).get();
    }

    @Test
    void testKeyNotEquals(){
        Mockito.when(mock.get()).thenReturn("value1");
        Assert.assertTrue(get("key1").equals("value1"));
        Mockito.verify(mock, Mockito.times(1)).get();
        Assert.assertTrue(get("key2").equals("value1"));
        Mockito.verify(mock, Mockito.times(2)).get();
    }


    @SneakyThrows
    private String get(String key){
        return functionResultCache.compute(key, s -> {
            log.info("function executed");
            return mock.get();
        }).get();
    }
}
