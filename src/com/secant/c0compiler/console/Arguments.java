package com.secant.c0compiler.console;

public class Arguments {
    private static int mode;
    private static String inputFileName;
    private static String outputFileName;

    public static void setMode(int mode) {
        Arguments.mode = mode;
    }

    public static void setInputFileName(String fileName) {
        Arguments.inputFileName = fileName;
    }

    public static void setOutputFileName(String fileName) {
        Arguments.outputFileName = fileName;
    }

    public static int getMode() {
        return Arguments.mode;
    }

    public static String getInputFileName() {
        return Arguments.inputFileName;
    }

    public static String getOutputFileName() {
        return Arguments.outputFileName;
    }
}
