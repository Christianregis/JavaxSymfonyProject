package fr.istic.l3miage.prg.list.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import fr.istic.l3miage.prg.list_util.Iterator;
import fr.istic.l3miage.prg.list_util.SmallSet;
import fr.istic.l3miage.prg.list.MySet;
import fr.istic.l3miage.prg.list.SubSet;

/**
 *
 * MODE D'EMPLOI POUR L'ÉTUDIANT
 * 
 * 1. Commencer par les tests portant le tag "pédagogique" : chaque test montre
 * des données courtes, un appel direct et un résultat attendu explicite.
 * 
 * 2. En cas d'échec, lire le message dans JUnit, puis ouvrir la méthode de test
 * nommée dans la pile d'appels (les auxiliaires peuvent apparaître avant elle).
 * 
 * 3. Pour les tests générés, relever les ensembles AVANT et ARPÈS l'opération.
 * Les tests aléatoires affichent les données complètes de l'étape fautive.
 * 
 * 4. Les messages indiquent les constats ; leur cause est à rechercher dans
 * votre code.
 * 
 * Les grandes listes de valeurs sont abrégées à 24 valeurs dans les messages
 * ordinaires ; le total et la première valeur incorrecte restent indiqués. Les
 * auxiliaires de création et vérification n'appellent aucune méthode à
 * completer : une erreur dans addNumber ne fausse pas toutes les donnees.
 * 
 * Java 8+, JUnit Jupiter 5.9 (API, Params et Engine).
 * 
 * Les fixtures sont construites avec addTail/SubSet/SmallSet, jamais avec
 * addNumber, les flux, copyOf ou les opérations ensemblistes à tester. Les
 * résultats attendus sont représentes par des TreeSet<Integer>, contenant
 * uniquement les entiers présents. La vérification lit directement les blocs :
 * elle ne dépend ni de containsValue, ni de size, ni de equals de MySet.
 *
 * Contrat retenu : valeurs 0..32767 ; rangs strictement croissants 0..127 ;
 * aucun bloc vide ; drapeau de rang 128 avec un SmallSet vide ; opérande droit
 * inchangé s'il est distinct de this ; pas de partage de blocs mutables. Les
 * flux sont des suites d'entiers valides terminées par -1. Les comportements
 * non spécifies (null hors equals, entiers hors domaine, texte invalide, EOF
 * sans -1, fermeture du flux) ne sont pas imposés.
 *
 * Tag "complexite" : budgets PÉDAGOGIQUES linéaires et ajustables ; ce ne sont
 * ni des nombres exacts d'opérations, ni une preuve asymptotique. Le compteur
 * fourni ignore notamment getValue, isOnFlag et les opérations de SmallSet,
 * ainsi que les opérations effectuées sur d'éventuelles listes temporaires.
 *
 * Timeout signale les boucles anormales, mais Java ne permet pas de tuer
 * proprement une boucle ignorant les interruptions : arreter le lancement JUnit
 * dans l'IDE si necessaire. Aucun test ne remplace System.in/System.out.
 */
