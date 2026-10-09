package fr.istic.prog.tp3.test;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.EngineFilter.includeEngines;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * Lance tous les tests de TestTP3 avec le vrai moteur JUnit Jupiter :
 * tests simples, @Nested, @ParameterizedTest, @MethodSource, @Timeout, etc.
 *
 * DEPENDANCES
 * Le lanceur utilise junit-platform-launcher, en plus de JUnit Jupiter.
 * Si les imports org.junit.platform.launcher ne sont pas resolus, ajouter
 * cet artefact dans une version compatible avec les autres composants :
 * Jupiter 5.9.3 <-> Platform 1.9.3, Jupiter 5.10.2 <-> Platform 1.10.2, etc.
 * Pour Maven, placer ce fichier dans src/test/java et ajouter le launcher
 * avec scope test, en alignant les versions au moyen du BOM JUnit utilise.
 * Sans Maven, junit-platform-console-standalone de la version correspondante
 * contient les composants necessaires. Eviter les JAR JUnit en double.
 *
 * RESULTAT
 * true : au moins un test execute, aucun echec, aucune execution abandonnee.
 * false : echec, abandon, aucun test execute ou erreur de lancement.
 * Le resume distingue les tests ignores des tests abandonnes (assumptions).
 * Les erreurs de groupes (@BeforeAll, sources de parametres, etc.) comptent
 * aussi comme echecs, meme si aucun corps de test n'a pu etre execute.
 * Code de sortie du processus : 0 si succes, 1 sinon.
 *
 * Documentation : https://docs.junit.org/5.14.1/advanced-topics/launcher-api
 */
public final class TestRunner {

    private TestRunner() {
        // Classe utilitaire : pas d'instance necessaire.
    }

    public static void main(String[] args) {
        boolean succes = executerEtAfficherResume();
        System.exit(succes ? 0 : 1);
    }

    /** Peut aussi etre appelee sans terminer la JVM. */
    public static boolean executerEtAfficherResume() {
        // Garder la sortie initiale : certains tests capturent temporairement System.out.
        PrintStream sortie = System.out;

        try {
            LauncherDiscoveryRequest requete = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectClass(TestTP3.class))
                    .filters(includeEngines("junit-jupiter"))
                    .configurationParameter("junit.jupiter.execution.parallel.enabled", "false")
                    .configurationParameter("junit.jupiter.execution.timeout.mode", "enabled")
                    .build();

            SummaryGeneratingListener bilan = new SummaryGeneratingListener();
            List<String> abandons = new ArrayList<>();

            TestExecutionListener suiviAbandons = new TestExecutionListener() {
                @Override
                public void executionFinished(TestIdentifier test, TestExecutionResult resultat) {
                    if (resultat.getStatus() == TestExecutionResult.Status.ABORTED) {
                        String cause = resultat.getThrowable()
                                .map(Throwable::toString).orElse("Cause non precisee");
                        abandons.add(test.getDisplayName() + "\n   "
                                + test.getUniqueId() + "\n   " + cause);
                    }
                }
            };

            Launcher lanceur = LauncherFactory.create();
            lanceur.registerTestExecutionListeners(bilan, suiviAbandons);
            lanceur.execute(requete);

            TestExecutionSummary resume = bilan.getSummary();
            boolean succes = resume.getTestsStartedCount() > 0
                    && resume.getTotalFailureCount() == 0
                    && resume.getTestsAbortedCount() == 0
                    && resume.getContainersAbortedCount() == 0;

            sortie.println(succes);
            sortie.println();
            sortie.println("Resume des tests de TP3Test");
            sortie.println("--------------------------");
            sortie.println("Tests demarres  : " + resume.getTestsStartedCount());
            sortie.println("Tests reussis   : " + resume.getTestsSucceededCount());
            sortie.println("Tests en echec  : " + resume.getTestsFailedCount());
            sortie.println("Tests ignores   : " + resume.getTestsSkippedCount());
            sortie.println("Tests abandonnes: " + resume.getTestsAbortedCount());
            sortie.println("Groupes en echec: " + resume.getContainersFailedCount());
            sortie.println("Groupes ignores : " + resume.getContainersSkippedCount());
            sortie.println("Groupes abandonnes: " + resume.getContainersAbortedCount());
            sortie.println("Duree : "
                    + (resume.getTimeFinished() - resume.getTimeStarted()) + " ms");

            if (resume.getTestsSkippedCount() > 0 || resume.getContainersSkippedCount() > 0) {
                sortie.println("Les tests/groupes ignores ne comptent pas comme des reussites"
                        + " ni comme des echecs (@Disabled notamment).");
            }
            if (resume.getTestsStartedCount() == 0) {
                sortie.println("Aucun test execute : impossible de conclure a un succes.");
            }

            int numero = 1;
            for (TestExecutionSummary.Failure echec : resume.getFailures()) {
                TestIdentifier test = echec.getTestIdentifier();
                sortie.println();
                sortie.println("ECHEC " + numero++ + " : " + test.getDisplayName());
                // L'identifiant indique aussi la classe imbriquee et l'invocation parametree.
                sortie.println("Identifiant : " + test.getUniqueId());
                test.getSource().ifPresent(source -> sortie.println("Source : " + source));
                // Inclut le message assertEquals, les valeurs attendue/obtenue et la ligne.
                echec.getException().printStackTrace(sortie);
            }

            for (String abandon : abandons) {
                sortie.println();
                sortie.println("ABANDON : " + abandon);
            }

            sortie.flush();
            return succes;
        } catch (Exception | LinkageError probleme) {
            // Une erreur de decouverte ou une dependance incompatible n'est pas un succes.
            sortie.println(false);
            sortie.println("Impossible de lancer ou de terminer les tests de TP3Test.");
            sortie.println("Verifier les erreurs de compilation et les dependances JUnit"
                    + " (Jupiter et Platform Launcher compatibles).");
            probleme.printStackTrace(sortie);
            sortie.flush();
            return false;
        }
    }
}
