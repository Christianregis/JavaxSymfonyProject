package fr.istic.prog.tp3.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import fr.istic.prog.tp3.Fourmis;
import fr.istic.prog.tp3.InsertionInteger;
import fr.istic.prog.tp3.InsertionPair;
import fr.istic.prog.tp3.Pair;

/**
 * Tests du sujet PRG TP3 2026/2027 (JUnit Jupiter 5.9 ou plus recent dans la
 * branche 5.x, Java 11 ou plus). Placer ce fichier dans le dossier de sources
 * de tests, package fr.istic.prog.tp3.test. Dependances : junit-jupiter-api,
 * junit-jupiter-params et junit-jupiter-engine (ou la dependance agregatrice
 * junit-jupiter). Sous Eclipse : bibliotheque JUnit 5.
 *
 * Les collections et tris utilises ICI sont des oracles de test :
 * l'interdiction du sujet concerne les classes rendues, pas les tests. Aucun
 * comportement impose hors preconditions : null, chaine vide, entiers non
 * positifs pour insert, flux sans sentinelle ou contenant du texte. Pair
 * accepte des int : les comparaisons testent aussi leurs valeurs extremes.
 * copyOf peut retourner this, puisque Pair est immuable.
 *
 * Le delai par test interrompt les boucles avec JUnit. Une boucle qui ignore
 * les interruptions peut necessiter d'arreter le processus de test dans l'IDE.
 */
