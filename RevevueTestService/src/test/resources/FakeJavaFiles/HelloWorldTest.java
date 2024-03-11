package fr.uge.testingtestsfortest;

import fr.uge.fakestringprovider.*;
import fr.uge.fakestringprovider.HelloWorld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {
    private static final String HELLO_WORLD = "Hello world !";

    public HelloWorldTest() {
    }

    @Test
    void helloWorld() {
        HelloWorld var1 = new HelloWorld();
        Assertions.assertEquals("Hello world !", var1.helloWorld());
    }

    @Test
    void wrongTest() {
        HelloWorld var1 = new HelloWorld();
        Assertions.assertEquals("Hello world ?", var1.helloWorld());
    }

}
