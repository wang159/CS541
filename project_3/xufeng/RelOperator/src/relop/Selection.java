package relop;

/**
 * The selection operator specifies which tuples to retain under a condition; in
 * Minibase, this condition is simply a set of independent predicates logically
 * connected by OR operators.
 */
public class Selection extends Iterator {
	private Iterator it;
	private Predicate[] predicates;
	private Tuple nextTuple;
  /**
   * Constructs a selection, given the underlying iterator and predicates.
   */
  public Selection(Iterator iter, Predicate... preds) {
	  it = iter;
	  predicates = preds;
	  setSchema(it.schema);
	  nextTuple = null;
  }

  /**
   * Gives a one-line explaination of the iterator, repeats the call on any
   * child iterators, and increases the indent depth along the way.
   */
  public void explain(int depth) {
	  indent(depth);
	  System.out.println("Selection iterator for given predicates.");
  }

  /**
   * Restarts the iterator, i.e. as if it were just constructed.
   */
  public void restart() {
	  nextTuple = null;
	  it.restart();
  }

  /**
   * Returns true if the iterator is open; false otherwise.
   */
  public boolean isOpen() {
	  return it.isOpen();
  }

  /**
   * Closes the iterator, releasing any resources (i.e. pinned pages).
   */
  public void close() {
	  it.close();
	  nextTuple = null;
  }

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public boolean hasNext() {
	  if(isOpen()){
		  while(it.hasNext()){
			  Tuple temp = it.getNext();
			  boolean meet = false;
			  for(int i = 0 ; i < predicates.length; i = i+1){
				  //System.out.println("here" + predicates[i].evaluate(temp));
				  meet  = meet || predicates[i].evaluate(temp);
			  }
			  
			  if(meet){
				 nextTuple = temp;
				 return true;
			  }
			  else{
				  nextTuple = null;
			  }
		  }
		  return false;
	  }
	  return false;
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
	  if(nextTuple != null){
		  return nextTuple;
	  }
	  else{
		  throw new IllegalStateException("File not open or no more tuples meet the predicates.");
	  }
  }

} // public class Selection extends Iterator