@DisplayName("TP3 : contrats, cas limites et erreurs frequentes")
@Timeout(value = 5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class TestTP3 {
	private static final String[] TERMES = { "1", "11", "21", "1211", "111221", "312211", "13112221", "1113213211",
			"31131211131221", "13211311123113112211" };

	static Stream<Arguments> termesFourmis() {
		return java.util.stream.IntStream.range(0, TERMES.length - 1)
				.mapToObj(i -> Arguments.of(TERMES[i], TERMES[i + 1]));
	}

	static Stream<Arguments> comparaisons() {
		return Stream.of(Arguments.of(1, 2, 1, 2, 0), Arguments.of(1, 2, 1, 3, -1), Arguments.of(1, 3, 1, 2, 1),
				Arguments.of(1, 99, 2, 1, -1), Arguments.of(2, 1, 1, 99, 1), Arguments.of(1, 9, 9, 1, -1),
				Arguments.of(0, 0, 0, 1, -1), Arguments.of(-2, 9, -1, -9, -1),
				Arguments.of(Integer.MIN_VALUE, 1, Integer.MAX_VALUE, 1, -1),
				Arguments.of(Integer.MAX_VALUE, 1, Integer.MIN_VALUE, 1, 1),
				Arguments.of(1, Integer.MIN_VALUE, 1, Integer.MAX_VALUE, -1),
				Arguments.of(1, Integer.MAX_VALUE, 1, Integer.MIN_VALUE, 1));
	}

	static Stream<Arguments> lecturesEntiers() {
		return Stream.of(Arguments.of("-1", new int[] {}), Arguments.of("8 -1", new int[] { 8 }),
				Arguments.of("3 18 1 4 3 2 1 3 -1", new int[] { 1, 2, 3, 4, 18 }),
				Arguments.of("9 8 7 6 5 4 3 2 1 -1", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }),
				Arguments.of("5 5 5 5 5 5 5 5 5 5 5 2 7 -1", new int[] { 2, 5, 7 }),
				Arguments.of(" \n 3\t1\r\n2  -1 ", new int[] { 1, 2, 3 }),
				Arguments.of("2147483647 1 2147483646 -1", new int[] { 1, 2147483646, 2147483647 }));
	}

	@Nested
	@DisplayName("Fourmis")
	class FourmisTests {
		@ParameterizedTest(name = "next({0}) = {1}")
		@MethodSource("fr.istic.prog.tp3.test.TestTP3#termesFourmis")
		void termesConnus(String entree, String attendu) {
			assertEquals(attendu, Fourmis.nextTerm(entree), "Compter les groupes consecutifs");
		}

		@Test
		void appelsIndependants() {
			assertEquals("111221", Fourmis.nextTerm("1211"));
			assertEquals("11", Fourmis.nextTerm("1"));
			assertEquals("1211", Fourmis.nextTerm("21"),
					"Un compteur ou un StringBuilder ne doit pas survivre a l'appel");
		}
	}

	@Nested
	@DisplayName("InsertionInteger")
	class EntiersTests {
		@Test
		void constructionVide() {
			verifier(new InsertionInteger());
		}

		@Test
		void debutMilieuFinEtDecalagesMultiples() {
			InsertionInteger c = new InsertionInteger();
			assertTrue(c.insert(40));
			verifier(c, 40);
			assertTrue(c.insert(20));
			verifier(c, 20, 40);
			assertTrue(c.insert(30));
			verifier(c, 20, 30, 40);
			assertTrue(c.insert(50));
			verifier(c, 20, 30, 40, 50);
			assertTrue(c.insert(10));
			verifier(c, 10, 20, 30, 40, 50);
			assertTrue(c.insert(25));
			verifier(c, 10, 20, 25, 30, 40, 50);
		}

		@ParameterizedTest
		@ValueSource(ints = { 10, 20, 30, 40, 50 })
		void doublonAToutePosition(int valeur) {
			InsertionInteger c = entiers(10, 20, 30, 40, 50);
			for (int i = 0; i < 15; i++) {
				assertFalse(c.insert(valeur), "Un doublon retourne false");
				verifier(c, 10, 20, 30, 40, 50);
			}
			for (int v : new int[] { 60, 70, 80, 90, 100 })
				assertTrue(c.insert(v));
			verifier(c, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
		}

		@Test
		void dixiemeInsertionAvecDecalageEtRefusDesSuivantes() {
			InsertionInteger c = entiers(20, 30, 40, 50, 60, 70, 80, 90, 100);
			assertTrue(c.insert(10), "La dixieme place est utilisable");
			int[] plein = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
			for (int v : new int[] { 1, 15, 55, 101, Integer.MAX_VALUE, 10, 50, 100 }) {
				assertFalse(c.insert(v), "Plein : ni remplacement ni depassement");
				verifier(c, plein);
			}
		}

		@Test
		void valeursPositivesExtremes() {
			InsertionInteger c = entiers(Integer.MAX_VALUE, 1, Integer.MAX_VALUE - 1, 2);
			verifier(c, 1, 2, Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
		}

		@Test
		void copiesDefensivesDansLesDeuxSens() {
			InsertionInteger c = entiers(20, 40);
			int[] ancienne = c.toArray();
			int[] autre = c.toArray();
			ancienne[0] = 999;
			verifier(c, 20, 40);
			assertArrayEquals(new int[] { 20, 40 }, autre);
			assertTrue(c.insert(10));
			assertArrayEquals(new int[] { 999, 40 }, ancienne,
					"Une insertion ne doit pas modifier une copie deja retournee");
			assertArrayEquals(new int[] { 20, 40 }, autre);
			verifier(c, 10, 20, 40);
		}

		@Test
		void instancesIndependantes() {
			InsertionInteger a = entiers(3, 1);
			InsertionInteger b = entiers(8);
			verifier(a, 1, 3);
			verifier(b, 8);
			assertTrue(a.insert(2));
			assertTrue(b.insert(7));
			verifier(a, 1, 2, 3);
			verifier(b, 7, 8);
		}

		@ParameterizedTest
		@MethodSource("fr.istic.prog.tp3.test.TestTP3#lecturesEntiers")
		void lecture(String texte, int[] attendu) {
			InsertionInteger c = new InsertionInteger();
			try (Scanner scanner = new Scanner(texte)) {
				c.createArray(scanner);
			}
			verifier(c, attendu);
		}

		@Test
		void consommeSentinelleSansLireLaSuiteEtNeFermePasScanner() {
			InsertionInteger c = new InsertionInteger();
			try (Scanner s = new Scanner("3 1 -1 77 88")) {
				c.createArray(s);
				verifier(c, 1, 3);
				assertEquals(77, s.nextInt());
				assertEquals(88, s.nextInt());
			}
		}

		@Test
		void pleinContinueLectureSansGarderLesDixPlusPetits() {
			InsertionInteger c = new InsertionInteger();
			try (Scanner s = new Scanner("20 30 40 50 60 70 80 90 100 110 1 2 20 999 -1 777")) {
				c.createArray(s);
				verifier(c, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);
				assertEquals(777, s.nextInt(), "Lire jusqu'a -1 meme lorsque le tableau est plein");
			}
		}

		@Test
		void lecturesSuccessivesAjoutentSansReinitialiser() {
			InsertionInteger c = entiers(8);
			try (Scanner s = new Scanner("3 8 -1 1 3 -1 -1")) {
				c.createArray(s);
				verifier(c, 3, 8);
				c.createArray(s);
				verifier(c, 1, 3, 8);
				c.createArray(s);
				verifier(c, 1, 3, 8);
			}
		}

		@Test
		void toutesLesPermutationsDeSixValeurs() {
			permutations(new int[] { 1, 2, 3, 4, 5, 6 }, 0, ordre -> {
				InsertionInteger c = new InsertionInteger();
				TreeSet<Integer> oracle = new TreeSet<>();
				for (int v : ordre) {
					assertTrue(c.insert(v), Arrays.toString(ordre));
					oracle.add(v);
					verifier(c, oracle.stream().mapToInt(Integer::intValue).toArray());
				}
			});
		}

		@Test
		void sequencesReproductiblesAvecOracle() {
			for (int graine = 0; graine < 50; graine++) {
				Random hasard = new Random(graine);
				InsertionInteger c = new InsertionInteger();
				TreeSet<Integer> oracle = new TreeSet<>();
				for (int i = 0; i < 100; i++) {
					int v = 1 + hasard.nextInt(25);
					boolean attendu = oracle.size() < 10 && !oracle.contains(v);
					if (attendu)
						oracle.add(v);
					assertEquals(attendu, c.insert(v), "Graine=" + graine + ", insertion=" + i);
					verifier(c, oracle.stream().mapToInt(Integer::intValue).toArray());
				}
			}
		}
	}

	@Nested
	@DisplayName("Pair")
	class PairTests {
		@ParameterizedTest
		@MethodSource("fr.istic.prog.tp3.test.TestTP3#comparaisons")
		void ordreLexicographiqueSansSoustractionDangereuse(int x, int y, int u, int v, int signe) {
			Pair a = new Pair(x, y);
			Pair b = new Pair(u, v);
			assertEquals(signe, Integer.signum(a.compareTo(b)));
			assertEquals(-signe, Integer.signum(b.compareTo(a)));
			assertEquals(0, a.compareTo(a));
		}

		@Test
		void egaliteDeValeurEtPasIdentite() {
			Pair a = new Pair(2, 7);
			Pair b = new Pair(2, 7);
			Pair c = new Pair(2, 7);
			assertNotSame(a, b);
			assertTrue(a.equals(a));
			assertTrue(a.equals(b));
			assertTrue(b.equals(a));
			assertTrue(b.equals(c));
			assertTrue(a.equals(c));
			assertFalse(a.equals(new Pair(2, 8)), "Verifier aussi y");
			assertFalse(a.equals(new Pair(3, 7)), "Verifier aussi x");
			assertFalse(a.equals(new Pair(7, 2)), "Ne pas comparer seulement les sommes");
		}

		@Test
		void equalsAccepteObjectEtAutresTypesSansException() {
			Object a = new Pair(2, 7);
			Object b = new Pair(2, 7);
			assertTrue(a.equals(b), "Il faut redefinir equals(Object), pas surcharger equals(Pair)");
			assertFalse(a.equals("(2,7)"));
			assertFalse(a.equals(Integer.valueOf(2)));
			assertFalse(a.equals(new Object()));
		}

		@Test
		void transitivite() {
			Pair[] ordre = { new Pair(-1, 99), new Pair(0, -5), new Pair(0, 2), new Pair(1, -99), new Pair(1, 4),
					new Pair(Integer.MAX_VALUE, 0) };
			for (int i = 0; i < ordre.length; i++)
				for (int j = i + 1; j < ordre.length; j++)
					for (int k = j + 1; k < ordre.length; k++) {
						assertTrue(ordre[i].compareTo(ordre[j]) < 0);
						assertTrue(ordre[j].compareTo(ordre[k]) < 0);
						assertTrue(ordre[i].compareTo(ordre[k]) < 0);
					}
		}

		@Test
		void copieDeMemeValeurEtInstancesIndependantes() {
			Pair a = new Pair(2, 7);
			Pair copie = a.copyOf();
			assertNotNull(copie);
			assertEquals(new Pair(2, 7), copie);
			assertEquals(0, a.compareTo(copie));
			Pair autre = new Pair(99, 100);
			assertEquals(new Pair(2, 7), a, "Les champs ne doivent pas etre static");
			assertEquals(new Pair(2, 7), copie);
			assertTrue(a.compareTo(autre) < 0);
			assertEquals(new Pair(2, 7), a);
			// Pas de assertNotSame : partager un objet immuable est autorise.
		}

		@Test
		void toStringStableEtSansModification() {
			Pair a = new Pair(12, 34);
			String texte = a.toString();
			assertNotNull(texte);
			assertFalse(texte.trim().isEmpty());
			assertEquals(texte, a.toString());
			assertEquals(new Pair(12, 34), a);
		}
	}

	@Nested
	@DisplayName("InsertionPair")
	class DoubletsTests {
		@Test
		void constructionVide() {
			verifier(new InsertionPair(), new int[][] {});
		}

		@Test
		void ordreLexicographiqueDebutMilieuFin() {
			InsertionPair c = new InsertionPair();
			assertTrue(c.insert(new Pair(3, 8)));
			verifier(c, new int[][] { { 3, 8 } });
			assertTrue(c.insert(new Pair(1, 4)));
			verifier(c, new int[][] { { 1, 4 }, { 3, 8 } });
			assertTrue(c.insert(new Pair(1, 3)));
			verifier(c, new int[][] { { 1, 3 }, { 1, 4 }, { 3, 8 } });
			assertTrue(c.insert(new Pair(2, 99)));
			assertTrue(c.insert(new Pair(4, 1)));
			assertTrue(c.insert(new Pair(3, 7)));
			verifier(c, new int[][] { { 1, 3 }, { 1, 4 }, { 2, 99 }, { 3, 7 }, { 3, 8 }, { 4, 1 } });
		}

		@Test
		void memeXOuMemeYNeSignifiePasDoublon() {
			InsertionPair c = doublets(new int[][] { { 2, 2 }, { 2, 3 }, { 3, 2 }, { 1, 3 }, { 3, 1 } });
			verifier(c, new int[][] { { 1, 3 }, { 2, 2 }, { 2, 3 }, { 3, 1 }, { 3, 2 } });
		}

		@Test
		void doublonsParValeurAToutesLesPositionsSansConsommerDePlace() {
			InsertionPair c = doublets(new int[][] { { 1, 9 }, { 2, 8 }, { 3, 7 }, { 4, 6 }, { 5, 5 } });
			for (int i = 1; i <= 5; i++)
				for (int j = 0; j < 12; j++) {
					assertFalse(c.insert(new Pair(i, 10 - i)), "Objet neuf, mais valeur deja presente");
					verifier(c, new int[][] { { 1, 9 }, { 2, 8 }, { 3, 7 }, { 4, 6 }, { 5, 5 } });
				}
			for (int i = 6; i <= 10; i++)
				assertTrue(c.insert(new Pair(i, 1)));
			assertEquals(10, c.toArray().length);
		}

		@Test
		void memeReferenceDeuxFois() {
			InsertionPair c = new InsertionPair();
			Pair p = new Pair(2, 3);
			assertTrue(c.insert(p));
			assertFalse(c.insert(p));
			verifier(c, new int[][] { { 2, 3 } });
		}

		@Test
		void dixiemeInsertionEtRefusQuandPlein() {
			InsertionPair c = new InsertionPair();
			int[][] attendu = new int[10][2];
			for (int i = 10; i >= 1; i--) {
				assertTrue(c.insert(new Pair(i, 5)), "Insertion numero " + (11 - i));
				attendu[i - 1] = new int[] { i, 5 };
			}
			verifier(c, attendu);
			for (Pair p : new Pair[] { new Pair(1, 1), new Pair(5, 4), new Pair(11, 1), new Pair(1, 5), new Pair(5, 5),
					new Pair(10, 5) }) {
				assertFalse(c.insert(p));
				verifier(c, attendu);
			}
		}

		@Test
		void copiesDefensivesEtAbsenceDeNullDansLaPartieUtile() {
			InsertionPair c = doublets(new int[][] { { 2, 8 }, { 4, 6 } });
			Pair[] avant = c.toArray();
			Pair[] autre = c.toArray();
			avant[0] = null;
			avant[1] = new Pair(99, 99);
			verifier(c, new int[][] { { 2, 8 }, { 4, 6 } });
			assertEquals(new Pair(2, 8), autre[0]);
			assertTrue(c.insert(new Pair(1, 9)));
			assertNull(avant[0]);
			assertEquals(new Pair(99, 99), avant[1]);
			assertArrayEquals(new Pair[] { new Pair(2, 8), new Pair(4, 6) }, autre);
			verifier(c, new int[][] { { 1, 9 }, { 2, 8 }, { 4, 6 } });
		}

		@Test
		void instancesIndependantes() {
			InsertionPair a = doublets(new int[][] { { 2, 8 } });
			InsertionPair b = doublets(new int[][] { { 1, 9 } });
			assertTrue(a.insert(new Pair(3, 7)));
			verifier(a, new int[][] { { 2, 8 }, { 3, 7 } });
			verifier(b, new int[][] { { 1, 9 } });
		}

		@Test
		void exempleDuSujetEtEspacesVaries() {
			InsertionPair c = new InsertionPair();
			try (Scanner s = new Scanner(" 3\t8\n1 4\r\n3 8 1 3 -1")) {
				c.createArray(s);
			}
			verifier(c, new int[][] { { 1, 3 }, { 1, 4 }, { 3, 8 } });
		}

		@Test
		void sentinelleEnPremiereComposanteSansLireLaSeconde() {
			InsertionPair c = new InsertionPair();
			try (Scanner s = new Scanner("-1 77 88")) {
				c.createArray(s);
				verifier(c, new int[][] {});
				assertEquals(77, s.nextInt());
				assertEquals(88, s.nextInt());
			}
		}

		@Test
		void sentinelleSeuleEnFinDeFlux() {
			InsertionPair c = new InsertionPair();
			try (Scanner s = new Scanner("2 3 -1")) {
				c.createArray(s);
			}
			verifier(c, new int[][] { { 2, 3 } });
		}

		@Test
		void sentinelleEnSecondeComposanteIgnoreDoubletIncomplet() {
			InsertionPair c = new InsertionPair();
			try (Scanner s = new Scanner("3 8 1 4 2 -1 77 88")) {
				c.createArray(s);
				verifier(c, new int[][] { { 1, 4 }, { 3, 8 } });
				assertEquals(77, s.nextInt());
				assertEquals(88, s.nextInt());
			}
		}

		@Test
		void aucunDoubletComplet() {
			InsertionPair c = new InsertionPair();
			try (Scanner s = new Scanner("7 -1")) {
				c.createArray(s);
			}
			verifier(c, new int[][] {});
		}

		@ParameterizedTest
		@ValueSource(strings = { "-1", "42 -1" })
		void pleinContinueLectureJusquaChaqueTypeDeSentinelle(String fin) {
			InsertionPair c = new InsertionPair();
			StringBuilder texte = new StringBuilder();
			int[][] attendu = new int[10][2];
			for (int i = 10; i >= 1; i--) {
				texte.append(i).append(" 5 ");
				attendu[i - 1] = new int[] { i, 5 };
			}
			texte.append("1 1 99 99 5 5 ").append(fin).append(" 777");
			try (Scanner s = new Scanner(texte.toString())) {
				c.createArray(s);
				verifier(c, attendu);
				assertEquals(777, s.nextInt());
			}
		}

		@Test
		void lecturesSuccessivesSansReinitialisation() {
			InsertionPair c = doublets(new int[][] { { 3, 8 } });
			try (Scanner s = new Scanner("1 4 -1 1 3 3 8 9 -1 -1")) {
				c.createArray(s);
				verifier(c, new int[][] { { 1, 4 }, { 3, 8 } });
				c.createArray(s);
				verifier(c, new int[][] { { 1, 3 }, { 1, 4 }, { 3, 8 } });
				c.createArray(s);
				verifier(c, new int[][] { { 1, 3 }, { 1, 4 }, { 3, 8 } });
			}
		}

		@Test
		void lectureDepuisVraiFichier(@TempDir Path dossier) throws IOException {
			Path fichier = dossier.resolve("doublets.txt");
			Files.write(fichier, "3 8\n1 4\n3 8\n1 3\n-1\n".getBytes(StandardCharsets.UTF_8));
			InsertionPair c = new InsertionPair();
			try (Scanner s = new Scanner(fichier, StandardCharsets.UTF_8)) {
				c.createArray(s);
			}
			verifier(c, new int[][] { { 1, 3 }, { 1, 4 }, { 3, 8 } });
			// Ceci teste createArray sur fichier, pas le dialogue du main interactif.
		}

		@Test
		void toutesLesPermutationsDeSixDoublets() {
			int[][] ordre = { { 1, 1 }, { 1, 9 }, { 2, 2 }, { 2, 8 }, { 3, 1 }, { 3, 9 } };
			permutations(new int[] { 0, 1, 2, 3, 4, 5 }, 0, indices -> {
				InsertionPair c = new InsertionPair();
				TreeSet<Integer> presents = new TreeSet<>();
				for (int i : indices) {
					assertTrue(c.insert(new Pair(ordre[i][0], ordre[i][1])));
					presents.add(i);
					verifier(c, presents.stream().map(j -> ordre[j]).toArray(int[][]::new));
				}
			});
		}

		@Test
		void sequencesReproductiblesAvecOracleIndependantDePair() {
			for (int graine = 0; graine < 50; graine++) {
				Random hasard = new Random(graine);
				InsertionPair c = new InsertionPair();
				// x,y dans [1,6] : 10*x+y respecte l'ordre lexicographique.
				// Ne pas utiliser TreeSet<Pair>, qui dependrait du compareTo teste.
				TreeSet<Integer> oracle = new TreeSet<>();
				for (int i = 0; i < 100; i++) {
					int x = 1 + hasard.nextInt(6);
					int y = 1 + hasard.nextInt(6);
					int code = 10 * x + y;
					boolean attendu = oracle.size() < 10 && !oracle.contains(code);
					if (attendu)
						oracle.add(code);
					assertEquals(attendu, c.insert(new Pair(x, y)), "Graine=" + graine + ", insertion=" + i);
					verifier(c, oracle.stream().map(n -> new int[] { n / 10, n % 10 }).toArray(int[][]::new));
				}
			}
		}

		@Test
		void toStringSansEffetDeBord() {
			InsertionPair c = new InsertionPair();
			assertNotNull(c.toString());
			verifier(c, new int[][] {});
			assertTrue(c.insert(new Pair(3, 8)));
			assertTrue(c.insert(new Pair(1, 4)));
			String texte = c.toString();
			assertNotNull(texte);
			assertFalse(texte.trim().isEmpty());
			assertEquals(texte, c.toString());
			verifier(c, new int[][] { { 1, 4 }, { 3, 8 } });
			assertTrue(c.insert(new Pair(2, 6)));
			verifier(c, new int[][] { { 1, 4 }, { 2, 6 }, { 3, 8 } });
		}
	}

	private static InsertionInteger entiers(int... valeurs) {
		InsertionInteger c = new InsertionInteger();
		for (int v : valeurs)
			assertTrue(c.insert(v), "Preparation : insertion de " + v);
		return c;
	}

	private static InsertionPair doublets(int[][] valeurs) {
		InsertionPair c = new InsertionPair();
		for (int[] v : valeurs)
			assertTrue(c.insert(new Pair(v[0], v[1])));
		return c;
	}

	private static void verifier(InsertionInteger c, int... attendu) {
		int[] obtenu = c.toArray();
		assertNotNull(obtenu);
		assertArrayEquals(attendu, obtenu, "Valeurs utiles seulement, distinctes et triees");
		assertTrue(obtenu.length <= 10);
		for (int i = 0; i < obtenu.length; i++) {
			assertTrue(obtenu[i] > 0);
			if (i > 0)
				assertTrue(obtenu[i - 1] < obtenu[i]);
		}
	}

	private static void verifier(InsertionPair c, int[][] attendu) {
		Pair[] obtenu = c.toArray();
		assertNotNull(obtenu);
		assertEquals(attendu.length, obtenu.length, "Longueur = nombre de valeurs utiles");
		assertTrue(obtenu.length <= 10);
		for (int i = 0; i < attendu.length; i++) {
			assertNotNull(obtenu[i], "Case utile " + i + " non initialisee");
			Pair p = new Pair(attendu[i][0], attendu[i][1]);
			assertEquals(p, obtenu[i], "Doublet incorrect a l'indice " + i);
			assertEquals(0, p.compareTo(obtenu[i]), "Comparer les deux composantes");
			if (i > 0)
				assertTrue(obtenu[i - 1].compareTo(obtenu[i]) < 0);
		}
	}

	private static void permutations(int[] valeurs, int debut, java.util.function.Consumer<int[]> action) {
		if (debut == valeurs.length) {
			action.accept(valeurs.clone());
			return;
		}
		for (int i = debut; i < valeurs.length; i++) {
			int tmp = valeurs[debut];
			valeurs[debut] = valeurs[i];
			valeurs[i] = tmp;
			permutations(valeurs, debut + 1, action);
			tmp = valeurs[debut];
			valeurs[debut] = valeurs[i];
			valeurs[i] = tmp;
		}
	}
}
