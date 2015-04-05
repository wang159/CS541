package relop;

import global.SearchKey;
import heap.HeapFile;
import index.HashIndex;
import index.BucketScan;
/**
 * Wrapper for bucket scan, an index access method.
 */
public class IndexScan extends Iterator {
	private HashIndex hash_index;
	private HeapFile hf;
	private BucketScan bkscan;
  /**
   * Constructs an index scan, given the hash index and schema.
   */
  public IndexScan(Schema schema, HashIndex index, HeapFile file) {
	  setSchema(schema);
	  hash_index = index;
	  hf = file;
	  bkscan = hash_index.openScan();
  }

  /**
   * Gives a one-line explaination of the iterator, repeats the call on any
   * child iterators, and increases the indent depth along the way.
   */
  public void explain(int depth) {
	  indent(depth);
	  System.out.println("This is the iterator for index scan " + hash_index.toString() );
  }

  /**
   * Restarts the iterator, i.e. as if it were just constructed.
   */
  public void restart() {
	  bkscan.close();
	  bkscan = hash_index.openScan();
  }

  /**
   * Returns true if the iterator is open; false otherwise.
   */
  public boolean isOpen() {
	  if(bkscan != null){
		  return true;
	  }
	  return false;
  }

  /**
   * Closes the iterator, releasing any resources (i.e. pinned pages).
   */
  public void close() {
	  if(isOpen()){
		  bkscan.close();
		  hash_index = null;
		  hf = null;
	  }
  }

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public boolean hasNext() {
	  if(isOpen()){
	    	return bkscan.hasNext();
	    }
	    return false;
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
	  byte[] temp = hf.selectRecord(bkscan.getNext());
	  if(temp != null){
		  return new Tuple(getSchema(),temp);
	    }else{
	    	throw new IllegalStateException("File is not open or no next tuple.");
	    }
  }

  /**
   * Gets the key of the last tuple returned.
   */
  public SearchKey getLastKey() {
	  return bkscan.getLastKey();
  }

  /**
   * Returns the hash value for the bucket containing the next tuple, or maximum
   * number of buckets if none.
   */
  public int getNextHash() {
    return bkscan.getNextHash();
  }

} // public class IndexScan extends Iterator
