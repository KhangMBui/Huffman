// Khang Minh Bui
// CSC143. Professor Francois Lepeintre

import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Scanner;

public class HuffmanTree2 {
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
	 * Constructs a Huffman tree from the given input stream.  
	 * Assumes that the standard bit representation has been used for the tree.
	 * @param input
	 */
	public HuffmanTree2(BitInputStream input) {
		this.root = buildTreeHelper(input);
	}
	public HuffmanNode buildTreeHelper(BitInputStream input) {
		int bit = input.readBit();
		HuffmanNode node = new HuffmanNode(-1, -1);
		if (bit == 0) {
			node.left = buildTreeHelper(input);
			node.right = buildTreeHelper(input);
		} else {
			node.ascii = read9(input);
		}
		return node;
	}
	/**
	 * Assigns codes for each character of the tree.  
	 * Assumes the array has null values before the method is called.  
	 * Fills in a String for each character in the tree indicating its code.
	 * @param codes
	 */
	public void assign(String[] codes) {
		assignHelper(codes, root, "");
	}
	void assignHelper(String[] codes, HuffmanNode root, String path) {
		if (root != null) {
			//If the current node is a leaf:
			if (!root.hasLeft()) {
				codes[root.ascii] = path;
			} else {
				assignHelper(codes, root.left, path + "1");
				assignHelper(codes, root.right, path + "0");
			}
		}
	}
	/**
	 * Writes the current tree to the output stream using the standard bit representation.
	 * @param output
	 */
	public void writeHeader(BitOutputStream output) {
		writeHeaderHelper(root, output);
	}
	void writeHeaderHelper(HuffmanNode root, BitOutputStream output) {
		if (root != null) {
			//As long as this node is a parent
            if (root.hasLeft()) {
                output.writeBit(0);
            } else {
                output.writeBit(1);
                write9(output, root.ascii);
            }
            writeHeaderHelper(root.left, output);
            writeHeaderHelper(root.right, output);
        }
	}
	// pre : 0 <= n < 512
	// post: writes a 9-bit representation of n to the given output stream
	private void write9(BitOutputStream output, int n) {
	    for (int i = 0; i < 9; i++) {
	        output.writeBit(n % 2);
	        n /= 2;
	    }
	}
	// pre : an integer n has been encoded using write9 or its equivalent
	// post: reads 9 bits to reconstruct the original integer
	private int read9(BitInputStream input) {
	    int multiplier = 1;
	    int sum = 0;
	    for (int i = 0; i < 9; i++) {
	        sum += multiplier * input.readBit();
	        multiplier *= 2;
	    }
	    return sum;
	}
	/**
	 * Constructs a Huffman tree using 
	 * the given array of frequencies where count[i] 
	 * is the number of occurrences of 
	 * the character with ASCII value i.
	 * @param count
	 */
	public HuffmanTree2(int[] count) {
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
		//if leaf:
		if (!n.hasLeft()) {
			//print code
			output.println(n.ascii + System.lineSeparator() + code);
		}
		else {
			writeHelper(code + "1", n.left, output);
			writeHelper(code + "0", n.right, output);
		}
	}
	/**
	 *Constructs a Huffman tree from the Scanner.  
	 *Assumes the Scanner contains a tree description in standard format.
	 * @param input
	 */
	public HuffmanTree2(Scanner input) {
		while (input.hasNextLine()) {
			int n = Integer.parseInt(input.nextLine());
			String code = input.nextLine();
			this.root = buildTree(this.root, code, n);
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
		if (code.length() == 1) {
			if (code.equals("1")) {
				root.left = new HuffmanNode(ascii, -1);
			}
			else {
				root.right = new HuffmanNode(ascii, -1);
			}
		}
		else {
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
	 * Reads bits from the given input stream and writes the corresponding characters to the output.  
	 * Stops reading when it encounters a character with value equal to eof.  
	 * This is a pseudo-eof character, so it should not be written to the output file.  
	 * Assumes the input stream contains a legal encoding of characters for this tree’s Huffman code.
	 * @param input
	 * @param output
	 * @param eof
	 */
	void decode(BitInputStream input, PrintStream output, int eof) {
		while (true) {
			HuffmanNode node = root;
			// -- inner loop
			// As long as the current node is not parent, read bit 
			// and assign its to the its children node
			while (node.left!= null) {
				// read bit
				int bit = input.readBit();
				// if 1 go left
				if (bit == 1) {
					node = node.left;
				}	
				// if 0 go right
				else {
					node = node.right;
				}
			}
			//Break out of the infinity loop if at end of file
			if (node.ascii == eof )
				break;
			//Else, print ascii of the current node
			else
				output.write(node.ascii);
		}
	}
	
	
}
