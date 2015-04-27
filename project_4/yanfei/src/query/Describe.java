package query;

import parser.AST_Describe;
import java.util.ArrayList;
import java.util.Map.Entry;
import global.Minibase;
import global.AttrType;
import heap.HeapFile;
import relop.*;


/**
 * Execution plan for describing tables.
 */
class Describe implements Plan {

	private String tab;
	private FileScan scan;
    private HeapFile hf;
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if table doesn't exist
   */
  public Describe(AST_Describe tree) throws QueryException {

	tab = tree.getFileName();
	QueryCheck.tableExists(tab);
	
	Schema tab_schema = Minibase.SystemCatalog.getSchema(tab);
	hf = new HeapFile(tab);
	scan = new FileScan(tab_schema, hf);

  } // public Describe(AST_Describe tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {
	
	Schema tab_schema = Minibase.SystemCatalog.getSchema(tab);
	System.out.println("Table: "+ tab);
	System.out.print("Columns: ");
	for(int i = 0; i < tab_schema.getCount(); i++){
		System.out.print(tab_schema.fieldName(i)+"("+AttrType.toString(tab_schema.fieldType(i))+")");
	}
    // print the output message
 //   System.out.println("(Not implemented)");
	scan.close();
  } // public void execute()

} // class Describe implements Plan
