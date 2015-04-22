package query;

import java.util.ArrayList;
import java.util.Map.Entry;
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
	private Integer[] recCount;
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
	  recCount = new Integer[tabs.length];
	  for(int i = 0; i < tabs.length; i = i+1){
		  Schema tmpsc = Minibase.SystemCatalog.getSchema(tabs[i]);
		  HeapFile tmphf = new HeapFile(tabs[i]);
		  its[i] = new FileScan(tmpsc,tmphf);
		  recCount[i] = tmphf.getRecCnt();
	  }

  } // public Select(AST_Select tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  
public void execute() {
	//Iterator it_ex = null;
	//for(int pi = 0; pi < preds.length; pi = pi+1){
		//for(int pj = 0; pj < preds[pi].length; pj = pj+1){
			//System.out.println(pi +"," + pj +" : " + preds[pi][pj].toString());
		//}
	//}
    ArrayList<String> toj =new ArrayList<String>();
    java.util.Iterator<Entry<String, Integer>> itr;
	@SuppressWarnings("unchecked")
	ArrayList<Predicate[]>[] PushUnderJoin = new ArrayList[tabs.length];
    
	for(int i=0; i < tabs.length; i = i+1){
		PushUnderJoin[i] = new ArrayList<Predicate[]>();
		for(int pi = 0; pi < preds.length; pi = pi+1){	
			boolean tmptrue = true;
			for(int pj = 0; pj < preds[pi].length; pj = pj+1){
				tmptrue &= preds[pi][pj].validate(its[i].getSchema());
				if(!toj.contains(preds[pi][pj].toString())){
					toj.add(preds[pi][pj].toString());
				}
			}
			if(tmptrue){
			   PushUnderJoin[i].add(preds[pi]);
			}
		}

	}
	

	for(int i = 0; i < tabs.length; i = i+1){
		if(PushUnderJoin[i].size()>0){
			//System.out.println("i= "+tabs[i]);
			for(int j = 0; j < PushUnderJoin[i].size(); j = j+1){
				its[i] = new Selection(its[i],PushUnderJoin[i].get(j));
				for(int k = 0; k < PushUnderJoin[i].get(j).length; k = k+1){
					toj.remove(PushUnderJoin[i].get(j)[k].toString());
				}
			}
		}
		//its[i].execute();
	}
	
	//for(int pi = 0; pi < predlist.size(); pi = pi+1){
		//for(int pj = 0; pj < predlist.get(pi).size(); pj = pj+1){
			//System.out.println(pi+","+pj+": "+predlist.get(pi).get(pj).toString());
		//}
	//}
	// join
	Iterator it_ex = its[0];
	Schema it_sc = it_ex.getSchema();
	
	//for(int tj = 0; tj < toj.size(); tj = tj+1){
		//System.out.println(toj.get(tj));
	//}

    if(tabs.length>1){
    	for(int i = 1; i < tabs.length; i = i+1){
    		
    		if(recCount[i-1]<recCount[i]){
    			it_sc = Schema.join(it_sc, its[i].getSchema());
    		}
    		else{
    			it_sc = Schema.join(its[i].getSchema(),it_sc);
    		}
			for(int pi = 0; pi < preds.length; pi = pi+1){
				for(int pj = 0; pj < preds[pi].length; pj = pj+1){
					if(preds[pi][pj].validate(it_sc)){
						for(int tj = 0; tj < toj.size(); tj = tj+1){
							if(toj.get(tj).equals(preds[pi][pj].toString())){								
									if(recCount[i-1]<recCount[i]){
								        it_ex = new SimpleJoin(it_ex,its[i],preds[pi][pj]);
								        toj.remove(tj);
			    						break;
									}
									else{
										it_ex = new SimpleJoin(its[i],it_ex,preds[pi][pj]);
										toj.remove(tj);
			    						break;
									}
									
							}		
						}
					}
				}

			}
    		
			//for(int pi = 0; pi < predlist.size(); pi = pi+1){
				//for(int pj = 0; pj < predlist.get(pi).size(); pj = pj+1){
					//if(predlist.get(pi).get(pj).validate(it_sc)){
					  //  it_ex = new SimpleJoin(it_ex,its[i],predlist.get(0).get(0));
					   // break;
					//}
				//}
				//break;
			//}
    	}
    }
    /*
 	 //Select
 	for(int i = 0; i<preds.length; i = i+1){
 		 it_ex = new Selection(it_ex, preds[i]);
 	}	 
 	*/
    
 	 // Projection
     Integer[] colnum = new Integer[cols.length];
 	 if(cols.length>0){
 		for (int i = 0; i < cols.length; i = i+1) {
			colnum[i] = it_sc.fieldNumber(cols[i]);
		}
 		it_ex = new Projection(it_ex,colnum);
 	 }
 	
	 System.out.println(it_ex.execute()+ " rows selected.");
	 it_ex.close();
  } // public void execute()

} // class Select implements Plan
