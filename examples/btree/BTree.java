package btree;

import java.util.*;
import java.io.*;

/*
 * @author Jason R Shaffner
 * simple server port: 2372
 */

public class BTree {

	public int count;
	Node root;
	final int order;

	//constructor	
	public BTree(int order) {
		this.order = order;
		root = new Node();
		count = 0;
	}

	class Node {
		String[] values;
		Node[]links;
		
		//constructor
		public Node() {
			values = new String[order-1];
			links = new Node[order];
		}

		//return true if values array is full
		boolean isFull() {
			return values[order-2] != null;
		}

		//return true if values array is empty
		boolean isEmpty() {
			return this == null;
		}

		//returns true if values array contains value
		boolean contains(String s) {
			for (int i = 0; i < order-1; i++) 
				if (values[i] != null && values[i].compareTo(s) == 0) return true;
			return false;
		}

		//returns values and links of first half of node
		Node lvalues() {
			Node lvalues = new Node();
			for (int i = 0; i < order/2-1; i++) {
				lvalues.values[i] = values[i];
				lvalues.links[i] = links[i];
			}
			lvalues.links[order/2-1] = links[order/2-1];
			return lvalues;
		}

		//returns values and links of second half of node
		Node rvalues() {
			Node rvalues = new Node();
			for (int i = 0; i < order/2-1; i++) {
				rvalues.values[i] = values[i+order/2];
				rvalues.links[i] = links[i+order/2];
			}
			rvalues.links[order/2-1] = links[order-1];
			return rvalues;
		}

		//inserts value into values array
		boolean insert(String s) {
			if (contains(s)) return false;
			for (int i = 0; i < order-1; i++) {
				if (values[i] == null || s.compareTo(values[i]) < 0) {
					for (int j = order-2; j > i; j--)
						values[j] = values[j - 1];
					values[i] = s;
					count++;
					return true;
				}
			}
			return false;
		}
	
		//inserts array of values into values array
		void insert(String[] s) {
			for (int i = 0; i < order - 1; i++)
				if (s[i] != null) insert(s[i]);
		}

		boolean rid(String s) {
			if (!contains(s)) return false;
    	for (int i = 0; i < order-1; i++)
      	if (s.equals(values[i])) { //find index of s
     			for (int j = i; j < order-2; j++)
       			values[j] = values[j+1]; //remove s from values
					values[order-2] = null;
					break;
				}
			count--;
      return true;
    }

		String[] rid(String[] s) {
			for (int i = 0; i < order-1; i++)
				rid(s[i]);
			return s;
		}
	
		void merge(Node n, Node parent) {
			for (int i = 0; i < order; i++)
				if (parent.links[i] == this) {
					send(parent.values[i]); //send separator to node
					for (int j = i; j < order-1; j++) { //remove separator and right node from parent
						if (j > i) parent.links[j] = parent.links[j+1];
						if (j < order-2) parent.values[j] = parent.values[j+1];
					}
					break;
				}
			parent.values[order-2] = null;
			parent.links[order-1] = null;
			send(n.values); //send siblings values to node
			link(n.links); //send siblings children to node
		}

		//version of insert for sending values between nodes
		void send(String s) {
			if (contains(s)) return;
			for (int i = 0; i < order-1; i++) {
				if (values[i] == null || s.compareTo(values[i]) < 0) {
					for (int j = order-2; j > i; j--)
						values[j] = values[j - 1];
					values[i] = s;
					return;
				}
			}
		}

		void send(String[] s) {
			for (int i = 0; i < order - 1; i++)
				if (s[i] != null) send(s[i]);
		}

		//inserts node into parent's links array
		void link(Node n) {
			for (int i = 0; i < order-1; i++)
				if (values[i] == null || n.values[0].compareTo(values[i]) < 0) {
					if (links[i] == null || links[i].contains(n.values[0])) {
							links[i] = n;
							return;
					}
					for (int j = order - 1; j > i; j--)
						links[j] = links[j-1];
					links[i] = n;
					return;
				} else if (values[order-2] != null && n.values[0].compareTo(values[order-2]) > 0) {
					links[order-1] = n;
					return;
				}
		}

		void link (Node[] n) {
			for (int i = 0; i < order; i++)
				if (n[i] != null) link(n[i]);
		}

		//splits root into three nodes, root retains middle value, lower values go to links[0], higher values go to links[1]
		void splitRoot() {
			Node temp = new Node();
			temp.values[0] = root.values[order/2-1];
			temp.links[0] = root.lvalues();
			temp.links[1] = root.rvalues();
			root = temp;
		}

		//splits node into two nodes, middle value gets inserted in parent values array, two new nodes get linked to parent
		void split(Node parent) {
			if (parent.insert(values[order/2-1])) count--;
			parent.link(lvalues());
			parent.link(rvalues());
		}
	}

	//returns the parent node
	Node parent(Node h, String s, Node parent) {
		if (h.contains(s)) return parent;
		for (int i = 0; i < order-1; i++) {
			if (h.values[i] == null || s.compareTo(h.values[i]) < 0) {
					if (h.links[i] != null) return parent(h.links[i],s,h);
				  else return null;
			} else if (h.values[order-2] != null && s.compareTo(h.values[order-2]) > 0)
					return parent(h.links[order-1],s,h);
		} 
		return null;
	}

