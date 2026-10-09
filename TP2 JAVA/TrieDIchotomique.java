import java.util.Arrays;
import java.util.Scanner;

public class TrieDIchotomique {
    public int lireTailleN() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez un nombre entre 1 et 50 ");
        int nombre = scanner.nextInt();
        if (nombre >= 1 && nombre <= 50) {
            return nombre;
        }
        System.out.println("Le nombre doit etre compris entre 1 et 50 !");
        return 0;

    }

    public int[] remplirTableauN(int n) {

        Scanner scanner = new Scanner(System.in);
        int[] tab = new int[n];
        int valeur;
        System.out.println("Entrez tous les elements du Tableau ");
        for (int i = 0; i < n; i++) {
            valeur = scanner.nextInt();
            tab[i] = valeur;
        }
        return tab;
    }

    public String afficherTableau(int[] tab) {
        return Arrays.toString(tab);
    }

    public void trieTableau(int[] tab) {
        for (int i = 0; i <= tab.length - 2; i++) {

            int rangMin = i;
            for (int j = i + 1; j <= tab.length - 1; ++j) {
                if (tab[j] < tab[rangMin]) {
                    rangMin = j;
                }
            }
            int aux = tab[i];
            tab[i] = tab[rangMin];
            tab[rangMin] = aux;
        }
    }

    public boolean trieDicho(int[] tab, int a) {
        int deb = 0;
        int fin = tab.length - 1;
        int milieu = (deb + fin) / 2;

        while (deb <= fin && a != tab[milieu]) {
            if (a < tab[milieu]) {
                fin = milieu - 1;
            } else {
                deb = milieu + 1;
            }

            milieu = (deb + fin) / 2;
        }
        return deb <= fin;
    }

    public static void main(String[] args) {
        Scanner scannner = new Scanner(System.in);

        TrieDIchotomique trieDIchotomique = new TrieDIchotomique();
        int nombre = trieDIchotomique.lireTailleN();
        int[] tab = trieDIchotomique.remplirTableauN(nombre);

        System.out.println(trieDIchotomique.afficherTableau(tab));

        trieDIchotomique.trieTableau(tab);
        System.out.println(trieDIchotomique.afficherTableau(tab));

        System.out.println("Entrez un nombre de recherche !");
        int valeur = scannner.nextInt();
        while (!trieDIchotomique.trieDicho(tab, valeur)) {
            System.out.println("Valeur non presente");
            System.out.println(trieDIchotomique.trieDicho(tab, valeur));
            valeur = scannner.nextInt();
        }
        
        System.out.println("Valeur trouvee !");
        scannner.close();
    }
}
