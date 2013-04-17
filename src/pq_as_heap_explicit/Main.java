package pq_as_heap_explicit;

public class Main {
	public static void main (String[] args) {
		PQueue Q = new PQueue();
		Q.insert(1);
		Q.insert(2);
		Q.insert(3);
		Q.insert(4);
		Q.insert(5);
		Q.insert(6);
		Q.insert(7);
		Q.insert(8);
		Q.insert(9);
		Q.insert(10);
		Q.insert(11);
		Q.insert(12);
		Q.insert(13);
		Q.insert(14);
		Q.insert(15);

		while (Q.count != 0) {
			System.out.println("Min: " + Q.deleteMin());
		}
	}
}
