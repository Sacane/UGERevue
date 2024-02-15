package fr.pentagon.test;


public class JavaTestFile {

    private int counter;
    private final String own;

    public JavaTestFile(String own){
        this.own = own;
    }

    public void show() {
        System.out.println(own);
    }
}