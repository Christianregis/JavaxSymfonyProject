package fr.istic.prog.tp3;

import java.util.Scanner;

/**
 * 
 * @author CHEUKAP TATOUN CHRISTIAN REGIS et LEBAS ENZO
 * @version 6.0
 * @since 2026-02-24
 * 
 *        Partie II
 *
 *        Lecture, au clavier, d'une suite de doublets d'entiers terminée par -1
 *
 *        Cette suite peut comporter des doublets identiques.
 * 
 *        Construction et affichage d'un tableau contenant les doublets
 *        distincts triés selon l'ordre croissant des doublets, à savoir : (x1,
 *        y1) < (x2, y2) <==> (x1<x2) ou (x1==x2 et y1<y2)
 * 
 *        En entrée : 3 8 1 4 3 8 1 3 -1 ==> En sortie : [1 3] [1 4] [3 8]
 */

public class InsertionPair {

	private static final int SIZE_MAX = 10;
	private static final int END_MARKER = -1;
	/**
	 * nombre de doublets présents dans t, 0 <= size <= SIZE_MAX
	 */
	private int size;
	private Pair[] array;

	public InsertionPair() {
		size = 0;
		array = new Pair[SIZE_MAX];
		for (int i = 0; i < SIZE_MAX; i++) {
			array[i] = new Pair(0, 0);
		}
	}

	/**
	 * @return copie de la partie remplie du tableau
	 */
	public Pair[] toArray() {
		Pair[] tab = new Pair[size];
		for (int i = 0; i < size; i++) {
			tab[i] = array[i].copyOf();
		}
		return tab;
	}

	/**
	 * array est rempli, par ordre croissant, en utilisant la fonction insert, avec
	 * les valeurs lues par scanner.
	 * 
	 * @param scanner
	 */
	public void createArray(Scanner scanner) {
		// Lecture via Scanner avec un fichier de test ou via le clavier
		
		while (true) {
			int val1 = scanner.nextInt();
			if (val1 == END_MARKER) {
				break;
			}
			int val2 = scanner.nextInt();
			if (val2 == END_MARKER) {
				break;
			}
			insert(new Pair(val1, val2));
		}
	}

	/**
	 * Si pair n'appartient pas à array[0..size-1] et size < SIZE_MAX, size est
	 * incrémenté de 1, pair est inséré dans array et les entiers array[0..size]
	 * sont triés par ordre croissant. Sinon array est inchangé.
	 * 
	 * @param pair doublet à insérer
	 * 
	 * @pre les doublets de array[0..size-1] sont triés par ordre croissant
	 * 
	 * @return false si pair appartient à array[0..size-1] ou si array est
	 *         complètement rempli; true si pair n’appartient pas à array[0..size-1]
	 */
	public boolean insert(Pair pair) {
		if (size == SIZE_MAX) {
			return false;
		}
		int index = 0;
		while (index < size && array[index].compareTo(pair) == -1) {
			index++;
		}
		if (index < size && array[index].compareTo(pair) == 0) {
			return false;
		}
		size++;
		for (int j = size - 1; j > index; j--) {
			array[j] = array[j - 1];
		}
		array[index] = pair.copyOf();
		return true;
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < size; i++) {
			result = result + array[i] + " ";
		}
		return result;
	}
}