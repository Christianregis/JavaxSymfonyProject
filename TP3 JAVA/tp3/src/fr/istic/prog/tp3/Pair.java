package fr.istic.prog.tp3;

/**
 * 
 * @author CHEUKAP TATOUN CHRISTIAN REGIS et LEBAS ENZO
 * @version 6.0
 * @since 2026-02-24
 * 
 *        Classe représentant des doublets *non modifiables*
 */

public class Pair implements Comparable<Pair> {

	private int x;
	private int y;

	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Pair d) {
		if ((this.x < d.x) || (this.x == d.x && this.y < d.y)) {
			return -1;
		} else if ((d.x < this.x) || (d.x == this.x && d.y < this.y)) {
			return 1;
		} else {
			return 0;
		}
	}

	public Pair copyOf() {
		return new Pair(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pair)) {
			return false;
		}
		// DERNIER CAS À TRAITER
		return x == ((Pair) obj).x && y == ((Pair) obj).y;
	}

	public static void main(String[] args) {
		Pair p1 = new Pair(2, 2);
		Pair p2 = p1;
		System.out.println(p1.compareTo(p2));
		System.out.println(p1.equals(new Pair(0, 0)));
	}
}