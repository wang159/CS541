package query;

import global.Minibase;
import index.HashIndex;
import parser.AST_DropIndex;

/**
 * Execution plan for dropping indexes.
 */
class DropIndex implements Plan {
	private String hf_name;
	
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if index doesn't exist
   */
  public DropIndex(AST_DropIndex tree) throws QueryException {
	  hf_name = tree.getFileName();
	  QueryCheck.indexExists(hf_name);
  } // public DropIndex(AST_DropIndex tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {
	// drop the index
	    new HashIndex(hf_name).deleteFile();
	    Minibase.SystemCatalog.dropIndex(hf_name);
    // print the output message
       System.out.println("Index " + hf_name + " dropped.");

  } // public void execute()

} // class DropIndex implements Plan
