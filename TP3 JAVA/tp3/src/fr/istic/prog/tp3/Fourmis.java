package fr.istic.prog.tp3;

/**
 * @author CHEUKAP TATOUN CHRISTIAN REGIS et LEBAS ENZO
 * @version 6.0
 * @since 2026-02-24
 * 
 */

public class Fourmis {

	/**
	 * @param s un terme de la suite des fourmis
	 * @pre s.length() > 0
	 * @return le terme suivant de la suite des fourmis
	 */
	public static String nextTerm(String s) {

		String ui = String.valueOf(s.charAt(0));
		StringBuilder result = new StringBuilder();
		int compteur = 0;
		for (int i = 0; i < s.length(); i++) {
			if (String.valueOf(s.charAt(i)).equals(ui)) {
				compteur++;
			} else {
				result.append(compteur);
				result.append(s.charAt(i - 1));
				compteur = 1;
				ui = String.valueOf(s.charAt(i));

			}
		}
		result.append(compteur);
		result.append(String.valueOf(s.charAt(s.length() - 1)));
		return result.toString();
	}

	/**
	 * @param n indice de la suite
	 * @return n-ième élément de la suite des fourmis
	 */
	public static String generate(int n) {
		String result = "1";
		for (int i = 1; i <= n; i++) {
			result = Fourmis.nextTerm(result);
		}

		return result;
	}

	public static void main(String[] args) {
		int n = 10;
		System.out.println("Le 0ème terme de la suite des fourmis est : 1");
		for (int i = 1; i <= n; i++) {
			System.out.println("Le " + i + "ème terme de la suite des fourmis est : " + Fourmis.generate(i));
		}
	}

}