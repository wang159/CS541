package heap;

import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.RID;
import global.*;
import heap.HeapFile;
import heap.HeapScan;
import heap.Tuple;

import java.io.IOException;

import chainexception.ChainException;


public class HeapScan
{
	public PageId firstpage;
	
	private HeapFile hfile;
	private RID ridptr;
	
	private HFPage curr_hf;
	private HFPage next_hf;
	private PageId curr_pid;
	private PageId next_pid;
	
	
	protected HeapScan(HeapFile hf){
	
		hfile = hf;
		hs_init(hf.header_pid);
	}

	public void hs_init(PageId pid){
// System.out.println("hs_init:pid:"+pid.pid);
		curr_hf = new HFPage();
		Minibase.BufferManager.pinPage(pid, curr_hf, false);
		next_pid = curr_hf.getNextPage();
		curr_pid = next_pid;
// System.out.println("curr_pid:"+curr_pid.pid);
		Minibase.BufferManager.unpinPage(pid, false);
		Minibase.BufferManager.pinPage(curr_pid, curr_hf, false);
		ridptr = curr_hf.firstRecord();
// System.out.println("rid_pid:"+ridptr.pageno.pid);

	}
	
	//added
	public HeapScan(){
	}
	
//	protected void finalize() throws Throwable
	
	public void close() throws ChainException{
	}
	
	public boolean hasNext(){
		return true;
	}
	
	int i=0;
	public Tuple getNext(RID rid){
		Tuple tuple;
		byte bytearray[];
		HFPage hfpagetmp = new HFPage();
		if(ridptr == null){
// System.out.println("%%%%%%%%%%%%%%%");
			Minibase.BufferManager.unpinPage(curr_pid, false);
			return null;
		}
		
		rid.slotno = ridptr.slotno;
		rid.pageno = ridptr.pageno;
// System.out.println("0 -- getNext:rid.pageno.pid:"+rid.pageno.pid);
		int length = curr_hf.getSlotLength(ridptr.slotno);
// System.out.println((i++)+"	getNext:curr_pid:"+curr_pid+"	getNext:length:"+length+"	slotno:"+ridptr.slotno);
		tuple = new Tuple(curr_hf.selectRecord(ridptr), 0, length);
		ridptr = curr_hf.nextRecord(ridptr);
		if(ridptr == null){

			PageId hftmp = curr_hf.getNextPage();
// System.out.println("hftmp:"+hftmp.pid);
			if(hftmp.pid != -1){
				Minibase.BufferManager.unpinPage(curr_pid, false);
	
				Minibase.BufferManager.pinPage(hftmp, curr_hf, false);
				curr_pid = hftmp;

				ridptr = curr_hf.firstRecord();
			}
		}
		
		
		return tuple;
		
	}
	
}
	
	
