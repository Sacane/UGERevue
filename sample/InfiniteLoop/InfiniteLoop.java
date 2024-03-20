package fr.uge.fakestringprovider;

public final class InfiniteLoop {

    public InfiniteLoop() {
    }

    public void infinite() {
        var i = 0;
        while (true){
            i++;
            i--;
        }
    }
}
