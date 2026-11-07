package fr.istic.prog.tp3;

import java.util.Scanner;

/**
 * @author CHEUKAP TATOUN CHRISTIAN REGIS et LEBAS ENZO
 * TestInsertionInteger
 */
public class TestInsertionInteger {
    public static void main(){
        Scanner scanner = new Scanner(System.in);
        InsertionInteger insertionInteger = new InsertionInteger();
        insertionInteger.createArray(scanner);
        System.out.println(insertionInteger);

        scanner.close();
    }
}
