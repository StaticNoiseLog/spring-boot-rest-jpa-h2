package com.example.demo;

import org.junit.jupiter.api.Test;

class CatTest {

    @Test
    void testToString() {
        Cat leon = new Cat("Leon");
        System.out.println(leon.toString());
    }
}