package com.secant.c0compiler.console;

public class Main {
    public static void main(String[] args) {
        switch (args.length) {
            case 0:
                printHelp();
                break;
            case 1:
                if (args[0].equals("-h")) {
                    printHelp();
                } else {
                    printInvalid();
                }
                break;
            case 2:
                if (args[0].equals("-s")) {
                    Arguments.setMode(0);
                } else if (args[0].equals("-c")) {
                    Arguments.setMode(1);
                } else {
                    printInvalid();
                }
                Arguments.setInputFileName(args[1]);
                break;
            case 4:
                if (args[0].equals("-s")) {
                    Arguments.setMode(0);
                } else if (args[0].equals("-c")) {
                    Arguments.setMode(1);
                } else {
                    printInvalid();
                }
                Arguments.setInputFileName(args[1]);
                if (!args[2].equals("-o")) {
                    printInvalid();
                }
                Arguments.setOutputFileName(args[3]);
                break;
            default:
                printInvalid();
                break;
        }
    }

    private static void printInvalid() {
        System.out.print("Invalid argument.\n\n");
        printHelp();
    }

    private static void printHelp() {
        System.out.print("Usage:\n" +
                "  cc0 [options] input [-o file]\n" +
                "or\n" +
                "  cc0 [-h]\n" +
                "Options:\n" +
                "  -s        Convert input source code to assembly code\n" +
                "  -c        Convert input source code to binary file\n" +
                "  -h        Display help about usage of compiler\n" +
                "  -o file   Output to specific file\n\n");
    }
}
