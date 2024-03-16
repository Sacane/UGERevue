
package fr.uge.testingtestsfortest;

import fr.uge.fakestringprovider.*;
import fr.uge.fakestringprovider.HelloWorld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InfiniteLoopTest {
    private static final String HELLO_WORLD = "Hello world !";

    public InfiniteLoopTest() {
    }

    @Test
    void infinite(){
        var i = 0;
        while (true){
            i++;
            i--;
        }
    }
}
