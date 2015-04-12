package relop;

import global.SearchKey;
import global.RID;
import index.HashIndex;
import heap.HeapFile;
import java.util.ArrayList;
import java.util.Arrays;

public class HashJoin extends Iterator {
	private Iterator it_left;
	private Iterator it_right;
	private Integer left_col;
	private Integer right_col;
	private IndexScan leftscan;
	private IndexScan rightscan;
	private static int cnt=0;
	private Tuple nextTuple;
	HashTableDup dupHT;
	private ArrayList<Tuple> rTobejoin;
	private Tuple tmpLeft;
	
	public HashJoin(Iterator left, Iterator right, Integer which_left_col, Integer which_right_col) {
		it_left = left;
		it_right = right;
		left_col = which_left_col;
		right_col = which_right_col;
		setSchema(Schema.join(left.getSchema(), right.getSchema()));
		leftscan = buildHashIndex(it_left,left_col);
		rightscan = buildHashIndex(it_right,right_col);
		dupHT = new HashTableDup();
		buildHashTab();
	}

	public void explain(int depth) {
		indent(depth);
		System.out.println("This class implements hash join.");
	}

	public void restart() {
		it_left.restart();
		it_right.restart();
		leftscan.restart();
		rightscan.restart();
		dupHT = new HashTableDup();
		buildHashTab();
	}

	public boolean isOpen() {
		if(it_left!=null && it_right!=null && leftscan!=null && rightscan != null){
			return true;
		}
		return false;
	}

	public void close() {
		it_left.close();
		it_right.close();
		leftscan.close();
		rightscan.close();
	}

	public boolean hasNext() {
		Tuple[] hashedTuples;
		if(tmpLeft != null && rTobejoin.size()!=0){
			Tuple tmpjoin= Tuple.join(tmpLeft, rTobejoin.get(0), getSchema());
			rTobejoin.remove(0);
			nextTuple = tmpjoin;
			return true;
		}
		while(leftscan.hasNext()){
			tmpLeft = leftscan.getNext();
			Object lcolObj = tmpLeft.getField(left_col);
			SearchKey lkey = new SearchKey(lcolObj);
			hashedTuples = dupHT.getAll(lkey);
			if(hashedTuples == null){
				continue;
			}
			else{
				rTobejoin = new ArrayList<Tuple>(Arrays.asList(hashedTuples));
				Tuple tmpjoin= Tuple.join(tmpLeft, rTobejoin.get(0), getSchema());
				rTobejoin.remove(0);
				nextTuple=tmpjoin;
				return true;
			}
		}
		return false;
	}

	public Tuple getNext() {
		return nextTuple;
	}
	
	private IndexScan buildHashIndex(Iterator it, Integer colnum){
		if(it instanceof FileScan){
			FileScan tmpIt = (FileScan)it;
			HashIndex hashIndx = new HashIndex("HI"+(cnt++));
			while(tmpIt.hasNext()){
				Tuple tmpTuple = tmpIt.getNext();
				Object colObj = tmpTuple.getField(colnum);
				hashIndx.insertEntry(new SearchKey(colObj), tmpIt.getLastRID());
			}
			return new IndexScan(tmpIt.getSchema(),hashIndx,tmpIt.getFile());
		}
		else if(it instanceof KeyScan){
			KeyScan tmpIt = (KeyScan)it;
			return new IndexScan(tmpIt.getSchema(),tmpIt.getHashIndx(),tmpIt.getFile());
		}
		else if(it instanceof IndexScan){
			return (IndexScan)it;
		}
		else if(it instanceof HashJoin){
			HashJoin tmpIt = (HashJoin)it;
			HashIndex hashIndx = new HashIndex("HI"+(cnt++));
			HeapFile  hf = new HeapFile(null);
			while(tmpIt.hasNext()){
				Tuple tmpTuple = tmpIt.getNext();
				Object colObj = tmpTuple.getField(colnum);
				RID rid = hf.insertRecord(tmpTuple.getData());
				hashIndx.insertEntry(new SearchKey(colObj), rid);
			}	
			return new IndexScan(tmpIt.getSchema(),hashIndx,hf);
		}
		else{
			System.out.println("The type of scan is not supported.");
			return null;
		}
		
	}
	
	private void buildHashTab(){		
		while(rightscan.hasNext()){
				Tuple tmpRight = rightscan.getNext();
				Object rcolObj = tmpRight.getField(right_col);
				SearchKey rkey = new SearchKey(rcolObj);
				dupHT.add(rkey, tmpRight);
			}
		rightscan.restart();
	}

}