	Node parent(Node h) {
		if (h == root) return null;
		else return parent(root,h.values[0],null);
	}
	//adds value to btree
	boolean add(Node h, String s, Node parent) {
		if (h.isFull())
			if (h == root) {
				h.splitRoot();
				return add(s);
			} else {
			  h.split(parent);
				return add(s);
			}
		if (h.contains(s)) return false;
		for (int i = 0; i  <  order-1; i++)
			if (h.values[i] == null || s.compareTo(h.values[i]) < 0)
				if (h.links[0] != null) return add(h.links[i],s,h);
				else return h.insert(s);
		return false;
	}

	//adds value to btree
	public boolean add(String s) {
		if (root.values[0] == null) return root.insert(s);
		else return add(root,s, null);
	}

	//returns the smallest value in the subtree
	String successor(Node h) {
		if (h.links[0] != null) return successor(h.links[0]);
		else return h.values[0];
	}

	//returns the largest value in the subtree
	String predecessor(Node h) {
		if (h.links[0] == null) {
			for (int i = order-2; i >= 0; i--)
				if (h.values[i] != null) return h.values[i];
		} else
			for (int i = order-1; i >= 0; i--)
				if (h.links[i] != null) return predecessor(h.links[i]);
		return null;
	}

	//removes the value from the tree
	boolean remove(Node h, String s, Node parent) {
		if (!h.contains(s)) { //find s
			for (int i = 0; i < order-1; i++)
				if (h.values[i] == null || s.compareTo(h.values[i]) < 0) 
					if (h.links[i] != null) return remove(h.links[i],s,h);
					else return false;
				else if (h.values[order-2] != null && s.compareTo(h.values[order-2]) > 0)
					if (h.links[order-1] != null) return remove(h.links[order-1],s,h);
					else return false;
			return false;
		} else if (h.links[0] != null) { //h is internal
			for (int i = 0; i < order-1; i++)
				if (s.equals(h.values[i])) //find index of s
					if (h.links[i] != null && h.links[i].values[order/2-1] == null &&
							h.links[i+1] != null && h.links[i+1].values[order/2-1] == null) { //two children can merge
						h.links[i].merge(h.links[i+1],h); //merge children
						if (h != root && h.values[order/2-2] == null) rebalanceAfterDelete(h,parent);
						return remove(s);
					} else { //get predecessor
						h.values[i] = predecessor(h.links[i]); //replace s with predecessor
						return remove(h.links[i],h.values[i],h); //remove predecessor
					}
		} else { //h is leaf
			h.rid(s);
			if (h.values[order/2 - 2] != null || h == root) return true;
			else //h will underflow
				for (int i = 0; i < order; i++)
					if (parent.links[i] == h) { //find index of h in parent
						if (i > 0 && parent.links[i-1].values[order/2-1] != null) { //h can steal from left sibling
							h.send(parent.values[i-1]); //insert separator into h
							parent.values[i-1] = predecessor(parent.links[i-1]);
							count++;
							return remove(parent.links[i-1],parent.values[i-1],parent); //remove last value from sibling
						} else if (i < order-1 && parent.links[i+1] != null && parent.links[i+1].values[order/2-1] != null) { //h can steal from right sibling
							h.send(parent.values[i]); //insert separator into h
							parent.values[i] = successor(parent.links[i+1]); //replace separator with first value in right sibling
							count++;
							return remove(parent.links[i+1],parent.values[i],parent); //remove first value in right sibling
						} else if (i > 0) { //merge with left sibling
							parent.links[i-1].merge(h,parent); //merge h and left sibling
							break;
						} else if (i < order-1 && parent.links[i+1] != null) { //merge with right sibling
							h.merge(parent.links[i+1],parent); //merge h and right sibling
							break;
						}
					}
				if (parent.values[order/2-2] == null) //merge made parent underflow
					return rebalanceAfterDelete(parent,parent(parent));
				else return true;
		}
		return false;
	}

	public boolean remove(String s) {
		if (root.links[0] != null) return remove(root,s,null);
		else return (root.rid(s));
	}

