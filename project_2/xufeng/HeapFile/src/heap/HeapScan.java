package heap;

import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.RID;
import global.PageId;
import global.Page;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import chainexception.ChainException;

public class HeapScan {
	// in HeapScan, where all record files are scanned, we will do things a little bit differently
	// we will scan through all the list of pages, and only go through the record pages.

    List<HFPage> hfp_list; // list of all pages. index is pageID
    RID curRid;
    int curIndex; // current record page index

    public HeapScan() {

    }

    public HeapScan(HeapFile hf) {
        hfp_list = hf.hfp_list; // pass input hfp_list
        curIndex = 0;           // start with the first (directory) page
    }

    protected void finalize() 
        throws Throwable {

    }

    public void close() {
        //
    
    }

    public boolean hasNext() {
        // See if next record exists. 
		RID saveCurRid = curRid;     // save current RID
		int saveCurIndex = curIndex; // save current index

		Tuple nextTuple = getNext(curRid); // get next record

		if (nextTuple == null) {
			// no more record
			return false;
		} else {
			return true;
		}
    }

    public Tuple getNext(RID rid) {
        // Get next record. Return NULL is no more record can be found
        Tuple thisTuple = new Tuple();
		RID nextRid = null;

		// iterate until a record is found, or all records are scanned

		if (curIndex == 0) {
			// first record
			curIndex = 1; // set to first record page (should always exist)
			nextRid = hfp_list.get(curIndex).firstRecord(); // get the RID of first record 
		} else {
			// non-first record
			if (hfp_list.get(curIndex).getType() == 1) {
				// only read if it is a record page, not a directory page
    	   		nextRid = hfp_list.get(curIndex).nextRecord(curRid); // get the RID of next record
			}
		}

   	    while (nextRid == null) {
  	        // no more record on this page, move to next page
			// iterate until a record is found, or all records are scanned
			curIndex++;
			if (curIndex < hfp_list.size() && hfp_list.get(curIndex).getType() == 1) {
				// only read if it is a record page, not a directory page
				nextRid = hfp_list.get(curIndex).firstRecord(); // get the RID of first record
			} else {
				// all pages are traversed
				break;
			}
	    }

		curRid = nextRid; // update current RID
		
		// get next tuple
		if (curRid == null) {
			thisTuple = null;
		} else {
			thisTuple.tuple = hfp_list.get(curIndex).selectRecord(curRid);
		}

        return thisTuple;
    }
}
