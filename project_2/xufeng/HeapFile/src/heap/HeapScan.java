package heap;

import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.RID;
import global.PageId;
import global.Page;
import diskmgr.DiskMgr;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import chainexception.ChainException;

public class HeapScan {
	// in HeapScan, where all record files are scanned, we will do things a little bit differently
	// we will scan through all the list of pages, and only go through the record pages.
	
	HeapFile hf;
    DiskMgr diskMgr;  // disk manager
    RID curRid;       // current record
    PageId curPageId; // current record page ID, (should be the same as one in curRid, but sometimes curRid may be null)

    public HeapScan() {

    }

    public HeapScan(HeapFile hf_in) {
		hf = hf_in;
		//diskMgr=hf.diskMgr;

	    curPageId = Minibase.DiskManager.get_file_entry(hf_in.heapFileName);  // get the first page ID of the heap file
		System.out.println("HeapScan: loading from file '"+hf_in.heapFileName+"'");
		System.out.println("HeapScan: start with Page "+curPageId.pid+" from file"+hf_in.heapFileName);
    }

    protected void finalize() 
        throws Throwable {

    }

    public void close() {
        //
    
    }

    public boolean hasNext() {
        // See if next record exists. 
		Tuple nextTuple = getNext(curRid); // get next record

		if (nextTuple == null) {
			// no more record
			return false;
		} else {
			return true;
		}
    }

    public Tuple getNext(RID rid) {
        // Get next record. Return NULL if no more record can be found
        Tuple thisTuple = new Tuple();
		RID nextRid = null;

		// iterate until a record is found, or all records are scanned
		if (readPage(curPageId.pid).getType() == 1) {
			// only read if it is a record page, not a directory page
			if (readPage(curPageId.pid).hasNext(curRid)) {
				// has next record
   	   			nextRid = readPage(curPageId.pid).nextRecord(curRid); // get the RID of next record
			}
		}
		System.out.println("HeapScan.getNext: on page "+curPageId.pid);
   	    while (nextRid == null) {
			System.out.println("HeapScan.getNext: on page "+curPageId.pid);
  	        // no more record on this page, move to next page
			// iterate until a record is found, or all records are scanned
			if (readPage(curPageId.pid).getNextPage() != null && readPage(curPageId.pid).getNextPage().pid >=0) {
				// next page exists
				curPageId = readPage(curPageId.pid).getNextPage(); // set current page ID to next

				if (readPage(curPageId.pid).getType() == 1) {
					// only read if it is a record page, not a directory page
					nextRid = readPage(curPageId.pid).firstRecord(); // get the RID of first record
				}
			} else {
				// no next page
				break;
			}
	    }

		curRid = nextRid; // update current RID
		
		// get next tuple
		if (curRid == null) {
			thisTuple = null;
		} else {
			thisTuple.tuple = hf.readPage(curPageId.pid).selectRecord(curRid);
			System.out.println(">> HeapScan: next record is found on page = "+curPageId.pid+"; slot = " +curRid.slotno);

			rid.pageno = curRid.pageno;
			rid.slotno = curRid.slotno;
		}


        return thisTuple;
    }

    ////////////////////////////
    // diskMgr operations
    ////////////////////////////
	HFPage readPage(int thisPageIdNum) {
		// utility function for easier diskMgr read_page
		PageId thisPageId = new PageId(thisPageIdNum);
		HFPage thisPage = new HFPage();
		Minibase.DiskManager.read_page(thisPageId, thisPage);

		return thisPage;
	}
}
