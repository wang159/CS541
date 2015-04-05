package relop;

import global.RID;
import heap.HeapFile;
import heap.HeapScan;

/**
 * Wrapper for heap file scan, the most basic access method. This "iterator"
 * version takes schema into consideration and generates real tuples.
 */
public class FileScan extends Iterator {
    private HeapFile hf;
	private HeapScan scan;
	private RID rid;
	
	
  /**
   * Constructs a file scan, given the schema and heap file.
   */
  public FileScan(Schema schema, HeapFile file) {
	  setSchema(schema);
	  hf = file;
	  scan = hf.openScan();
	  rid = new RID();
  }

  /**
   * Gives a one-line explaination of the iterator, repeats the call on any
   * child iterators, and increases the indent depth along the way.
   */
  public void explain(int depth) {
	  indent(depth);
      System.out.println("This is the iterator for file scan " + hf.toString());
  }

  /**
   * Restarts the iterator, i.e. as if it were just constructed.
   */
  public void restart() {
	  close();
	  scan = hf.openScan();
  }

  /**
   * Returns true if the iterator is open; false otherwise.
   */
  public boolean isOpen() {
	  if(scan != null){
		  return true;
	  }
	  return false;
  }

  /**
   * Closes the iterator, releasing any resources (i.e. pinned pages).
   */
  public void close() {
    if(isOpen()){
    	scan.close();
    	scan = null;
    }
  }

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public boolean hasNext() {
    if(isOpen()){
    	return scan.hasNext();
    }
    return false;
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
	byte[] temp = scan.getNext(rid);
    if(temp != null){
	  return new Tuple(getSchema(),temp);
    }else{
    	throw new IllegalStateException("File is not open or no next tuple.");
    }
  }

  /**
   * Gets the RID of the last tuple returned.
   */
  public RID getLastRID() {
      RID tmp = new RID(rid);
	  return tmp;
  }

} // public class FileScan extends Iterator
