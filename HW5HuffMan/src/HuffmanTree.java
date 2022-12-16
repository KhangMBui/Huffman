// Khang Minh Bui
// CSC143. Professor Francois Lepeintre

import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Scanner;

public class HuffmanTree {
	private class HuffmanNode implements Comparable<HuffmanNode>{
		private int frequency;
		private int ascii;
		private HuffmanNode left, right;
		
		@Override
		public int compareTo(HuffmanNode that) {
			return this.frequency - that.frequency;
		}	
		
		public HuffmanNode(int ascii, int frequency) {
			this.frequency = frequency;
			this.ascii = ascii;
			this.left = null;
			this.right = null;
		}
		
		public HuffmanNode(int ascii, int frequency, HuffmanNode nLeft, HuffmanNode nRight) {
			this.frequency = frequency;
			this.ascii = ascii;
			this.left = nLeft;
			this.right = nRight;
		}
		public boolean hasLeft() {
			return (this.left != null);
		}
	}
	
	private HuffmanNode root;
	/**
	 * Constructs a Huffman tree using 
	 * the given array of frequencies where count[i] 
	 * is the number of occurrences of 
	 * the character with ASCII value i.
	 * @param count
	 */
	public HuffmanTree(int[] count) {
		PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>();
		// Add all of the nodes corresponding to each non-zero element of count
		// add, remove
		for (int i = 0; i<count.length; i++) {
			if (count[i]>0) {
				HuffmanNode node = new HuffmanNode(i, count[i]);
				q.add(node);
			}
		}
		//The end of file value will be one higher than the value of the 
		//highest character in the frequency array passed to 
		//the constructor.  It will always have a frequency of 1 
		//because it appears exactly once at the end of each file to be encoded. 
		HuffmanNode pseudoEof = new HuffmanNode(count.length, 1);
		q.add(pseudoEof);
		//Combine two nodes as one by creating a new root node for them:
		while (q.size() >= 2) {
			//this remove function doesn't mean removing but retrieving the data of the 
			//head of the queue and assign its value to the variable
			HuffmanNode nodeL = q.remove();
			HuffmanNode nodeR = q.remove();
			//Create root for the two nodes above
			HuffmanNode root = new HuffmanNode(-1, nodeL.frequency + nodeR.frequency, nodeL, nodeR);
			q.add(root);
		}
		root = q.remove();
	}
	/**
	 * Writes the current tree to the given output stream in standard format.
	 * @param output
	 */
	public void write(PrintStream output) {
		writeHelper("", this.root, output);
	}
	private void writeHelper(String code, HuffmanNode n, PrintStream output) {
		//if leaf, print ascii and code
		if (!n.hasLeft()) {
			output.println(n.ascii + System.lineSeparator() + code);
		}
		else {
			writeHelper(code + "1", n.left, output);
			writeHelper(code + "0", n.right, output);
		}
	}
	public HuffmanNode buildTree(HuffmanNode root, String code, int ascii) {
		//If there is no root yet, create a root and set 
		//its frequency and ascii to basic value
		if (root == null) {
			root = new HuffmanNode(-1, -1);
		}
		//All frequencies in this method are set to -1
		//Case when we build the leaf node:
		//If read to the last digit of the code
		if (code.length() == 1) {
			//Go left if it's 1, and right if it's 0
			if (code.equals("1")) {
				root.left = new HuffmanNode(ascii, -1);
			}
			else {
				root.right = new HuffmanNode(ascii, -1);
			}
		}
		//Else, use recursion to gradually reach the leaf node:
		else {
			//Go left if it's 1, and right if it's 0
			if (code.charAt(0) == '1') {
				root.left = buildTree(root.left, code.substring(1), ascii);
			}
			else {
				root.right = buildTree(root.right, code.substring(1), ascii);
			}
		}
		return root;	
	}
	/**
	 *Constructs a Huffman tree from the Scanner.  
	 *Assumes the Scanner contains a tree description in standard format.
	 * @param input
	 */
	public HuffmanTree(Scanner input) {
		while (input.hasNextLine()) {
			//Get ascii
			int n = Integer.parseInt(input.nextLine());
			//Get code
			String code = input.nextLine();
			//Create the root
			this.root = buildTree(this.root, code, n);
		}
		
	}	
	/**
	 * Reads bits from the given input stream and writes the corresponding characters to the output.  
	 * Stops reading when it encounters a character with value equal to eof.  
	 * This is a pseudo-eof character, so it should not be written to the output file.  
	 * Assumes the input stream contains a legal encoding of characters for this tree�s Huffman code.
	 * @param input
	 * @param output
	 * @param eof
	 */
	void decode(BitInputStream input, PrintStream output, int eof) {
		while (true) {
			HuffmanNode node = root;
			// As long as the node is a parent
			while (node.left!= null) {
				// ead bit
				int bit = input.readBit();
				// If bit is 1, assign node to its left children
				if (bit == 1) {
					node = node.left;
				}	
				// If bit is 0, assign node to its right children
				else {
					node = node.right;
				}
			}
			// Break out of the infinite loop if node's ascii is at end of file
			if (node.ascii == eof )
				break;
			// Else, print the node's ascii
			else
				output.write(node.ascii);
		}
	}
}
