package query;

import global.Minibase;
import parser.AST_Update;
import relop.*;
import heap.HeapFile;
import global.RID;
import index.HashIndex;
import global.SearchKey;

/**
 * Execution plan for updating tuples.
 */
class Update implements Plan {
  private String hf_name;
  private Object[] insertValues;
  private Schema scm;
  private int[] col_num;
  private String[] col;
  private Predicate[][] preds;
  private Iterator its;
// private FileScan its;	  	
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if invalid column names, values, or pedicates
   */
  public Update(AST_Update tree) throws QueryException {
	  hf_name = tree.getFileName(); // file name
	  col = tree.getColumns(); // column name
	  insertValues = tree.getValues(); // value
	  preds = tree.getPredicates(); // predicates
	  	  
	  QueryCheck.tableExists(hf_name);
	  scm = Minibase.SystemCatalog.getSchema(hf_name);

	  QueryCheck.predicates(scm, preds);

	  if(col.length > 0){
		  col_num = new int[col.length];
		  for(int i = 0; i < col.length; i = i+1){
			  col_num[i] = QueryCheck.columnExists(scm, col[i]);
		  }
	  }
	  QueryCheck.updateValues(scm, col_num, insertValues);
  } // public Update(AST_Update tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {
 	HeapFile hf = new HeapFile(hf_name);
	FileScan scan = new FileScan(scm, hf);

	int cnt = 0;
    if (preds.length > 0) {
        // certain predicates are given
     	Selection sel = new Selection(scan, preds[0][0]);
     	
	    for(int pi = 0; pi < preds.length; pi = pi+1){	
	        for(int pj = 0; pj < preds[pi].length; pj = pj+1){
	            sel = new Selection(sel, preds[pi][pj]);
	        }
	    }

	    while(sel.hasNext()){
	        Tuple tmpTuple = new Tuple(scm,hf.selectRecord(scan.getLastRID())); // get the current record
	        
	        if(col.length > 0){
		      col_num = new int[col.length];
		      for(int i = 0; i < col.length; i = i+1){
			      col_num[i] = scm.fieldNumber(col[i]);
			      tmpTuple.setField(col_num[i],insertValues[i]);
		      }
	        }
	      
		    hf.updateRecord(scan.getLastRID(),tmpTuple.getData());
		    cnt++;
	    }
    } else {
        // no predicates are given	
        System.out.println("Update with no predicates: The behavior of our code is to update all the rows."); 	     	
	    while(scan.hasNext()){
	        //Tuple tmpTuple = new Tuple(scm,hf.selectRecord(scan.getLastRID())); // get the current record
	        Tuple tmpTuple = scan.getNext(); // get the current record	            System.out.println("Column name = "+col.length);	  
	        if(col.length > 0){
		      col_num = new int[col.length];
		      for(int i = 0; i < col.length; i = i+1){
			      col_num[i] = scm.fieldNumber(col[i]);
			      tmpTuple.setField(col_num[i],insertValues[i]);
		      }
	        }

		    hf.updateRecord(scan.getLastRID(),tmpTuple.getData());
		    cnt++;
	    }
    }

    scan.restart();
    scan.execute();
	scan.close();  
	        
    // print the output message
    System.out.println("UPDATE: " +cnt+ " rows affected.");

  } // public void execute()

} // class Update implements Plan
