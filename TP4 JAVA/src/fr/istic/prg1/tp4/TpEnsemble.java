package fr.istic.prg1.tp4;

import java.awt.Frame;
import java.awt.Font;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Scanner;

public class TpEnsemble {

	private static final int SET_NUMBER = 5;
	private static SmallSet[] sets = new SmallSet[SET_NUMBER];
	private static final Scanner standardInput = new Scanner(System.in);

	private static final String[] COMMAND_TEXTS = { "E[n1] <-- E[n2]", "E[n1] <-- ensemble vide",
			"afficher le contenu de E[n1]", "ajouter une valeur à E[n1]", "ajouter un intervalle de valeurs à E[n1]",
			"déterminer si x appartient à E[n1]", "afficher le cardinal de E[n1]", "E[n1] <-- complémentaire(E[n1])",
			"E[n1] <-- différence(E[n1],E[n2])", "E[n1] <-- différenceSymétrique(E[n1],E[n2])",
			"déterminer si E[n1] et E[n2] sont égaux", "déterminer si E[n1] est inclus dans E[n2]",
			"E[n1] <-- intersection(E[n1],E[n2])", "retirer une valeur à E[n1]",
			"retirer un intervalle de valeurs à E[n1]", "E[n1] <-- union(E[n1],E[n2])", "déterminer si E[n1] est vide",
			"arrêt de l'exécution" };

	private static final String[] BUTTON_NAMES = { "Affecter", "AffecterVide", "Afficher", "Ajouter",
			"AjouterIntervalle", "Appartient", "Cardinal", "Complementaire", "Difference", "DifférenceSymetrique",
			"Egal", "InclusDans", "Intersection", "Oter", "RetirerIntervalle", "Union", "Vide", "Quitter" };

	private static Frame menu = new Frame();
	private static final int H = 150;
	private static final int V = 570;
	private static final int LOCX = 800;
	private static final int LOCY = 10;
	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 30;

	private static class Action implements ActionListener {
		private int actionNumber;

		public Action(int actionNumber) {
			this.actionNumber = actionNumber;
		}

		public synchronized void actionPerformed(ActionEvent event) {
			menu.setVisible(false);
			executer(actionNumber);
			menu.setVisible(true);
		}

		private static void executer(int number) {
			// traitements associes aux boutons
			System.out.println("* " + COMMAND_TEXTS[number]);
			switch (number) {
			case 0:
				sets[readNumber(1)] = sets[readNumber(2)].copyOf();
				break;
			case 1:
				sets[readNumber(1)].clear();
				break;
			case 2:
				System.out.println(sets[readNumber(1)]);
				break;
			case 3:
				sets[readNumber(1)].add(readValue("valeur à ajouter : "));
				break;
			case 4:
				sets[readNumber(1)].addInterval(readValue("deb intervalle : "), readValue("fin intervalle : "));
				break;
			case 5:
				System.out.println(sets[readNumber(1)].contains(readValue("x = ")));
				break;
			case 6:
				System.out.println(sets[readNumber(1)].size());
				break;
			case 7:
				sets[readNumber(1)].complement();
				break;
			case 8:
				sets[readNumber(1)].difference(sets[readNumber(2)]);
				break;
			case 9:
				sets[readNumber(1)].symmetricDifference(sets[readNumber(2)]);
				break;
			case 10:
				System.out.println(sets[readNumber(1)].equals(sets[readNumber(2)]));
				break;
			case 11:
				System.out.println(sets[readNumber(1)].isIncludedIn(sets[readNumber(2)]));
				break;
			case 12:
				sets[readNumber(1)].intersection(sets[readNumber(2)]);
				break;
			case 13:
				sets[readNumber(1)].remove(readValue("valeur à retirer : "));
				break;
			case 14:
				sets[readNumber(1)].removeInterval(readValue("début intervalle : "), readValue("fin intervalle : "));
				break;
			case 15:
				sets[readNumber(1)].union(sets[readNumber(2)]);
				break;
			case 16:
				System.out.println(sets[readNumber(1)].isEmpty());
				break;
			case 17:
			default:
				standardInput.close();
				System.exit(0);
			}
		} // executer

		private static int readNumber(int i) {
			return readInt("  numéro d'ensemble n" + i + " : ", 0, SET_NUMBER - 1);
		}

		private static int readValue(String s) {
			return readInt(s, 0, 255);
		}

		private static int readInt(String s, int start, int end) {
			// lecture d'un entier >=start et <= end
			int number;
			boolean b;
			do {
				b = true;
				number = 0;
				System.out.print(s);
				try {
					number = standardInput.nextInt();
				} catch (NumberFormatException e) {
					b = false;
				}
				b = b && number >= start && number <= end;
				if (!b) {
					System.out.println("valeur incorrecte");
				}
			} while (!b);
			return number;
		}

	} // class Action

	public static void main(String[] args) {
		for (int i = 0; i < SET_NUMBER; ++i) {
			sets[i] = new SmallSet();
		}
		menu.setTitle("TP Ensemble");
		menu.setSize(H, V);
		menu.setLocation(LOCX, LOCY);
		menu.setLayout(null);
		for (int i = 0; i < BUTTON_NAMES.length; ++i) {
			// cr�ation des boutons du menu
			Button bouton = new Button(BUTTON_NAMES[i]);
			bouton.addActionListener(new Action(i));
			bouton.setLocation(0, 25 + BUTTON_HEIGHT * i);
			bouton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
			bouton.setFont(new Font("Serif", Font.BOLD, 14));
			menu.add(bouton);
		}
		menu.setVisible(true);
	}
}