	//rebalances the tree after deletion
	boolean rebalanceAfterDelete(Node h, Node parent) {			
		if (h == root) { //h is root
			if (h.values[0] == null)
				root = h.links[0];
			else if (h.links[1] == null) {
				add(h.links[0],h.values[0],null);
				count--;
				root = h.links[0];
			}
			return true;
		} else
			for (int i = 0; i < order; i++)
				if (parent.links[i] == h) //find index of h in parent
					if (i > 0 && parent.links[i-1] != null && parent.links[i-1].values[order/2-1] != null && parent.links[i-1].links[0] != null) { //can work with left sibling
						h.send(parent.values[i-1]); //insert separator into h
						for (int j = order-2; j >= 0; j--)
							if (parent.links[i-1].values[j] != null) { 
								parent.values[i-1] = parent.links[i-1].values[j]; //replace separator with last element in left child
								parent.links[i-1].values[j] = null; //remove last value of left sibling
								h.link(parent.links[i-1].links[j+1]); //link last child of left sibling to h
								parent.links[i-1].links[j+1] = null; //remove last child of left sibling
								break;
							}
						return true;							
					} else if (i < order-1 && parent.links[i+1] != null && parent.links[i+1].values[order/2-1] != null && parent.links[i+1].links[0] != null) { //can work with right sibling
						h.send(parent.values[i]); //insert separator into h
						parent.values[i] = parent.links[i+1].values[0]; //replace separator with first element in right sibling
						h.link(parent.links[i+1].links[0]); //link first link of right child into h
						for (int j = 0; j < order-1; j++) {
							parent.links[i+1].links[j] = parent.links[i+1].links[j+1]; //remove first link from right child
						  if (j < order-2) parent.links[i+1].values[j] = parent.links[i+1].values[j+1];
						}
						parent.links[i+1].links[order-1] = null; 
						parent.links[i+1].values[order-2] = null;
						return true;
					} else if (i > 0) { //merge with left sibling
						parent.links[i-1].merge(h,parent); //merge h with left sibling
						break;
					} else if (i < order-1) { //merge with right sibling
						h.merge(parent.links[i+1],parent); //merge h with right sibling
						break;
					}
				if (parent.values[order/2-2] == null) return rebalanceAfterDelete(parent,parent(parent));
				else return true;	
	}
									
	boolean search(Node h, String s, Node parent) {
		if (h== null) return false;
		if (h.isFull())
			if (h == root) {
				h.splitRoot();
				search(root,s,null);
			} else {
				h.split(parent);
				search(s);
			}
		if (h.contains(s)) return true;
		for (int i = 0; i < h.values.length; i++)
			if (h.values[i] == null || s.compareTo(h.values[i]) < 0) {
				if (h.links[i] != null) return search(h.links[i],s,h);
			} else if (i == order-2 && s.compareTo(h.values[order-2]) > 0)
				if (h.links[order-1] != null) return search(h.links[order-1],s,h);
	return false;
	}

	public boolean search(String s) {
		return search(root,s,null);
	}

	//displays btree
	void display(Node n) {
		for (int i = 0; i < n.values.length; i++)
			if (n.values[i] != null)
				System.out.print(n.values[i] + "\t");
		for (int j = 0; j < n.links.length; j++) {
			if (n.links[j] != null) {
				System.out.println("\n" + n.values[0] + " " + j);
				display(n.links[j]);
			}
		}
		System.out.println();
	}

	//displays btree
	public void display() {
		display(root);
		System.out.println("There are " + count + " values in total.");
	}
	
	public static void main (String[] args) {
		BTree bt = new BTree(8);
		Scanner s = new Scanner(System.in);
		Scanner sc = new Scanner(System.in);
		try {
			sc = new Scanner(new File("btree/words.txt"));
		} catch (FileNotFoundException f) {}
		System.out.println("BTree Tester...\nBuilding>>>");

		while (sc.hasNext()) {
		       String t = sc.next();	
			if (!bt.add(t)) System.out.println(t);
		}

		System.out.println("Complete...");
		System.out.println("Words: " + bt.count);
		System.out.println("Press enter to display tree");
		String st = s.nextLine();
		bt.display();

		st = s.nextLine();
		while (!st.equals("q")) {
			System.out.println("[A]dd or [R]emove???");
			st = s.nextLine();
			System.out.println("Word :");
			if (st.equalsIgnoreCase("A")) {
				bt.add(s.nextLine());
				bt.display();
			} else if (st.equalsIgnoreCase("R")) {
				bt.remove(s.nextLine());
				bt.display();
			} else if (st.equalsIgnoreCase("q")) break;
			else continue;
		}

		System.out.println("Press enter to delete all words");
		st = s.nextLine();
		try {
			sc = new Scanner(new File("btree/words.txt"));
		} catch (FileNotFoundException f) {}
		while (sc.hasNext()) {
			String t = sc.nextLine();
			if (bt.remove(t)) System.out.println(t);
			else System.out.println(t + " not deleted");
		}

		

		System.out.print("Press enter to display tree");
			st = s.nextLine();
			bt.display();

		try {
			sc = new Scanner(new File("btree/words.txt"));
		} catch (FileNotFoundException f) {}
		while (sc.hasNext()) {
			String t = sc.next();
			if (bt.search(t)) System.out.println(t);
		}
		System.out.println("Words left: " + bt.count);
		st = s.nextLine();
		while (!st.equals("q")) {
			System.out.println("[A]dd or [R]emove???");
			st = s.nextLine();
			System.out.println("Word :");
			if (st.equalsIgnoreCase("A")) {
				bt.add(s.nextLine());
				bt.display();
			} else if (st.equalsIgnoreCase("R")) {
				bt.remove(s.nextLine());
				bt.display();
			} else if (st.equalsIgnoreCase("q")) break;
			else continue;
		}
	}
}
