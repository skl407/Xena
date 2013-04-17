package pq_as_heap_explicit;

public class PQueue {
	BinaryTreeNode root;
	BinaryTreeNode lastNode;
	int count;
	//int size;

	public PQueue(){
		root = null; //create empty tree
		count = 0; //set count to 0
		//size = n; //set max size
	}

	//public boolean isFull(){return (count == size);}

	public boolean isEmpty(){return (count == 0);}

	public void insert(double d){//Precond: not full
		count = count+1; //increment count
		root = insert(d,root, count); //call private insert()
	}

	private BinaryTreeNode insert(double d, BinaryTreeNode r, int n){ 
		if (n==1) return new BinaryTreeNode(d,null,null); //if count is 1, return node with two empty subtrees(base case)

		//The tree level is the log of the current node number
		int l = (int) (Math.log(n)/ Math.log(2));// Floor{log_2(n)}

		if (n < (int) (Math.pow(2,l) + Math.pow(2,l-1))){ //If the current node is on the left side of the tree
			r.left = insert(d, r.left, (int) (n - Math.pow(2, l-1))); //Call insert on the left child
			if (r.data > r.left.data){ //Swap parent and left child data if parent data > left child data
				double tmp = r.data;
				r.data = r.left.data;
				r.left.data = tmp;
			}
		}

		else{ //If the current node is on the right side of the tree
			r.right = insert(d, r.right, (int) (n - Math.pow(2, l-1) - Math.pow(2, l-1))); //call insert on right child
			if (r.data > r.right.data){ //Swap parent and right child data if parent data greater than right child data
				double tmp = r.data;
				r.data = r.right.data;
				r.right.data = tmp;
			}
		}

		return r; //root will always contain lowest value at this point
	}
	public double min(){return root.data;}//PRECOND: PQ not empty

	public double deleteMin() {
		lastNode = root;
		double min = root.data;
		root.data = deleteMin(count);
		count = count - 1;
		sortHeap(root);
		return min;
	}

	private double deleteMin(int nodeCount) {
		int level = (int)(Math.log(nodeCount)/Math.log(2));
		if (nodeCount == 1) {
			root = null;
		}

		//		else if (nodeCount == 2) {
		//			BinaryTreeNode newRoot = root.left;
		//			root.left = null;
		//			root = newRoot; 
		//			root.right = oldRoot.right;
		//			root.left = oldRoot.left;
		//		}
		//
		//		else if (nodeCount == 3) {
		//			BinaryTreeNode newRoot = root.right;
		//			root.right = null;
		//			root = newRoot;	
		//			root.right = oldRoot.right;
		//			root.left = oldRoot.left;
		//		}

		else if (nodeCount < (Math.pow(2,level) + Math.pow(2,level + 1) - 1)/2) {
			lastNode = lastNode.left;
			if (nodeCount != 2) {
				deleteMin((int)(nodeCount - Math.pow(2,level-1)));
			}
		}

		else { 
			lastNode = lastNode.right;
			if (nodeCount != 3) {
				deleteMin((int)(nodeCount - Math.pow(2,level)));
			}
		}

		return lastNode.data;
	}

	public void sortHeap(BinaryTreeNode curNode) {
		if (curNode == null || curNode.left == null) { //if the left child is empty, the heap is sorted
			return;
		}

		if (curNode.right == null || (curNode.left.data < curNode.right.data)) { //if there is only a left child, or if the left child is less than the right child
			if (curNode.data > curNode.left.data) { //if the current node is greater than its left child
				swapData(curNode, curNode.left); //swap
				sortHeap(curNode.left); //original data is now in left child of current node
			}
		}

		else { //if right child is not null and is less than the left child
			if (curNode.data > curNode.right.data) {
				swapData(curNode, curNode.right);
				sortHeap(curNode.right);
			}
		}
	}

	public void swapData(BinaryTreeNode nodeA, BinaryTreeNode nodeB) {
		double AData = nodeA.data;
		nodeA.data = nodeB.data;
		nodeB.data = AData;
	}

}