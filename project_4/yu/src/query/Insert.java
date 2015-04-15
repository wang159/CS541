package query;

import global.Minibase;
import parser.AST_Insert;
import relop.Schema;
import relop.Tuple;
import heap.HeapFile;
import global.RID;
import index.HashIndex;
import global.SearchKey;
/**
 * Execution plan for inserting tuples.
 */
class Insert implements Plan {
	private String hf_name;
	private Object[] insertValues;
	private Schema scm;
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if table doesn't exists or values are invalid
   */
  public Insert(AST_Insert tree) throws QueryException {
	  hf_name = tree.getFileName();
	  QueryCheck.tableExists(hf_name);
	  insertValues = tree.getValues();
	  scm = Minibase.SystemCatalog.getSchema(hf_name);
	  QueryCheck.insertValues(scm, insertValues);
  } // public Insert(AST_Insert tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {
	  HeapFile hf = new HeapFile(hf_name);
	  Tuple tmpTuple = new Tuple(scm,insertValues);
	  RID rid = hf.insertRecord(tmpTuple.getData());
	  IndexDesc[] ind = Minibase.SystemCatalog.getIndexes(hf_name);
	  for(int i = 0; i<ind.length; i = i+1){
		  HashIndex hind = new HashIndex(ind[i].indexName);
		  hind.insertEntry(new SearchKey(tmpTuple.getField(ind[i].columnName)), rid);
	  }
      // print the output message
      System.out.println("1 rows inserted.");
  } // public void execute()

} // class Insert implements Plan
