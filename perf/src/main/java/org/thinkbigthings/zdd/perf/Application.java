package org.thinkbigthings.zdd.perf;

public class Application {

    public static void main(String[] args) throws Exception {

        LoadTester test = new LoadTester();

        test.run();

        System.out.println("Program done.");
    }

}