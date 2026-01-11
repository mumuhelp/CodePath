package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloTest {

    @Test
    void testGreet() {
        Hello hello = new Hello();
        String result = hello.greet(true);
        assertEquals("Good morning", result);
    }
}