@Execution(ExecutionMode.SAME_THREAD)
@Timeout(value = 15, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class TestMySet {
	private static final int BLOCK = 256;
	private static final int RANKS = 128;
	private static final int LIMIT = BLOCK * RANKS;

	// Constantes et budgets
	private static final int FIXED_COST = 4;
	private static final int SINGLE_FACTOR = 1;
	private static final int BINARY_FACTOR = 1;

	enum Operation {
		UNION, INTERSECTION, DIFFERENCE, SYMMETRIC_DIFFERENCE
	}

	// ================================================================
	// EXEMPLES PEDAGOGIQUES : lire d'abord ces tests dans Eclipse.
	// creerEnsemble prépare les blocs sans appeler votre addNumber.
	// verifierContenuEtStructure lit les blocs sans appeler votre equals.
	// ================================================================

	@Test
	@Tag("pedagogique")
	@DisplayName("containsValue : recherche dans un singleton")
	void appartenanceDansUnSingleton() {
		MySet a = creerEnsemble(259);
		assertTrue(a.containsValue(259), "Dans {259}, containsValue(259).");
		assertFalse(a.containsValue(3), "Dans {259}, containsValue(3).");
		verifierContenuEtStructure(a, ensemble(259), "Une recherche doit conserver {259}.");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("addNumber : ajout d'une valeur intermediaire")
	void ajoutDUneValeurIntermediaire() {
		MySet a = creerEnsemble(3, 515);
		a.addNumber(259);
		verifierContenuEtStructure(a, ensemble(3, 259, 515), "Depart={3, 515}. Appel=addNumber(259).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("addNumber : ajout d'une valeur presente")
	void ajoutDUneValeurPresente() {
		MySet a = creerEnsemble(3, 17, 259);
		a.addNumber(17);
		verifierContenuEtStructure(a, ensemble(3, 17, 259), "Depart={3, 17, 259}. Appel=addNumber(17).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("removeNumber : retrait en debut d'ensemble")
	void retraitDUneValeurEnDebut() {
		MySet a = creerEnsemble(3, 17, 259);
		a.removeNumber(3);
		verifierContenuEtStructure(a, ensemble(17, 259), "Depart={3, 17, 259}. Appel=removeNumber(3).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("removeNumber : retrait au milieu")
	void retraitDUneValeurAuMilieu() {
		MySet a = creerEnsemble(3, 259, 515);
		a.removeNumber(259);
		verifierContenuEtStructure(a, ensemble(3, 515), "Depart={3, 259, 515}. Appel=removeNumber(259).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("removeNumber : retrait d'une valeur absente")
	void retraitDUneValeurAbsente() {
		MySet a = creerEnsemble(3, 515);
		a.removeNumber(259);
		verifierContenuEtStructure(a, ensemble(3, 515), "Depart={3, 515}. Appel=removeNumber(259).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("size : cardinal d'un petit ensemble")
	void cardinalDUnPetitEnsemble() {
		MySet a = creerEnsemble(3, 17, 259);
		assertEquals(3, a.size(), "Appel de size() sur {3, 17, 259}.");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("union : deux petits ensembles")
	void unionDeDeuxPetitsEnsembles() {
		MySet a = creerEnsemble(3, 259);
		MySet b = creerEnsemble(17, 259, 515);
		a.union(b);
		verifierContenuEtStructure(a, ensemble(3, 17, 259, 515), "{3, 259}.union({17, 259, 515}).");
		verifierContenuEtStructure(b, ensemble(17, 259, 515), "union doit conserver le second ensemble.");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("intersection : deux petits ensembles")
	void intersectionDeDeuxPetitsEnsembles() {
		MySet a = creerEnsemble(3, 259);
		MySet b = creerEnsemble(17, 259);
		a.intersection(b);
		verifierContenuEtStructure(a, ensemble(259), "{3, 259}.intersection({17, 259}).");
		verifierContenuEtStructure(b, ensemble(17, 259), "intersection doit conserver le second ensemble.");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("difference : plusieurs valeurs communes")
	void differenceAvecPlusieursValeursCommunes() {
		MySet a = creerEnsemble(3, 259, 515, 771);
		MySet b = creerEnsemble(259, 515);
		a.difference(b);
		verifierContenuEtStructure(a, ensemble(3, 771), "{3, 259, 515, 771}.difference({259, 515}).");
		verifierContenuEtStructure(b, ensemble(259, 515), "difference doit conserver le second ensemble.");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("difference : deux ensembles avec recouvrement")
	void differenceDeDeuxEnsemblesAvecRecouvrement() {
		MySet a = creerEnsemble(3, 17);
		MySet b = creerEnsemble(17, 42);
		a.difference(b);
		verifierContenuEtStructure(a, ensemble(3), "{3, 17}.difference({17, 42}).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("symmetricDifference : deux petits ensembles")
	void differenceSymetriqueDeDeuxPetitsEnsembles() {
		MySet a = creerEnsemble(3, 17, 259);
		MySet b = creerEnsemble(17, 42, 515);
		a.symmetricDifference(b);
		verifierContenuEtStructure(a, ensemble(3, 42, 259, 515), "{3, 17, 259}.symmetricDifference({17, 42, 515}).");
		verifierContenuEtStructure(b, ensemble(17, 42, 515), "symmetricDifference doit conserver le second ensemble.");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("difference symetrique : meme objet comme second operande")
	void differenceSymetriqueAvecSoiMeme() {
		MySet a = creerEnsemble(3, 259);
		a.symmetricDifference(a);
		verifierContenuEtStructure(a, ensemble(), "Depart={3, 259}. Appel=a.symmetricDifference(a).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("difference : meme objet comme second operande")
	void differenceAvecSoiMeme() {
		MySet a = creerEnsemble(3, 259);
		a.difference(a);
		verifierContenuEtStructure(a, ensemble(), "Depart={3, 259}. Appel=a.difference(a).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("equals : comparaison de deux ensembles")
	void egaliteDeDeuxPetitsEnsembles() {
		MySet a = creerEnsemble(3, 259);
		MySet b = creerEnsemble(3, 273);
		assertFalse(a.equals(b), "{3, 259}.equals({3, 273}).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("isIncludedIn : comparaison dans les deux sens")
	void inclusionDUnSousEnsembleStrict() {
		MySet petit = creerEnsemble(3, 259);
		MySet grand = creerEnsemble(3, 17, 259);
		assertTrue(petit.isIncludedIn(grand), "{3, 259}.isIncludedIn({3, 17, 259}).");
		assertFalse(grand.isIncludedIn(petit), "{3, 17, 259}.isIncludedIn({3, 259}).");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("addAllFromStream : lecture d'un flux court")
	void ajoutDepuisUnFluxCourt() {
		MySet a = creerEnsemble(17);
		a.addAllFromStream(creerFlux("3 259 3 -1 515"));
		verifierContenuEtStructure(a, ensemble(3, 17, 259), "Depart={17}, flux=\"3 259 3 -1 515\".");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("removeAllFromStream : lecture d'un flux court")
	void retraitDepuisUnFluxCourt() {
		MySet a = creerEnsemble(3, 17, 259);
		a.removeAllFromStream(creerFlux("3 42 -1 259"));
		verifierContenuEtStructure(a, ensemble(17, 259), "Depart={3, 17, 259}, flux=\"3 42 -1 259\".");
	}

	@Test
	@Tag("pedagogique")
	@DisplayName("union : modification ulterieure du second ensemble")
	void unionPuisModificationDuSecondEnsemble() {
		MySet a = creerEnsemble(3);
		MySet b = creerEnsemble(259);
		a.union(b);
		verifierContenuEtStructure(a, ensemble(3, 259), "Apres {3}.union({259}).");
		b.iterator().getValue().set.add(17);
		verifierContenuEtStructure(a, ensemble(3, 259), "Contenu de a apres union, puis ajout direct de 273 dans b.");
	}

	@Test
	@Tag("complexite")
	@DisplayName("equals : cout de comparaison de deux ensembles egaux")
	void coutEgaliteDeDeuxEnsemblesEgaux() {
		MySet a = creerDixBlocs(false);
		MySet b = creerDixBlocs(false);
		a.startCount();
		b.startCount();
		boolean resultat = a.equals(b);
		int cout = a.getCount() + b.getCount();
		int budget = 2 * 10 + FIXED_COST;
		assertTrue(resultat, "Deux objets distincts contenant les memes 20 entiers doivent etre egaux.");
		verifierBudget(cout, budget, "equals : 10 blocs de 2 entiers chacun dans chaque ensemble."
				+ "\nDans chaque rang r, les entiers sont 256*r+3 et 256*r+17.");
	}

	@ParameterizedTest(name = "{0} : premiere difference au dernier des 10 blocs")
	@EnumSource(Operation.class)
	@Tag("complexite")
	void coutOperationAvecDifferenceAuDernierBloc(Operation operation) {
		TreeSet<Integer> avantA = valeursDixBlocs(false);
		TreeSet<Integer> avantB = valeursDixBlocs(true);
		MySet a = creerEnsemble(avantA);
		MySet b = creerEnsemble(avantB);
		String contexte = decrireCas(nomOperation(operation), "10 blocs peu remplis ; seul le dernier differe", avantA,
				avantB);
		int cout = mesurer(a, b, () -> appliquerOperation(operation, a, b));
		int budget = BINARY_FACTOR * 2 * 10 + FIXED_COST;
		verifierBudget(cout, budget, contexte);
		verifierContenuEtStructure(a, calculerResultatAttendu(operation, avantA, avantB), contexte);
		verifierContenuEtStructure(b, avantB, contexte + "\nLe second ensemble doit rester inchange.");
	}

	private static MySet creerDixBlocs(boolean valeurSupplementaire) {
		return creerEnsemble(valeursDixBlocs(valeurSupplementaire));
	}

	private static TreeSet<Integer> valeursDixBlocs(boolean valeurSupplementaire) {
		TreeSet<Integer> valeurs = ensemble();
		for (int rang = 0; rang < 10; rang++) {
			valeurs.add(256 * rang + 3);
			valeurs.add(256 * rang + 17);
		}
		if (valeurSupplementaire)
			valeurs.add(256 * 9 + 42);
		return valeurs;
	}

	// ================================================================
	// OUTILS DE DIAGNOSTIC : aucune methode MySet a completer n'est appelee.
	// ================================================================

	private static String afficher(TreeSet<Integer> valeurs) {
		StringBuilder texte = new StringBuilder("{");
		int nombre = 0;
		for (int valeur : valeurs) {
			if (nombre == 24) {
				texte.append(", ...");
				break;
			}
			if (nombre > 0)
				texte.append(", ");
			texte.append(valeur);
			nombre++;
		}
		return texte.append("} (cardinal ").append(valeurs.size()).append(")").toString();
	}

	private static String localiser(TreeSet<Integer> valeurs) {
		if (valeurs.isEmpty())
			return "aucune";
		int valeur = valeurs.first();
		return valeur + " (rang " + valeur / BLOCK + ", reste " + valeur % BLOCK + ")";
	}

	private static String nomOperation(Operation operation) {
		switch (operation) {
		case UNION:
			return "union";
		case INTERSECTION:
			return "intersection";
		case DIFFERENCE:
			return "difference";
		case SYMMETRIC_DIFFERENCE:
			return "symmetricDifference";
		default:
			throw new AssertionError(operation);
		}
	}

	private static String decrireCas(String operation, String nom, TreeSet<Integer> premier, TreeSet<Integer> second) {
		return "Cas : " + nom + "\nOperation : " + operation + "\nPremier ensemble AVANT : " + afficher(premier)
				+ "\nSecond ensemble AVANT  : " + afficher(second);
	}

	private static TreeSet<Integer> ensemble(int... values) {
		TreeSet<Integer> result = new TreeSet<>();
		for (int value : values) {
			if (value < 0 || value >= LIMIT) {
				throw new IllegalArgumentException("Erreur dans les donnees du test : " + value);
			}
			result.add(value);
		}
		return result;
	}

	private static TreeSet<Integer> intervalle(int begin, int endExclusive) {
		TreeSet<Integer> result = new TreeSet<>();
		for (int value = begin; value < endExclusive; value++)
			result.add(value);
		return result;
	}

	private static MySet creerEnsemble(int... values) {
		return creerEnsemble(ensemble(values));
	}

	private static MySet creerEnsemble(TreeSet<Integer> values) {
		MySet result = new MySet();
		int previousRank = -1;
		SmallSet small = null;
		// TreeSet parcourt les entiers en ordre croissant : un bloc par rang.
		for (int value : values) {
			int rank = value / BLOCK;
			if (rank != previousRank) {
				small = new SmallSet();
				result.addTail(new SubSet(rank, small));
				previousRank = rank;
			}
			small.add(value % BLOCK);
		}
		return result;
	}

	/** Lecture bornee, verification du contenu et des deux sens de chainage. */
	private static TreeSet<Integer> lireContenuEtVerifierStructure(MySet actual, String context) {
		TreeSet<Integer> result = new TreeSet<>();
		ArrayList<SubSet> nodes = new ArrayList<>();
		Iterator<SubSet> it = actual.iterator();
		int previousRank = -1;
		while (!it.isOnFlag()) {
			assertTrue(nodes.size() < RANKS, context + " : trop de blocs ou cycle sans drapeau");
			SubSet node = it.getValue();
			assertNotNull(node, context + " : bloc null");
			assertTrue(node.rank >= 0 && node.rank < RANKS, context + " : rang hors domaine");
			assertTrue(node.rank > previousRank, context + "\nRangs non tries ou dupliques");
			assertNotNull(node.set, context + " : SmallSet null");
			assertFalse(node.set.isEmpty(), context + "\nProblème avec la représentation creuse.");
			for (int offset = 0; offset < BLOCK; offset++) {
				if (node.set.contains(offset))
					result.add(node.rank * BLOCK + offset);
			}
			nodes.add(node);
			previousRank = node.rank;
			it.goForward();
		}
		SubSet flag = it.getValue();
		assertNotNull(flag, context + " : drapeau sans valeur");
		assertEquals(RANKS, flag.rank, context + " : rang du drapeau");
		assertNotNull(flag.set, context + " : SmallSet du drapeau null");
		assertTrue(flag.set.isEmpty(), context + "\nLe contenu du drapeau obtenu n'est pas vide.");
		for (int i = nodes.size() - 1; i >= 0; i--) {
			it.goBackward();
			assertFalse(it.isOnFlag(), context + " : retour premature au drapeau");
			assertSame(nodes.get(i), it.getValue(), context + " : chainage arriere incorrect");
		}
		it.goBackward();
		assertTrue(it.isOnFlag(), context + " : le voisin gauche de la tete doit etre le drapeau");
		assertEquals(result.isEmpty(), actual.isEmpty(), context + " : isEmpty incoherent");
		return result;
	}

	private static void verifierContenuEtStructure(MySet actual, TreeSet<Integer> expected, String context) {
		String diagnostic = context + "\nContenu attendu : " + afficher(expected);
		TreeSet<Integer> observed = lireContenuEtVerifierStructure(actual, diagnostic);
		if (!expected.equals(observed)) {
			TreeSet<Integer> missing = new TreeSet<>(expected);
			missing.removeAll(observed);
			TreeSet<Integer> extra = new TreeSet<>(observed);
			extra.removeAll(expected);
			fail(diagnostic + "\nContenu obtenu  : " + afficher(observed) + "\nValeurs manquantes : "
					+ afficher(missing) + "\nValeurs en trop    : " + afficher(extra) + "\nPremiere valeur manquante : "
					+ localiser(missing) + "\nPremiere valeur en trop  : " + localiser(extra));
		}
	}

	private static void appliquerOperation(Operation op, MySet a, MySet b) {
		switch (op) {
		case UNION:
			a.union(b);
			break;
		case INTERSECTION:
			a.intersection(b);
			break;
		case DIFFERENCE:
			a.difference(b);
			break;
		case SYMMETRIC_DIFFERENCE:
			a.symmetricDifference(b);
			break;
		default:
			throw new AssertionError(op);
		}
	}

	private static TreeSet<Integer> calculerResultatAttendu(Operation op, TreeSet<Integer> a, TreeSet<Integer> b) {
		// Toujours copier a : les donnees de depart ne doivent pas etre modifiees.
		TreeSet<Integer> result = new TreeSet<>(a);
		switch (op) {
		case UNION:
			result.addAll(b);
			break;
		case INTERSECTION:
			result.retainAll(b);
			break;
		case DIFFERENCE:
			result.removeAll(b);
			break;
		case SYMMETRIC_DIFFERENCE:
			for (int value : b) {
				if (!result.add(value))
					result.remove(value);
			}
			break;
		default:
			throw new AssertionError(op);
		}
		return result;
	}

	private static boolean inclusionAttendue(TreeSet<Integer> a, TreeSet<Integer> b) {
		return b.containsAll(a);
	}

	private static ByteArrayInputStream creerFlux(String text) {
		return new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII));
	}

	static Stream<Arguments> valeursAuxFrontieres() {
		ArrayList<Arguments> cas = new ArrayList<>();
		int[] restes = { 0, 1, 127, 128, 254, 255 };
		for (int rang = 0; rang < RANKS; rang++) {
			for (int reste : restes)
				cas.add(Arguments.of(rang * BLOCK + reste));
		}
		return cas.stream();
	}

	@ParameterizedTest(name = "containsValue({0})")
	@MethodSource("valeursAuxFrontieres")
	void appartenanceDansUnBlocEtDansUnEnsembleVide(int value) {
		MySet empty = creerEnsemble();
		assertFalse(empty.containsValue(value), "ensemble vide, valeur=" + value);
		verifierContenuEtStructure(empty, ensemble(), "lecture du vide");
		MySet one = creerEnsemble(value);
		assertTrue(one.containsValue(value), "valeur presente=" + value);
		assertFalse(one.containsValue((value % 2 == 0 ? value + 1 : value - 1)), "autre reste dans le meme bloc");
		assertFalse(one.containsValue((value + BLOCK) % LIMIT), "meme reste dans un autre rang");
		verifierContenuEtStructure(one, ensemble(value), "containsValue ne modifie pas this");
	}

	@Test
	void appartenanceSurToutLeDomaine() {
		TreeSet<Integer> expected = ensemble(0, 2, 254, 255, 256, 511, 513, 8192, 16383, 32766, 32767);
		MySet set = creerEnsemble(expected);
		for (int value = 0; value < LIMIT; value++) {
			assertEquals(expected.contains(value), set.containsValue(value), "value=" + value);
		}
		verifierContenuEtStructure(set, expected, "appartenance exhaustive");
	}

	@ParameterizedTest(name = "addNumber({0}) et doublon")
	@MethodSource("valeursAuxFrontieres")
	void ajoutDansLeVidePuisDoublon(int value) {
		MySet set = creerEnsemble();
		set.addNumber(value);
		verifierContenuEtStructure(set, ensemble(value), "premier ajout " + value);
		set.addNumber(value);
		verifierContenuEtStructure(set, ensemble(value), "doublon " + value);
		verifierContenuEtStructure(new MySet(), ensemble(), "un autre ensemble reste vide");
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 255, 256, 257, 511, 512, 513, 767, 768, 1023, 32767 })
	void ajoutAvantEntreApresOuDansLesBlocs(int value) {
		TreeSet<Integer> expected = ensemble(256, 258, 768, 770, 32512);
		MySet set = creerEnsemble(expected);
		set.addNumber(value);
		expected.add(value);
		verifierContenuEtStructure(set, expected, "insertion " + value);
	}

	@Test
	void ajoutsEnOrdreDecroissantPuisSuppressionDeTousLesRangs() {
		MySet set = creerEnsemble();
		TreeSet<Integer> expected = ensemble();
		for (int rank = RANKS - 1; rank >= 0; rank--) {
			int value = rank * BLOCK + 255;
			set.addNumber(value);
			expected.add(value);
			verifierContenuEtStructure(set, expected, "ajout decroissant rang " + rank);
		}
		for (int rank = 0; rank < RANKS; rank++) {
			int value = rank * BLOCK + 255;
			set.removeNumber(value);
			expected.remove(value);
			verifierContenuEtStructure(set, expected, "suppression successive en tete " + rank);
		}
	}

	@Test
	void ajoutsEnOrdreAleatoireSansDoublonDeRang() {
		ArrayList<Integer> values = new ArrayList<>();
		for (int rank = 0; rank < RANKS; rank++) {
			values.add(rank * BLOCK);
			values.add(rank * BLOCK + 255);
		}
		Collections.shuffle(values, new Random(5721));
		MySet set = creerEnsemble();
		TreeSet<Integer> expected = ensemble();
		for (int value : values) {
			set.addNumber(value);
			expected.add(value);
			verifierContenuEtStructure(set, expected, "ordre aleatoire, ajout " + value);
		}
	}

	@Test
	void ajouterLes256RestesDUnMemeBloc() {
		MySet set = creerEnsemble(0, 32767);
		TreeSet<Integer> expected = ensemble(0, 32767);
		for (int value = 511; value >= 256; value--) {
			set.addNumber(value);
			expected.add(value);
			verifierContenuEtStructure(set, expected, "remplissage d'un bloc, valeur " + value);
		}
		// Deuxieme passage : ni nouveau bloc, ni nouvelle valeur.
		for (int value = 256; value < 512; value++)
			set.addNumber(value);
		verifierContenuEtStructure(set, expected, "bloc plein ajoute deux fois");
	}

	@ParameterizedTest(name = "removeNumber({0})")
	@MethodSource("valeursAuxFrontieres")
	void suppressionDernierElementPuisAbsent(int value) {
		MySet set = creerEnsemble(value);
		set.removeNumber(value);
		verifierContenuEtStructure(set, ensemble(), "dernier element du bloc " + value);
		set.removeNumber(value);
		verifierContenuEtStructure(set, ensemble(), "suppression dans le vide " + value);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 255, 256, 257, 511, 512, 513, 767, 768, 1023, 32767 })
	void suppressionPresenteOuAbsenteDansPlusieursBlocs(int value) {
		TreeSet<Integer> expected = ensemble(0, 1, 255, 256, 511, 512, 768, 32767);
		MySet set = creerEnsemble(expected);
		set.removeNumber(value);
		expected.remove(value);
		verifierContenuEtStructure(set, expected, "suppression " + value);
	}

	@Test
	void viderUnBlocPleinPuisLeRecreer() {
		MySet set = creerEnsemble(intervalle(256, 512));
		TreeSet<Integer> expected = intervalle(256, 512);
		for (int value = 256; value < 512; value++) {
			set.removeNumber(value);
			expected.remove(value);
			verifierContenuEtStructure(set, expected, "vidage bloc, valeur " + value);
		}
		set.addNumber(511);
		verifierContenuEtStructure(set, ensemble(511), "reutilisation apres vidage");
	}

	static Stream<Arguments> casCardinaux() {
		return Stream.of(Arguments.of("vide", ensemble()), Arguments.of("zero", ensemble(0)),
				Arguments.of("maximum", ensemble(32767)), Arguments.of("meme bloc", ensemble(0, 1, 127, 255)),
				Arguments.of("frontiere", ensemble(255, 256)), Arguments.of("bloc plein", intervalle(256, 512)),
				Arguments.of("tous les rangs", uneValeurParRang(0, 1, 0)),
				Arguments.of("domaine complet", intervalle(0, LIMIT)));
	}

	@ParameterizedTest(name = "size : {0}")
	@MethodSource("casCardinaux")
	void cardinalCompteLesEntiersEtNonLesBlocs(String label, TreeSet<Integer> expected) {
		MySet set = creerEnsemble(expected);
		assertEquals(expected.size(), set.size(), label);
		assertEquals(expected.size(), set.size(), label + " : deuxieme appel");
		verifierContenuEtStructure(set, expected, "size est un observateur");
	}

	@Test
	void cardinalApresModificationsEtClear() {
		MySet set = creerEnsemble(0, 255, 256);
		assertEquals(3, set.size());
		set.addNumber(0);
		assertEquals(3, set.size(), "doublon");
		set.addNumber(32767);
		assertEquals(4, set.size());
		set.removeNumber(42);
		assertEquals(4, set.size(), "absence");
		set.removeNumber(256);
		assertEquals(3, set.size());
		set.clear();
		assertEquals(0, set.size());
		set.addNumber(32767);
		assertEquals(1, set.size());
		verifierContenuEtStructure(set, ensemble(32767), "cardinal apres reutilisation");
	}

	static Stream<Arguments> casFlux() {
		return Stream.of(Arguments.of("-1", ensemble()), Arguments.of("-1 100 200", ensemble()),
				Arguments.of("0 -1 256", ensemble(0)), Arguments.of("32767 -1 0", ensemble(32767)),
				Arguments.of("255 256 511 512 -1", ensemble(255, 256, 511, 512)),
				Arguments.of("512 256 0 512 0 256 -1 32767", ensemble(0, 256, 512)),
				Arguments.of(" \t32767\n0\r\n255  256\t-1\n999", ensemble(0, 255, 256, 32767)));
	}

	@ParameterizedTest(name = "ajout depuis flux #{index} : {0}")
	@MethodSource("casFlux")
	void ajoutFluxCumuleEtSArreteAuMoinsUn(String text, TreeSet<Integer> read) {
		TreeSet<Integer> expected = ensemble(42, 255, 513);
		MySet set = creerEnsemble(expected);
		set.addAllFromStream(creerFlux(text));
		expected.addAll(read);
		verifierContenuEtStructure(set, expected, "addAllFromStream, depart={42, 255, 513}, flux="
				+ text.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t"));
	}

	@ParameterizedTest(name = "retrait depuis flux #{index} : {0}")
	@MethodSource("casFlux")
	void retraitFluxIgnoreAbsentsEtSArreteAuMoinsUn(String text, TreeSet<Integer> read) {
		TreeSet<Integer> expected = ensemble(0, 42, 100, 200, 255, 256, 511, 512, 513, 999, 32767);
		MySet set = creerEnsemble(expected);
		set.removeAllFromStream(creerFlux(text));
		expected.removeAll(read);
		verifierContenuEtStructure(set, expected,
				"removeAllFromStream, depart={0, 42, 100, 200, 255, 256, 511, 512, 513, 999, 32767}, flux="
						+ text.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t"));
	}

	@Test
	void fluxSurVideEtAppelsSuccessifs() {
		MySet set = creerEnsemble();
		set.removeAllFromStream(creerFlux("0 256 32767 -1"));
		verifierContenuEtStructure(set, ensemble(), "retraits dans le vide");
		set.addAllFromStream(creerFlux("32767 0 256 0 -1"));
		verifierContenuEtStructure(set, ensemble(0, 256, 32767), "premier flux");
		set.addAllFromStream(creerFlux("255 256 -1"));
		verifierContenuEtStructure(set, ensemble(0, 255, 256, 32767), "second flux");
		set.removeAllFromStream(creerFlux("0 255 255 256 32767 -1"));
		verifierContenuEtStructure(set, ensemble(), "tout retirer, doublon compris");
	}

	static Stream<Arguments> casOperationsBinaires() {
		java.util.List<Arguments> cases = new ArrayList<>();
		for (Operation op : Operation.values()) {
			ajouterCas(cases, op, "deux vides", ensemble(), ensemble());
			ajouterCas(cases, op, "gauche vide", ensemble(), ensemble(0, 255, 256, 32767));
			ajouterCas(cases, op, "droite vide", ensemble(0, 255, 256, 32767), ensemble());
			ajouterCas(cases, op, "egaux distincts", ensemble(0, 255, 512, 32767), ensemble(0, 255, 512, 32767));
			ajouterCas(cases, op, "memes rangs restes disjoints", ensemble(0, 256, 32766), ensemble(1, 257, 32767));
			ajouterCas(cases, op, "blocs entrelaces", ensemble(0, 512, 1024), ensemble(256, 768, 1280));
			ajouterCas(cases, op, "gauche avant", ensemble(0, 256), ensemble(512, 32767));
			ajouterCas(cases, op, "droite avant", ensemble(512, 32767), ensemble(0, 256));
			ajouterCas(cases, op, "recouvrement partiel", ensemble(0, 1, 255, 256, 257, 512, 32767),
					ensemble(1, 255, 257, 768, 32767));
			ajouterCas(cases, op, "suppressions consecutives", ensemble(0, 256, 512, 768, 1024),
					ensemble(0, 256, 512, 768));
			ajouterCas(cases, op, "inclusion stricte", ensemble(0, 511), ensemble(0, 1, 255, 256, 511, 32767));
			ajouterCas(cases, op, "bloc plein contre partiel", intervalle(0, 256), ensemble(0, 127, 255, 256));
			ajouterCas(cases, op, "tous les rangs", uneValeurParRang(0, 1, 0), uneValeurParRang(0, 1, 255));
			ajouterCas(cases, op, "domaine complet", intervalle(0, LIMIT), intervalle(255, 32767));
		}
		return cases.stream();
	}

	private static void ajouterCas(java.util.List<Arguments> cases, Operation op, String name, TreeSet<Integer> a,
			TreeSet<Integer> b) {
		cases.add(Arguments.of(op, name, a, b));
	}

	@ParameterizedTest(name = "{0} : {1}")
	@MethodSource("casOperationsBinaires")
	void operationsBinaires(Operation op, String name, TreeSet<Integer> aValues, TreeSet<Integer> bValues) {
		MySet a = creerEnsemble(aValues);
		MySet b = creerEnsemble(bValues);
		String contexte = decrireCas(nomOperation(op), name, aValues, bValues);
		try {
			appliquerOperation(op, a, b);
			verifierContenuEtStructure(a, calculerResultatAttendu(op, aValues, bValues), contexte);
			verifierContenuEtStructure(b, bValues, contexte + "\nLe second ensemble doit rester inchange.");
			verifierAbsenceDePartage(a, b);
		} catch (RuntimeException | AssertionError erreur) {
			fail(contexte + "\n" + erreur.getMessage(), erreur);
		}
	}

	private static void verifierAbsenceDePartage(MySet a, MySet b) {
		Iterator<SubSet> ia = a.iterator();
		int guardA = 0;
		while (!ia.isOnFlag()) {
			assertTrue(guardA++ < RANKS, "cycle dans this");
			SubSet sa = ia.getValue();
			Iterator<SubSet> ib = b.iterator();
			int guardB = 0;
			while (!ib.isOnFlag()) {
				assertTrue(guardB++ < RANKS, "cycle dans set2");
				SubSet sb = ib.getValue();
				assertNotSame(sa, sb, "Un meme objet SubSet est present dans les deux ensembles.");
				assertNotSame(sa.set, sb.set, "Un meme objet SmallSet est present dans les deux ensembles.");
				ib.goForward();
			}
			ia.goForward();
		}
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(Operation.class)
	void operandeIdentiqueAThis(Operation op) {
		for (TreeSet<Integer> initial : Arrays.asList(ensemble(), ensemble(0), ensemble(0, 255, 256, 32767))) {
			MySet a = creerEnsemble(initial);
			appliquerOperation(op, a, a);
			verifierContenuEtStructure(a, calculerResultatAttendu(op, initial, initial), op + " avec set2 == this");
		}
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(Operation.class)
	void mutationsUlterieuresIndependantes(Operation op) {
		TreeSet<Integer> aa = ensemble(0, 1, 256, 512, 32767);
		TreeSet<Integer> bb = ensemble(1, 2, 256, 768, 32767);
		MySet a = creerEnsemble(aa);
		MySet b = creerEnsemble(bb);
		appliquerOperation(op, a, b);
		TreeSet<Integer> expected = calculerResultatAttendu(op, aa, bb);
		verifierContenuEtStructure(a, expected, "avant mutation");
		verifierContenuEtStructure(b, bb, "avant mutation de set2");
		verifierAbsenceDePartage(a, b);
		// Modifie directement les SmallSet : ce test ne depend pas d'addNumber.
		Iterator<SubSet> ib = b.iterator();
		while (!ib.isOnFlag()) {
			ib.getValue().set.complement();
			ib.goForward();
		}
		verifierContenuEtStructure(a, expected, "modifier set2 ne doit pas modifier this");
		TreeSet<Integer> bAfter = lireContenuEtVerifierStructure(b, "set2 apres mutation directe");
		Iterator<SubSet> ia = a.iterator();
		while (!ia.isOnFlag()) {
			ia.getValue().set.complement();
			ia.goForward();
		}
		verifierContenuEtStructure(b, bAfter, "modifier this ne doit pas modifier set2");
	}

	@Test
	void equalsRespecteNullTypeReflexiviteSymetrieEtTransitivite() {
		MySet a = creerEnsemble(0, 255, 256, 32767);
		MySet b = creerEnsemble(0, 255, 256, 32767);
		MySet c = creerEnsemble(0, 255, 256, 32767);
		assertTrue(a.equals(a), "reflexivite");
		assertFalse(a.equals(null));
		assertFalse(a.equals("ensemble"));
		assertFalse(a.equals(new SmallSet()));
		assertTrue(a.equals(b));
		assertTrue(b.equals(a), "symetrie");
		assertTrue(b.equals(c));
		assertTrue(a.equals(c), "transitivite");
		assertTrue(a.equals(b), "stabilite");
		verifierContenuEtStructure(a, ensemble(0, 255, 256, 32767), "equals : gauche");
		verifierContenuEtStructure(b, ensemble(0, 255, 256, 32767), "equals : droite");
		verifierContenuEtStructure(c, ensemble(0, 255, 256, 32767), "equals : troisieme ensemble");
	}

	static Stream<Arguments> casRelations() {
		return Stream.of(Arguments.of("vides", ensemble(), ensemble()),
				Arguments.of("vide inclus", ensemble(), ensemble(0)),
				Arguments.of("non vide contre vide", ensemble(0), ensemble()),
				Arguments.of("memes valeurs", ensemble(0, 255, 256), ensemble(0, 255, 256)),
				Arguments.of("meme cardinal autre rang", ensemble(0), ensemble(256)),
				Arguments.of("meme rang autre reste", ensemble(0), ensemble(1)),
				Arguments.of("prefixe strict", ensemble(0, 256), ensemble(0, 256, 32767)),
				Arguments.of("suffixe strict", ensemble(256, 32767), ensemble(0, 256, 32767)),
				Arguments.of("bloc partiel", ensemble(0, 255), intervalle(0, 256)),
				Arguments.of("rang absent au milieu", ensemble(0, 256, 512), ensemble(0, 512)),
				Arguments.of("restes en partie communs", ensemble(0, 1), ensemble(1, 2)),
				Arguments.of("derniere valeur differente", ensemble(0, 256, 32766), ensemble(0, 256, 32767)),
				Arguments.of("domaine complet", intervalle(0, LIMIT), intervalle(0, LIMIT)));
	}

	@ParameterizedTest(name = "equals : {0}")
	@MethodSource("casRelations")
	void egaliteSelonLeContenu(String name, TreeSet<Integer> aa, TreeSet<Integer> bb) {
		name = decrireCas("equals", name, aa, bb);
		MySet a = creerEnsemble(aa);
		MySet b = creerEnsemble(bb);
		assertEquals(aa.equals(bb), a.equals(b), name);
		assertEquals(aa.equals(bb), b.equals(a), name + " : sens inverse");
		verifierContenuEtStructure(a, aa, "equals ne modifie pas this");
		verifierContenuEtStructure(b, bb, "equals ne modifie pas set2");
	}

	@ParameterizedTest(name = "inclusion : {0}")
	@MethodSource("casRelations")
	void inclusionDansLesDeuxSens(String name, TreeSet<Integer> aa, TreeSet<Integer> bb) {
		name = decrireCas("isIncludedIn", name, aa, bb);
		MySet a = creerEnsemble(aa);
		MySet b = creerEnsemble(bb);
		assertEquals(inclusionAttendue(aa, bb), a.isIncludedIn(b), name);
		assertEquals(inclusionAttendue(bb, aa), b.isIncludedIn(a), name + " : sens inverse");
		assertTrue(a.isIncludedIn(a), "inclusion reflexive");
		verifierContenuEtStructure(a, aa, "inclusion ne modifie pas this");
		verifierContenuEtStructure(b, bb, "inclusion ne modifie pas set2");
	}

	private static TreeSet<Integer> sousEnsembleNumero(int mask) {
		// Le numero choisit un sous-ensemble parmi les 32 possibles.
		// Un chiffre binaire egal a 1 signifie : inclure la valeur correspondante.
		// Le message d'echec affiche les valeurs, pas seulement ce numero.
		int[] basis = { 0, 1, 255, 256, 32767 };
		TreeSet<Integer> result = ensemble();
		int numero = mask;
		for (int valeur : basis) {
			if (numero % 2 == 1)
				result.add(valeur);
			numero /= 2;
		}
		return result;
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(Operation.class)
	@Tag("approfondi")
	void toutesLesPairesDeSousEnsemblesDUnPetitUnivers(Operation op) {
		// 32 * 32 paires ; en cas d'echec, les ensembles sont affiches en clair.
		for (int left = 0; left < 32; left++) {
			for (int right = 0; right < 32; right++) {
				TreeSet<Integer> aa = sousEnsembleNumero(left);
				TreeSet<Integer> bb = sousEnsembleNumero(right);
				MySet a = creerEnsemble(aa);
				MySet b = creerEnsemble(bb);
				String contexte = decrireCas(nomOperation(op), "petit univers", aa, bb);
				try {
					appliquerOperation(op, a, b);
					verifierContenuEtStructure(a, calculerResultatAttendu(op, aa, bb), contexte);
					verifierContenuEtStructure(b, bb, contexte + "\nLe second ensemble doit rester inchange.");
				} catch (RuntimeException | AssertionError erreur) {
					fail(contexte + "\n" + erreur.getMessage(), erreur);
				}
			}
		}
	}

	@Test
	@Tag("approfondi")
	void toutesLesRelationsDUnPetitUnivers() {
		for (int left = 0; left < 32; left++) {
			for (int right = 0; right < 32; right++) {
				TreeSet<Integer> aa = sousEnsembleNumero(left);
				TreeSet<Integer> bb = sousEnsembleNumero(right);
				MySet a = creerEnsemble(aa);
				MySet b = creerEnsemble(bb);
				String context = decrireCas("equals / isIncludedIn", "petit univers", aa, bb);
				assertEquals(aa.equals(bb), a.equals(b), context + " : equals");
				assertEquals(inclusionAttendue(aa, bb), a.isIncludedIn(b), context + " : inclusion");
				verifierContenuEtStructure(a, aa, context + " : gauche");
				verifierContenuEtStructure(b, bb, context + " : droite");
			}
		}
	}

	private static TreeSet<Integer> ensembleAleatoire(Random random) {
		TreeSet<Integer> result = ensemble();
		int count = random.nextInt(401);
		for (int i = 0; i < count; i++)
			result.add(random.nextInt(LIMIT));
		return result;
	}

	@ParameterizedTest(name = "sequence aleatoire, graine {0}")
	@ValueSource(ints = { 0, 1, 2, 3, 5, 8, 13, 21, 42, 255, 256, 512, 2026, 32767 })
	@Tag("approfondi")
	void sequencesReproductiblesAvecOracleIndependant(int seed) {
		Random random = new Random(seed);
		MySet a = creerEnsemble();
		TreeSet<Integer> expected = ensemble();
		for (int step = 0; step < 120; step++) {
			int choice = random.nextInt(10);
			int value = random.nextInt(LIMIT);
			TreeSet<Integer> avant = new TreeSet<>(expected);
			TreeSet<Integer> second = null;
			String operation = "";
			try {
				if (choice == 0 || choice == 1) {
					operation = "addNumber(" + value + ")";
					a.addNumber(value);
					expected.add(value);
				} else if (choice == 2 || choice == 3) {
					if (choice == 3 && !expected.isEmpty())
						value = expected.first();
					operation = "removeNumber(" + value + ")";
					a.removeNumber(value);
					expected.remove(value);
				} else if (choice < 8) {
					Operation op = Operation.values()[choice - 4];
					operation = nomOperation(op);
					second = ensembleAleatoire(random);
					MySet b = creerEnsemble(second);
					appliquerOperation(op, a, b);
					expected = calculerResultatAttendu(op, expected, second);
					verifierContenuEtStructure(b, second, "Le second ensemble a ete modifie");
				} else if (choice == 8) {
					operation = "containsValue(" + value + ")";
					assertEquals(expected.contains(value), a.containsValue(value), operation);
				} else {
					second = ensembleAleatoire(random);
					MySet b = creerEnsemble(second);
					operation = "equals";
					assertEquals(expected.equals(second), a.equals(b), operation);
					operation = "isIncludedIn";
					assertEquals(inclusionAttendue(expected, second), a.isIncludedIn(b), operation);
					verifierContenuEtStructure(b, second, "Une observation a modifie le second ensemble");
				}
				verifierContenuEtStructure(a, expected, "Apres " + operation);
				assertEquals(expected.size(), a.size(), "Cardinal incorrect apres " + operation);
			} catch (RuntimeException | AssertionError erreur) {
				// La graine rejoue la sequence entiere. Les valeurs ci-dessous permettent
				// aussi de reconstruire directement l'etape fautive avec creerEnsemble(...).
				fail("Sequence reproductible : graine=" + seed + ", etape=" + step + " (numerotation a partir de 0)"
						+ "\nOperation : " + operation + "\nPremier ensemble AVANT (complet) : " + avant
						+ (second == null ? "" : "\nSecond ensemble AVANT (complet) : " + second) + "\nCause : "
						+ erreur.getMessage(), erreur);
			}
		}
	}

	private static TreeSet<Integer> uneValeurParRang(int firstRank, int step, int offset) {
		TreeSet<Integer> result = ensemble();
		for (int rank = firstRank; rank < RANKS; rank += step)
			result.add(rank * BLOCK + offset);
		return result;
	}

	private static int compterBlocs(TreeSet<Integer> values) {
		int count = 0;
		int previousRank = -1;
		for (int value : values) {
			int rank = value / BLOCK;
			if (rank != previousRank) {
				count++;
				previousRank = rank;
			}
		}
		return count;
	}

	private static int mesurer(MySet a, MySet b, Runnable action) {
		a.startCount();
		if (b != null && a != b)
			b.startCount();
		action.run();
		// Lire AVANT toute verification : les assertions structurelles parcourent les
		// listes.
		return a.getCount() + (b != null && b != a ? b.getCount() : 0);
	}

	private static void verifierBudget(int cost, int bound, String context) {
		assertTrue(cost >= 0 && cost <= bound,
				context + "\nOperations comptees : " + cost + "\nMaximum autorise    : " + bound);

	}

	@Tag("complexite")
	@ParameterizedTest
	@ValueSource(ints = { 8, 32, 64, 128 })
	void coutLineaireDesOperationsElementaires(int ranks) {
		TreeSet<Integer> aa = ensemble();
		for (int rank = 0; rank < ranks; rank++)
			aa.add(rank * BLOCK);
		MySet a = creerEnsemble(aa);
		int bound = SINGLE_FACTOR * ranks + FIXED_COST;
		int cost = mesurer(a, null, () -> assertFalse(a.containsValue(32767)));
		verifierBudget(cost, bound, "containsValue absent en fin");
		cost = mesurer(a, null, () -> assertTrue(a.containsValue((ranks - 1) * BLOCK)));
		verifierBudget(cost, bound, "containsValue dernier bloc");
		cost = mesurer(a, null, () -> assertEquals(ranks, a.size()));
		verifierBudget(cost, bound, "size");
		verifierContenuEtStructure(a, aa, "observateurs mesures");
		cost = mesurer(a, null, () -> a.addNumber(32767));
		verifierBudget(cost, bound, "addNumber en fin");
		aa.add(32767);
		verifierContenuEtStructure(a, aa, "ajout mesure");
		cost = mesurer(a, null, () -> a.removeNumber(32767));
		verifierBudget(cost, SINGLE_FACTOR * (ranks + 1) + FIXED_COST, "removeNumber en fin");
		aa.remove(32767);
		verifierContenuEtStructure(a, aa, "retrait mesure");
	}

	@Tag("complexite")
	@ParameterizedTest(name = "budget {0} : {1}")
	@MethodSource("casOperationsBinaires")
	void coutLineaireDesOperationsBinaires(Operation op, String name, TreeSet<Integer> aa, TreeSet<Integer> bb) {
		MySet a = creerEnsemble(aa);
		MySet b = creerEnsemble(bb);
		int m = compterBlocs(aa);
		int n = compterBlocs(bb);

		int bound = m + n + 1;
		if (op.equals(Operation.UNION) || op.equals(Operation.SYMMETRIC_DIFFERENCE)) {
			bound += FIXED_COST + 2;
		}
		int cost = mesurer(a, b, () -> appliquerOperation(op, a, b));
		verifierBudget(cost, bound, decrireCas(nomOperation(op), name, aa, bb) + "\nBlocs avant : m=" + m + ", n=" + n);
		verifierContenuEtStructure(a, calculerResultatAttendu(op, aa, bb), "resultat du calcul mesure");
		verifierContenuEtStructure(b, bb, "set2 apres calcul mesure");
	}

	@Tag("complexite")
	@ParameterizedTest
	@ValueSource(ints = { 8, 32, 64, 128 })
	void coutLineaireSurDeLonguesListesDeMemesRangs(int ranks) {
		TreeSet<Integer> aa = ensemble();
		TreeSet<Integer> bb = ensemble();
		for (int rank = 0; rank < ranks; rank++) {
			aa.add(rank * BLOCK);
			bb.add(rank * BLOCK);
			bb.add(rank * BLOCK + 255);
		}
		int bound = BINARY_FACTOR * 2 * ranks + FIXED_COST;
		for (Operation op : Operation.values()) {
			MySet a = creerEnsemble(aa);
			MySet b = creerEnsemble(bb);
			int cost = mesurer(a, b, () -> appliquerOperation(op, a, b));
			verifierBudget(cost, bound, op + " sur " + ranks + " rangs communs");
			verifierContenuEtStructure(a, calculerResultatAttendu(op, aa, bb), "longues listes");
			verifierContenuEtStructure(b, bb, "operande conserve");
		}
		MySet a = creerEnsemble(aa);
		MySet b = creerEnsemble(bb);
		int cost = mesurer(a, b, () -> assertTrue(a.isIncludedIn(b)));
		verifierBudget(cost, bound, "inclusion complete");
		MySet equal = creerEnsemble(aa);
		cost = mesurer(a, equal, () -> assertTrue(a.equals(equal)));
		verifierBudget(cost, bound, "egalite complete");
		verifierContenuEtStructure(a, aa, "gauche observee");
		verifierContenuEtStructure(b, bb, "droite observee");
		verifierContenuEtStructure(equal, aa, "copie observee");
	}

	@Tag("complexite")
	@Test
	void coutInclusionDeDeuxPetitsEnsembles() {
		MySet a = new MySet();
		MySet b = new MySet();

		int nombreBlocs = 10;

		for (int rang = 0; rang < nombreBlocs; rang++) {
			SmallSet sa = new SmallSet();
			SmallSet sb = new SmallSet();

			sa.add(3);
			sa.add(17);

			sb.add(3);
			sb.add(17);

			if (rang == nombreBlocs - 1) {
				sb.add(42);
			}

			a.addTail(new SubSet(rang, sa));
			b.addTail(new SubSet(rang, sb));
		}

		a.startCount();
		b.startCount();

		boolean resultat = a.isIncludedIn(b);

		// Mesurer avant toute verification parcourant les listes.
		int cout = a.getCount() + b.getCount();
		int budget = 2 * nombreBlocs + 4;

		assertTrue(resultat, "a doit etre inclus dans b");
		assertTrue(cout <= budget, "Parcours excessif : " + cout + " operations, budget : " + budget);
	}
}
