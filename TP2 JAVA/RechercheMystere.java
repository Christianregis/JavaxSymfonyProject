
public class RechercheMystere {
    public boolean recherche(int n) {
        boolean[] tabBool = new boolean[10];
        int n2 = n * n;
        int n3 = n * n * n;

        int valeurN2 = n2;
        int valeurN3 = n3;
        for (int i = 0; i < tabBool.length; i++) {
            while (valeurN3 > 0) {
                int valeur2 = valeurN2 > 0 ? valeurN2 % 10 : -1;
                int valeur3 = valeurN3 % 10;
                if (valeur2 > 0) {
                    if ((valeur2 == i || valeur3 == i) && !tabBool[i]) {
                        tabBool[i] = true;
                    } else if (((valeur2 == i && valeur3 != i) || (valeur2 != i && valeur3 == i)
                            || (valeur2 == i && valeur3 == i)) && tabBool[i]) {
                        return false;
                    }
                } else {
                    if (valeur3 == i && !tabBool[i]) {
                        tabBool[i] = true;
                    } else if (valeur3 == i && tabBool[i]) {
                        return false;
                    }
                }
                valeurN2 = valeurN2 / 10;
                valeurN3 = valeurN3 / 10;
            }
            valeurN2 = n2;
            valeurN3 = n3;
        }
        for (int i = 0; i < tabBool.length; i++) {
            if (!tabBool[i]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        RechercheMystere rechercheMystere = new RechercheMystere();
        System.out.println(rechercheMystere.recherche(69));
    }
}
