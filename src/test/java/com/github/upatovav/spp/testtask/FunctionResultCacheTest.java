package com.github.upatovav.spp.testtask;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Supplier;

@SpringBootTest
@Log
public class FunctionResultCacheTest {

    private FunctionResultCache<String, String> functionResultCache;

    Supplier<String> mock = Mockito.mock(Supplier.class);

    @BeforeEach
    void before(){
        functionResultCache = new FunctionResultCache<>();
        Mockito.reset(mock);
    }

    @Test
    void testCaching(){
        Mockito.when(mock.get()).thenReturn("value1");
        Assert.assertTrue(get1("key1").equals("value1"));
        Mockito.verify(mock, Mockito.times(1)).get();
        Assert.assertTrue(get1("key1").equals("value1"));
        Mockito.verify(mock, Mockito.times(1)).get();
    }

    @Test
    void testFunctionNotEquals(){
        Mockito.when(mock.get()).thenReturn("value1");
        Assert.assertTrue(get1("key1").equals("value1"));
        Mockito.verify(mock, Mockito.times(1)).get();
        Assert.assertTrue(get2("key1").equals("value1"));
        Mockito.verify(mock, Mockito.times(2)).get();
    }

    @Test
    void testKeyNotEquals(){
        Mockito.when(mock.get()).thenReturn("value1");
        Assert.assertTrue(get1("key1").equals("value1"));
        Mockito.verify(mock, Mockito.times(1)).get();
        Assert.assertTrue(get1("key2").equals("value1"));
        Mockito.verify(mock, Mockito.times(2)).get();
    }


    @SneakyThrows
    private String get1(String key){
        return functionResultCache.compute(key, new FunctionResultCache.CacheableFunction<String, String>() {
            @Override
            public String apply(String s) {
                log.info("function 1 executed");
                return mock.get();
            }
        }).get();
    }

    @SneakyThrows
    private String get2(String key){
        return functionResultCache.compute(key, new FunctionResultCache.CacheableFunction<String, String>() {
            @Override
            public String apply(String s) {
                log.info("function 2 executed");
                return mock.get();
            }
        }).get();
    }
}
