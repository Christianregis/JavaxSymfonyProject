package fr.istic.prog.tp3;

import java.util.Scanner;

public class TestInsertionInteger {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        InsertionInteger insertionInteger = new InsertionInteger();
        insertionInteger.createArray(scanner);
        System.out.println(insertionInteger);

        scanner.close();
    }
}
