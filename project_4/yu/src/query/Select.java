package query;

import java.util.ArrayList;
import global.Minibase;
import parser.AST_Select;
import relop.*;
import heap.HeapFile;

/**
 * Execution plan for selecting tuples.
 */
class Select implements Plan {
	
	private Predicate[][] preds;
	private String[] tabs;
	private String[] cols;
	private Integer[] col_nums;
	private Iterator[] its;
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if validation fails
   */
  public Select(AST_Select tree) throws QueryException {
	  preds = tree.getPredicates();
	  tabs = tree.getTables();
	  cols = tree.getColumns();
	  
	  if(tabs == null || tabs.length == 0 || cols == null){
		  throw new QueryException("Tables/columns set incorrectly in the WHERE statement.");
	  }
	  // check existence of each table
	  for(int i = 0; i < tabs.length; i = i+1){
		  QueryCheck.tableExists(tabs[i]);
	  }
	  
	  
	  Schema joinedSchema = null;
	  for(int i = 0; i < tabs.length; i = i+1){		  
		  if(i == 0){
			  joinedSchema = QueryCheck.tableExists(tabs[i]);
			  continue;
		  }	  
		  joinedSchema = Schema.join(joinedSchema, QueryCheck.tableExists(tabs[i]));
	  }
	  
	  QueryCheck.predicates(joinedSchema, preds);
	  
	  if(cols.length > 0){
		  col_nums = new Integer[cols.length];
		  for(int i = 0; i < cols.length; i = i+1){
			  col_nums[i] = QueryCheck.columnExists(joinedSchema, cols[i]);
		  }

	  }
	  
	  its = new Iterator[tabs.length];
	  for(int i = 0; i < tabs.length; i = i+1){
		  Schema tmpsc = Minibase.SystemCatalog.getSchema(tabs[i]);
		  HeapFile tmphf = new HeapFile(tabs[i]);
		  its[i] = new FileScan(tmpsc,tmphf);
	  }

  } // public Select(AST_Select tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  
public void execute() {
	 
	// Join
 	 Iterator it_ex = its[0];
 	 for(int i = 1; i < tabs.length; i = i+1){
 		 it_ex = new SimpleJoin(it_ex,its[i]);
 	 }
	 // Select
 	 for(int i = 0; i<preds.length; i = i+1){
 		 it_ex = new Selection(it_ex, preds[i]);
 	 }
 	 // Projection
 	 if(cols.length>0){
 		 it_ex = new Projection(it_ex,col_nums);
 	 }
 	 
	 System.out.println(it_ex.execute()+ " rows selected.");
	 it_ex.close();
  } // public void execute()

} // class Select implements Plan
