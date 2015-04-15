package query;

import parser.AST_CreateIndex;
import global.Minibase;
import global.SearchKey;
import heap.HeapFile;
import relop.Schema;
import relop.Tuple;
import relop.FileScan;
import index.HashIndex;
/**
 * Execution plan for creating indexes.
 */
class CreateIndex implements Plan {
	private String hf_name;
	private String IxTable;
	private Schema scm;
	private String IxColumn;
	private int which_col;
	
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if index already exists or table/column invalid
   */
  public CreateIndex(AST_CreateIndex tree) throws QueryException {
	  hf_name = tree.getFileName();
	  QueryCheck.fileNotExists(hf_name);
	  IxTable = tree.getIxTable();
	  scm = QueryCheck.tableExists(IxTable);
	  IxColumn = tree.getIxColumn();
	  which_col = QueryCheck.columnExists(scm, IxColumn);
	  IndexDesc[] indDes= Minibase.SystemCatalog.getIndexes(hf_name);
	  if(indDes.length>0){
		  throw new QueryException("An index has already been built for this column.");
	  }
  } // public CreateIndex(AST_CreateIndex tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {
	  HeapFile hf = new HeapFile(IxTable);
	  FileScan fscan = new FileScan(scm,hf);
	  HashIndex hashind = new HashIndex(hf_name);
	  while(fscan.hasNext()){
		  Tuple tmpTuple = fscan.getNext();
		  hashind.insertEntry(new SearchKey(tmpTuple.getField(which_col)),fscan.getLastRID());
	  }
	  fscan.close();
	  Minibase.SystemCatalog.createIndex(hf_name, IxTable, IxColumn);
      // print the output message
      System.out.println("Index created on column \""+ IxColumn + "\" of Table \"" + IxTable +"\"");

  } // public void execute()

} // class CreateIndex implements Plan
