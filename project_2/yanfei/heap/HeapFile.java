package heap;

import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.*;
import global.RID;
import heap.HeapFile;
import heap.HeapScan;
import heap.Tuple;

import java.io.IOException;
import java.util.*;
import chainexception.ChainException;

public class HeapFile implements GlobalConst
{
	private HFPage hfpage;
	private int Rec_Cnt;
	public PageId header_pid;
	public String filename;
//	private LinkedList hfpagelist = new LinkedList();
	
	
	public HeapFile(String name){

		if(name != null){
		//file does not exist
			header_pid = Minibase.DiskManager.get_file_entry(name);
			if(header_pid != null){

			}
			else {
				filename = name;
				hfpage = new HFPage();
				header_pid = Minibase.BufferManager.newPage(hfpage, 1);
				Minibase.DiskManager.add_file_entry(name, header_pid);
				Rec_Cnt = 0;
				Minibase.BufferManager.unpinPage(header_pid, true);
				hfpage.setCurPage(header_pid);
				HFPage hfpagetmp = new HFPage();
				PageId pid = Minibase.BufferManager.newPage(hfpagetmp,1);
				hfpage.setNextPage(pid);
				Minibase.BufferManager.unpinPage(pid, true);
// System.out.println("header_pid:"+header_pid.pid);
// System.out.println("curr_pid:"+hfpage.getCurPage().pid);
// System.out.println("next_pid:"+hfpage.getNextPage().pid);
			}
		} else { 
		//file exist
		}
	}

	int i = 0;
	int j = 0;

	public RID insertRecord(byte[] record) throws 
	ChainException, IOException {
		
//		Minibase.BufferManager.pinPage(header_pid, hfpage, false);
		if(record.length > MAX_TUPSIZE) {
			throw new SpaceNotAvailableException("Space not available");
		}
		RID rid = new RID();
		HFPage hfpageptr = new HFPage();
// System.out.println("@@@@@@@@@@@@ -- 00");
		PageId pid = new PageId();
		pid = hfpage.getNextPage();
//System.out.println("header_pid:"+header_pid.pid);
//System.out.println("next_pid:"+pid.pid);
//System.out.println("curr_pid:"+hfpage.getCurPage().pid);
// System.out.println("@@@@@@@@@@@@ -- 0");
		

		while(pid.pid != -1){
			Minibase.BufferManager.pinPage(pid, hfpageptr, false);
			hfpageptr.setCurPage(pid);
			if((record.length+4) <= hfpageptr.getFreeSpace()){
				rid = hfpageptr.insertRecord(record);
 // System.out.println("1-"+(i++)+"	freespace:"+hfpageptr.getFreeSpace()+"	recordlength:"+record.length+"	Rec_Cnt:"+Rec_Cnt+"	PageId:"+pid.pid+"	rid.pageno.pid:"+rid.pageno.pid);
				Rec_Cnt++;
				Minibase.BufferManager.unpinPage(pid, true);
				return rid;
			}
			Minibase.BufferManager.unpinPage(pid, false);
			pid = hfpageptr.getNextPage();
		}
// System.out.println("2-"+(j++)+"	freespace:"+hfpageptr.getFreeSpace()+"	recordlength:"+record.length+"	Rec_Cnt:"+Rec_Cnt);
		HFPage hfpagenew = new HFPage();
		pid = Minibase.BufferManager.newPage(hfpagenew, 1);
		hfpagenew.setCurPage(pid);

//		Minibase.BufferManager.pinPage(pid, hfpagenew, true);
		rid = hfpagenew.insertRecord(record);
		Rec_Cnt++;
		hfpageptr.setNextPage(pid);
		Minibase.BufferManager.unpinPage(pid, true);
		
		return rid;
	}
	
	public Tuple getRecord(RID rid){
		HFPage tmp_hf = new HFPage();
		Minibase.BufferManager.pinPage(rid.pageno, tmp_hf, false);
		int length = tmp_hf.getSlotLength(rid.slotno);
		Tuple tuple = new Tuple(tmp_hf.selectRecord(rid), 0, length);
		Minibase.BufferManager.unpinPage(rid.pageno, true);
		return tuple;
	}
	
	public boolean updateRecord(RID rid, Tuple newRecord) throws 
	ChainException, InvalidUpdateException {
		HFPage tmp_hf_1 = new HFPage();
		Minibase.BufferManager.pinPage(rid.pageno, tmp_hf_1, false);
// System.out.println("rid.getLength:"+tmp_hf.getSlotLength(rid.slotno)+"	newRecordLength:"+newRecord.getLength());
		try {
			if(tmp_hf_1.getSlotLength(rid.slotno) != newRecord.getLength()) throw new InvalidUpdateException();
			tmp_hf_1.updateRecord(rid, newRecord);
		}
		catch (InvalidUpdateException e){
			Minibase.BufferManager.unpinPage(rid.pageno, true);
			return false;
		}
		
		Minibase.BufferManager.unpinPage(rid.pageno, true);
		return true;
	}
	
	public boolean deleteRecord(RID rid){
		HFPage tmp_hf = new HFPage();
// System.out.println("1 -- deleteRecord:rid.pageno.pid:"+rid.pageno.pid+"	rid.slotno:"+rid.slotno);
		Minibase.BufferManager.pinPage(rid.pageno, tmp_hf, false);
		tmp_hf.deleteRecord(rid);
		Rec_Cnt--;
		Minibase.BufferManager.unpinPage(rid.pageno, true);
		return true;
	}
	
	public int getRecCnt(){
		return Rec_Cnt;
	}
	
	public HeapScan openScan(){
		HeapScan hscan = new HeapScan();
// System.out.println("openScan:header_pid:"+header_pid.pid);
		hscan.hs_init(header_pid);
		return hscan;
	}

}