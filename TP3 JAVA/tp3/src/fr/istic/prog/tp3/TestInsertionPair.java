package fr.istic.prog.tp3;

import java.util.Scanner;

/**
 * @author CHEUKAP TATOUN CHRISTIAN REGIS et LEBAS ENZO
 * TestInsertionPair
 */
public class TestInsertionPair {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Lecture via Scanner avec un fichier de test ou via le clavier

        InsertionPair insertionPair = new InsertionPair();
        int inputType = scanner.nextInt();
        switch (inputType) {
            case 1:
                insertionPair.createArray(scanner);
                System.out.println(insertionPair);
                break;
            case 2:
                try {
                    Scanner fileScanner = new Scanner(new java.io.File(args[0]));
                    insertionPair.createArray(fileScanner);
                    System.out.println(insertionPair);
                    fileScanner.close();
                } catch (Exception _) {
                    System.out.println("Le fichier est introuvable veuillez ressayer !");
                }
                break;
            default:
                System.out.println("Type d'entrée invalide. Veuillez entrer 1 pour le clavier ou 2 pour un fichier.");
                return;
        }

        System.out.println(insertionPair);
        scanner.close();
    }
}
