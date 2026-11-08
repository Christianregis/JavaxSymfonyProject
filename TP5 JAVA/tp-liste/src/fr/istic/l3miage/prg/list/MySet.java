package fr.istic.l3miage.prg.list;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import fr.istic.l3miage.prg.list_util.Comparison;
import fr.istic.l3miage.prg.list_util.Iterator;
import fr.istic.l3miage.prg.list_util.List;
import fr.istic.l3miage.prg.list_util.SmallSet;

/**
 * @author CHEUKAP CHRISTIAN REGIS et ENZO LEBAS
 * @version 4.1
 * @since 2023-10-11
 */

public class MySet extends List<SubSet> {

	/**
	 * Borne supérieure pour les rangs des sous-ensembles.
	 */
	private static final int RANK_LIMIT = 128;
	/**
	 * Sous-ensemble de rang maximal à mettre dans le drapeau de la liste.
	 */
	private final SubSet flagValue = new SubSet(RANK_LIMIT, new SmallSet());

	private static final String NEW_VALUE = " nouveau contenu :"; // demande de SonarLint

	/**
	 * Entrée standard.
	 */
	private static final Scanner STANDARD_INPUT = new Scanner(System.in);

	public MySet() {
		super();
		setFlag(flagValue);
	}

	/**
	 * Fermer tout (actuellement juste l'entrée standard).
	 */
	public static void closeAll() {
		STANDARD_INPUT.close();
	}

	private static Comparison compare(int a, int b) {
		if (a < b) {
			return Comparison.INF;
		} else if (a == b) {
			return Comparison.EGAL;
		} else {
			return Comparison.SUP;
		}
	}

	/**
	 * Afficher à l’écran les entiers appartenant à this, dix entiers par ligne
	 * d’écran.
	 */
	public void print() {
		System.out.println(" [version corrigee de contenu]");
		this.print(System.out);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// //////////// Appartenance, Ajout, Suppression, Cardinal
	// ////////////////////
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * @return true si le nombre saisi par l'utilisateur appartient à this, false
	 *         sinon
	 */
	public boolean contains() {
		System.out.println(" valeur cherchee : ");
		int value = readValue(STANDARD_INPUT, 0);
		return this.containsValue(value);
	}

	/**
	 * @param value valeur à tester
	 * @return true si value appartient à l'ensemble, false sinon
	 */

	public boolean containsValue(int value) {
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			if (it.getValue().rank == value / 256 && it.getValue().set.contains(value % 256)) {
				return true;
			}
			it.goForward();
		}
		return false;
	}

	/**
	 * Ajouter à this toutes les valeurs saisies par l'utilisateur et afficher le
	 * nouveau contenu (arrêt par lecture de -1).
	 */
	public void add() {
		System.out.println(" valeurs a ajouter (-1 pour finir) : ");
		this.addAllFromStream(System.in);
		this.printNewState();
	}

	/**
	 * Ajouter à this toutes les valeurs prises dans is.
	 * 
	 * @param is flux d'entrée.
	 */
	public void addAllFromStream(InputStream is) {
		int value = readValue(STANDARD_INPUT, -1);
		while (value != -1) {
			this.addNumber(value);
			value = readValue(STANDARD_INPUT, -1);
		}
	}

	/**
	 * Ajouter element à this.
	 *
	 * @param element valuer à ajouter.
	 */
	public void addNumber(int value) {
		Iterator<SubSet> it = this.iterator();

		while (!it.isOnFlag() && it.getValue().rank < value / 256) {
			it.goForward();
		}
		if (!it.isOnFlag() && it.getValue().rank == value / 256) {
			it.getValue().set.add(value % 256);
		}
		if (!it.isOnFlag() && it.getValue().rank > value / 256) {
			SubSet newSubSet = new SubSet(value / 256, new SmallSet());
			newSubSet.set.add(value % 256);
			it.addLeft(newSubSet);
		}
	}

	/**
	 * Supprimer de this toutes les valeurs saisies par l'utilisateur et afficher le
	 * nouveau contenu (arrêt par lecture de -1).
	 */
	public void remove() {
		System.out.println("  valeurs a supprimer (-1 pour finir) : ");
		this.removeAllFromStream(System.in);
		this.printNewState();
	}

	/**
	 * Supprimer de this toutes les valeurs prises dans is.
	 * 
	 * @param is flux d'entrée
	 */
	public void removeAllFromStream(InputStream is) {
		int valeur = readValue(STANDARD_INPUT, -1);
		while (valeur != -1) {
			this.removeNumber(valeur);
			valeur = readValue(STANDARD_INPUT, -1);
		}
	}

