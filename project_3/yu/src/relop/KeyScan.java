package relop;

import global.SearchKey;
import heap.HeapFile;
import index.HashIndex;
import index.HashScan;
/**
 * Wrapper for hash scan, an index access method.
 */
public class KeyScan extends Iterator {
	  private HashIndex hash_index;
	  private SearchKey search_key;
	  private HeapFile hf;
	  private HashScan kscan;

	  /**
	   * Constructs an index scan, given the hash index and schema.
	   */
	  public KeyScan(Schema schema, HashIndex index, SearchKey key, HeapFile file) {
		  setSchema(schema);
	      hash_index = index;
	      search_key = key;
	      hf = file;
	      kscan = hash_index.openScan(search_key);
	  }


	  /**
	   * Gives a one-line explaination of the iterator, repeats the call on any
	   * child iterators, and increases the indent depth along the way.
	   */
	  public void explain(int depth) {
		  indent(depth);
	      System.out.println("Iterator for key scan.");
	  }

	  /**
	   * Restarts the iterator, i.e. as if it were just constructed.
	   */
	  public void restart() {
		  close();
		  kscan = hash_index.openScan(search_key);
	  }

	  /**
	   * Returns true if the iterator is open; false otherwise.
	   */
	  public boolean isOpen() {
		  if(kscan != null){
		    	return true;
		    }
		    return false;
	  }

	  /**
	   * Closes the iterator, releasing any resources (i.e. pinned pages).
	   */
	  public void close() {
		  if(isOpen()){
			  kscan.close();
			  kscan = null;
		  }
	  }

	  /**
	   * Returns true if there are more tuples, false otherwise.
	   */
	  public boolean hasNext() {
		  return kscan.hasNext();
	  }

	  /**
	   * Gets the next tuple in the iteration.
	   * 
	   * @throws IllegalStateException if no more tuples
	   */
	  public Tuple getNext() {
	    byte[] temp = hf.selectRecord(kscan.getNext());
	    if (temp != null){
	    	return new Tuple(getSchema(), temp);
	    } else{
	    	throw new IllegalStateException();
	    }
	    
	  }

} // public class KeyScan extends Iterator
