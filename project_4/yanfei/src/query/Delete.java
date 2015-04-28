package query;

import parser.AST_Delete;
import java.util.ArrayList;
import java.util.Map.Entry;
import global.Minibase;
import relop.*;
import heap.HeapFile;

/**
 * Execution plan for deleting tuples.
 */
class Delete implements Plan {

  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if table doesn't exist or predicates are invalid
   */
   
   private Predicate[][] preds;
   private String tab;
   
   private FileScan scan;
   private HeapFile hf;
   
  public Delete(AST_Delete tree) throws QueryException {
	preds = tree.getPredicates();
	tab = tree.getFileName();
	
/*
	if(tab == null || tab.length == 0){
		throw new QueryException("Table set incorrectly in the WHERE statement.");  // ?
	}
*/
	
	QueryCheck.tableExists(tab);
	Schema tab_schema = Minibase.SystemCatalog.getSchema(tab);
	QueryCheck.predicates(tab_schema, preds);
	
	hf = new HeapFile(tab);
	scan = new FileScan(tab_schema, hf);
	

  } // public Delete(AST_Delete tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {
	
	Selection sel = new Selection(scan, preds[0]);
	
  	for(int pi = 1; pi < preds.length; pi = pi+1){
		for(int pj = 0; pj < preds[pi].length; pj = pj+1){
			sel = new Selection(sel, preds[pi][pj]);
		}
	}
  
	int cnt = 0;
	while(sel.hasNext()){
		hf.deleteRecord(scan.getLastRID());
		cnt++;
	}

    // print the output message
	if(cnt == 0){
//		System.out.println(preds[0][0].toString());
		System.out.println("-- No record with given predicates!");
	}
	else {
//		System.out.println(preds[0][0].toString());
		System.out.println("-- Record with given predicates has been deleted!");
	}
	scan.close();

  } // public void execute()

} // class Delete implements Plan