	/**
	 * Supprimer element de this.
	 * 
	 * @param element valeur à supprimer
	 */
	public void removeNumber(int value) {
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag() && it.getValue().rank < value / 256) {
			it.goForward();
		}
		if (!it.isOnFlag() && it.getValue().rank == value / 256) {
			it.getValue().set.remove(value);
		}
	}

	/**
	 * @return taille de l'ensemble this
	 */
	public int size() {
		Iterator<SubSet> it = this.iterator();
		int count = 0;
		while (!it.isOnFlag()) {
			count = count + it.getValue().set.size();
			it.goForward();
		}
		return count == 0 ? -1 : count;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// /////// Difference, DifferenceSymetrique, Intersection, Union ///////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * This devient la différence de this et set2.
	 * 
	 * @param set2 deuxième ensemble
	 */
	public void difference(MySet set2) {
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
		System.out.println("---------- fonction à écrire -------------");
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
	}

	/**
	 * This devient la différence symétrique de this et set2.
	 * 
	 * @param set2 deuxième ensemble
	 */
	public void symmetricDifference(MySet set2) {
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
		System.out.println("---------- fonction à écrire -------------");
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
	}

	/**
	 * This devient l'intersection de this et set2.
	 * 
	 * @param set2 deuxième ensemble
	 */
	public void intersection(MySet set2) {
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
		System.out.println("---------- fonction à écrire -------------");
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
	}

	/**
	 * This devient l'union de this et set2.
	 * 
	 * @param set2 deuxième ensemble
	 */
	public void union(MySet set2) {
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
		System.out.println("---------- fonction à écrire -------------");
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
	}

	// /////////////////////////////////////////////////////////////////////////////
	// /////////////////// Egalit�, Inclusion ////////////////////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * @param o deuxième ensemble
	 * 
	 * @return true si les ensembles this et o sont égaux, false sinon
	 */
	@Override
	public boolean equals(Object o) {
		boolean b = true;
		if (this == o) {
			b = true;
		} else if (o == null) {
			b = false;
		} else if (!(o instanceof MySet)) {
			b = false;
		} else {
			MySet no = (MySet) o;
			Iterator<SubSet> it1 = this.iterator();
			Iterator<SubSet> it2 = no.iterator();

			b = false;
		}
		return b;
	}

	/**
	 * @param set2 deuxième ensemble
	 * @return true si this est inclus dans set2, false sinon
	 */
	public boolean isIncludedIn(MySet set2) {
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
		System.out.println("---------- fonction à écrire -------------");
		System.out.println("------------------------------------------");
		System.out.println("------------------------------------------");
		return false;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// //////// Rangs, Restauration, Sauvegarde, Affichage //////////////
	// /////////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
		int i = 1;
		int result = 0;
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			SubSet ss = it.getValue();
			result += i * ss.rank * ss.set.hashCode();
			i *= 31;
			it.goForward();
		}
		return result;
	}

	/**
	 * Afficher les rangs présents dans this.
	 */
	public void printRanks() {
		System.out.println(" [version corrigee de rangs]");
		this.printRanksAux();
	}

	private void printRanksAux() {
		int count = 0;
		Iterator<SubSet> it = this.iterator();
		StringBuilder line = new StringBuilder("Rangs presents : ");
		while (!it.isOnFlag()) {
			line.append(it.getValue().rank).append("  ");
			count = count + 1;
			if (count == 10) {
				line.append("\n");
				count = 0;
			}
			it.goForward();

		}
		System.out.println(line.toString());
		if (count > 0) {
			System.out.println("\n");
		}
	}

	/**
	 * Créer this à partir d’un fichier choisi par l’utilisateur contenant une
	 * séquence d’entiers positifs terminée par -1 (cf f0.ens, f1.ens, f2.ens,
	 * f3.ens et f4.ens).
	 */
	public void restore() {
		String fileName = readFileName();
		InputStream inFile;
		try {
			inFile = new FileInputStream(fileName);
			System.out.println(" [version corrigee de restauration]");
			this.clear();
			this.addAllFromStream(inFile);
			inFile.close();
			System.out.println(NEW_VALUE);
			this.printNewState();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("fichier " + fileName + " inexistant");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier " + fileName);
		}
	}

	/**
	 * Sauvegarder this dans un fichier d’entiers positifs terminé par -1.
	 */
	public void save() {
		System.out.println(" [version corrigee de sauvegarde]");
		OutputStream outFile;
		try {
			outFile = new FileOutputStream(readFileName());
			this.print(outFile);
			outFile.write("-1\n".getBytes());
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("pb ouverture fichier lors de la sauvegarde");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier");
		}
	}

	/**
	 * @return copie profonde de this
	 */
	@Override
	public MySet copyOf() {
		MySet copie = new MySet();
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			copie.addTail(it.getValue().copyOf());
			it.goForward();
		}
		return copie;
	}

	/**
	 * @return l'ensemble this sous forme de chaîne de caractères.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		int count = 0;
		SubSet subSet;
		int startValue;
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			subSet = it.getValue();
			startValue = subSet.rank * 256;
			for (int i = 0; i < 256; ++i) {
				if (subSet.set.contains(i)) {
					StringBuilder number = new StringBuilder(String.valueOf(startValue + i));
					int numberLength = number.length();
					for (int j = 6; j > numberLength; --j) {
						number.append(" ");
					}
					result.append(number);
					++count;
					if (count == 10) {
						result.append("\n");
						count = 0;
					}
				}
			}
			it.goForward();
		}
		if (count > 0) {
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Imprimer this dans outFile.
	 *
	 * @param outFile flux de sortie
	 */
	private void print(OutputStream outFile) {
		try {
			String string = this.toString();
			outFile.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Afficher l'ensemble avec sa taille et les rangs présents.
	 */
	private void printNewState() {
		this.print(System.out);
		int size = this.size();
		System.out.println("Nombre d'elements : " + size);
		this.printRanksAux();
	}

	/**
	 * @param scanner
	 * @param min     valeur minimale possible
	 * @return l'entier lu au clavier (doit être entre min et 32767)
	 */
	private static int readValue(Scanner scanner, int min) {
		int value = scanner.nextInt();
		while (value < min || value > 32767) {
			System.out.println("valeur incorrecte");
		}
		return value;
	}

	/**
	 * @return nom de fichier saisi psar l'utilisateur
	 */
	private static String readFileName() {
		System.out.println(" nom du fichier : ");
		return STANDARD_INPUT.next();
	}
}