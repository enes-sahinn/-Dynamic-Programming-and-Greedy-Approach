import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Enes_Sahin_2018510093 {
	
	static Node[] orderedLions;
	// A Node class to represent left child right sibling representation.
	static class Node {
		String name;
		int ability;
		boolean isHunting;
		Node next, child, parent;

		public Node(String name, int ability) {
			this.name = name;
			this.ability = ability;
			isHunting = false;
			next = child = parent = null;
		}
	}
	
	static public int DP(Node root) {
		Queue<Node> queue = new LinkedList<Node>();
		Queue<Node> queue2 = new LinkedList<Node>(); // A queue for holding lions according to their level
		queue.add(root);
		while (!queue.isEmpty()) {
			Node tempNode = queue.poll();
			queue2.add(tempNode); 
			if (tempNode.child != null) {
				queue.add(tempNode.child);
				if (tempNode.child.next != null) {
					tempNode = tempNode.child;
					while (tempNode.next != null) {
						queue.add(tempNode.next);
						tempNode = tempNode.next;
					}
				}
			}
		}
		orderedLions = new Node[queue2.size()];
		for (int i = 0; i < orderedLions.length; i++) {
			orderedLions[i] = queue2.poll();
		}
		
		int[] maxAbilities = new int[orderedLions.length];	// An array for holding posibilities for finding max ability 
		int i = orderedLions.length - 1;
		int maxLevel = findLevel(orderedLions[i]);			// A function for finding lions' level in the tree
		while(maxLevel == findLevel(orderedLions[i])) {		// First loop for assigning lions' ability to the array
			maxAbilities[i] = orderedLions[i].ability;
			i--;
		}
		while(maxLevel-1 == findLevel(orderedLions[i])) {	// Second loop for assigning max ability according to parent and children
			int childrenTotalAbility = 0;
			if (orderedLions[i].child != null) {
				Node tempNode = orderedLions[i].child;
				while (tempNode != null) {
					childrenTotalAbility = childrenTotalAbility + tempNode.ability;
					tempNode = tempNode.next;
				}
				if (childrenTotalAbility > orderedLions[i].ability) {
					tempNode = orderedLions[i].child;
					while (tempNode != null) {
						tempNode.isHunting = true;
						tempNode = tempNode.next;
					}
				}else {
					orderedLions[i].isHunting = true;
				}
				maxAbilities[i] = Math.max(childrenTotalAbility, orderedLions[i].ability);
			} else {
				maxAbilities[i] = orderedLions[i].ability;
				orderedLions[i].isHunting = true;
			}	
			i--;
		}
		int j = 2;
		while(j <= maxLevel) {			// Third loop for assigning max ability according to parent, children and grandchildren
			while(maxLevel-j == findLevel(orderedLions[i])) {
				int childrenTotalAbility = 0;
				int gChildrenTotalAbility = 0;
				if (orderedLions[i].child != null) {
					Node tempNode = orderedLions[i].child;
					while (tempNode != null) {
						if (tempNode.child != null) {
							Node tempNode2 = tempNode.child;
							while(tempNode2 != null) {
								gChildrenTotalAbility = gChildrenTotalAbility + findMaxAbility(tempNode2, orderedLions, maxAbilities);
								tempNode2 = tempNode2.next;
							}
						}
						childrenTotalAbility = childrenTotalAbility + findMaxAbility(tempNode, orderedLions, maxAbilities);
						tempNode = tempNode.next;
					}
					if (childrenTotalAbility < orderedLions[i].ability + gChildrenTotalAbility) {
						orderedLions[i].isHunting = true;
						if (orderedLions[i].child != null) {
							tempNode = orderedLions[i].child;
							while (tempNode != null) {
								if (tempNode.child != null) {
									Node tempNode2 = tempNode.child;
									while(tempNode2 != null) {									
										boolean itsChildHunting = false;
										if (tempNode2.child != null) {
											Node tempNode3 = tempNode2.child;
											while(tempNode3 != null) {
												if (tempNode3.isHunting == true) { 
													itsChildHunting = true;			
													break;
												}
												tempNode3 = tempNode3.next;
											}
										}	
										if (!itsChildHunting) 
											tempNode2.isHunting = true;	
										tempNode2 = tempNode2.next;
									}
								}
								tempNode.isHunting = false;
								tempNode = tempNode.next;
							}
						}
					}
					maxAbilities[i] = Math.max(childrenTotalAbility, orderedLions[i].ability + gChildrenTotalAbility);	
				} else {
					maxAbilities[i] = orderedLions[i].ability;
					orderedLions[i].isHunting = true;
				}
				i--;
				if (i < 0) break;	
			}
			j++;
		}
	return maxAbilities[0]; 
	}
	
	static public int Greedy() {
		int totalAbility = 0;
		int index = 0;
		int maxAbility = 0;
		for (int j = 0; j < orderedLions.length; j++) {
			orderedLions[j].isHunting = false;
		}			
		while (true) {			
			maxAbility = Integer.MIN_VALUE;	
			for (int i = 0; i < orderedLions.length; i++) {	// Loop for findng lion that has maximum ability 
				if (orderedLions[i].ability >= maxAbility) {
					maxAbility = orderedLions[i].ability;
					index = i;
				}
			}	// Assign -1 value to chosen lion's ability and its parent and children if they are exist 
			orderedLions[index].isHunting = true;	
			if (orderedLions[index].parent != null && orderedLions[index].child != null) {
				orderedLions[index].ability = orderedLions[index].parent.ability = -1;
				Node tempNode = orderedLions[index].child;
				while (tempNode != null) {
					tempNode.ability = -1;
					tempNode = tempNode.next;
				}
			}
			else if (orderedLions[index].parent == null && orderedLions[index].child != null) {
				orderedLions[index].ability = -1;
				Node tempNode = orderedLions[index].child;
				while (tempNode != null) {
					tempNode.ability = -1;
					tempNode = tempNode.next;
				}
			}
			else if (orderedLions[index].parent != null && orderedLions[index].child == null) {
				orderedLions[index].ability = orderedLions[index].parent.ability = -1;
			}
			if (maxAbility == -1) {
				break;
			}
			totalAbility += maxAbility;
		}
		return totalAbility;
	}
	
	
	// Adds a sibling to a tree
	static public void addSibling(Node child, Node sibling) {
		if (child != null) {
			child.next = sibling;
			sibling.parent = child.parent;
		}
	}
	// Add a child to a tree
	static public void addChild(Node parent, Node child) {
		if (parent != null) {
			parent.child = child;
			child.parent = parent;
		}
	}
	// For finding max ability of given node in the array that hold possiblities
	static int findMaxAbility(Node node, Node[] orderedLions, int[] maxAbilities) { 
		for (int i = 0; i < orderedLions.length; i++) { 
			if (orderedLions[i].name.equals(node.name)) { 
				return maxAbilities[i]; 
			}
		} return 0;
	}
	// For finding lion node while reading the file
	static public Node findLion(Node[] array, String name) {  
		for (int i = 0; i < array.length; i++) {
			if (array[i].name.equals(name)) {
				return (array[i]);
			}
		}
		return null;
	}
	
	static public int findLevel(Node node) {
		int level = 0;
		Node tempNode = node; 
		while (tempNode.parent != null) {
			tempNode = tempNode.parent;
			level++;
		}
		return level;
	}
	
	public static void printHuntingLions() {
		int maxLevel = findLevel(orderedLions[orderedLions.length-1]);
		int j = maxLevel;
		for (int i = 0; i < orderedLions.length; i++) {		
			if (findLevel(orderedLions[i]) == maxLevel-j) {
				System.out.print("\nLevel " + findLevel(orderedLions[i]) + ": ");
				j--;
			}
			if (orderedLions[i].isHunting == true) {
				System.out.print(orderedLions[i].name + ", ");
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		File file;
		Scanner scn;
		int lionsArrayCapacity = 0;
		file = new File("hunting_abilities.txt");
		scn = new Scanner(file);
		while (scn.hasNextLine()) {
			scn.nextLine();
			lionsArrayCapacity++;
		}
		scn.close();
		
		Node[] lions = new Node[lionsArrayCapacity-1];
		int index = -1;
		scn = new Scanner(file);
		while (scn.hasNextLine()) {
			String[] currentLine = scn.nextLine().split("\t");
			if (index > -1) {
				lions[index] = new Node(currentLine[0], Integer.parseInt(currentLine[1]));
			}
			index++;
		}
		scn.close();

		index = -1;
		file = new File("lions_hierarchy.txt");
		scn = new Scanner(file);
		Node root = new Node("",0);

		while (scn.hasNextLine()) {
			String[] currentLine = scn.nextLine().split("\t");
			if (index == 0) {
				root = findLion(lions, currentLine[0]);
				if (currentLine[2].contentEquals("Left-Child")) {
					addChild(root, findLion(lions, currentLine[1]));
				} else
					addSibling(root, findLion(lions, currentLine[1]));
			}
			if (index > 0) {
				if (currentLine[2].contentEquals("Left-Child")) {
					addChild(findLion(lions, currentLine[0]), findLion(lions, currentLine[1]));
				} else
					addSibling(findLion(lions, currentLine[0]), findLion(lions, currentLine[1]));
			}
			index++;
		}
		scn.close();

		System.out.println("DP Results: " + DP(root)); 
		System.out.print("DP Results- Selected Lions:");
		printHuntingLions();
		
		System.out.println("\n\n\nGreedy Results: " + Greedy());
		System.out.print("Greedy Results- Selected Lions:" );
		printHuntingLions();	
	}
}
