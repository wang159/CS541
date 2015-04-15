package relop;

/**
 * The projection operator extracts columns from a relation; unlike in
 * relational algebra, this operator does NOT eliminate duplicate tuples.
 */
public class Projection extends Iterator {
	private Iterator it;
    private Integer[] f;
  /**
   * Constructs a projection, given the underlying iterator and field numbers.
   */
  public Projection(Iterator iter, Integer... fields) {
	  it = iter;
	  f = fields;
	  this.schema = new Schema(f.length);
	  for(int i = 0; i<f.length; i = i +1){
		  this.schema.initField(i, it.schema , f[i]);
	  }
  }

  /**
   * Gives a one-line explaination of the iterator, repeats the call on any
   * child iterators, and increases the indent depth along the way.
   */
  public void explain(int depth) {
	  indent(depth);
	  System.out.println("Projection iteration for given fields. Depth is " + depth);
  }

  /**
   * Restarts the iterator, i.e. as if it were just constructed.
   */
  public void restart() {
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
  }

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public boolean hasNext() {
	  return it.hasNext();
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
	  Tuple temp = it.getNext();
	  if(temp != null){
		  Tuple afterProj = new Tuple(getSchema());
		  for(int i =0; i < f.length; i = i +1){
			  afterProj.setField(i, temp.getField(f[i]));
		  }
		  return afterProj;
	  }
	  else{
		  throw new IllegalStateException("File not open or no more tuples.");
	  }
  }

} // public class Projection extends Iterator
