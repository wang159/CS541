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

public class HeapFile {
    DiskMgr diskMgr = Minibase.DiskManager; // disk manager

	String heapFileName; // serves as the unique identifier of this heap file
	PageId curDirPageId; // current directory page ID
	PageId latestPageId; // last created page ID
    PageId firstPageId;  // first created page ID

    public HeapFile () {

    }

    public HeapFile (String fileName) {
        // create heapfile via filename
		heapFileName = fileName;
        
        if (diskMgr.get_file_entry(fileName) == null) {
            // NO existing heap file, setup new file       
			System.out.println("Creating new heap file '" + fileName + "'..."); 
			latestPageId = null;

            firstPageId=createDirPage(); // create directory page; this is first page
			diskMgr.add_file_entry(heapFileName, firstPageId);  // register this heap file to header page
            createRecPage();             // create record page
        } else {
            // existing heap file found
			System.out.println("Existing heap file found. Loading ...");

			curDirPageId = diskMgr.get_file_entry(fileName);
        }
    }

    public boolean deleteRecord(RID rid) {
        // Deletes a record from the page.
		// >> parameters
		// rid: the ID of the record to be deleted.
		// >> details
        // In this implementation of the Heap File, deleting a record does not compact the space. 
		// Instead, the total free space is kept inside the page, and unless all available pages are exhausted
		// no page compacting operation is executed. Therefore, the delete record operation becomes very simple by
		// setting the slot record offset to -1.

		HFPage thisDirPage = new HFPage();
		System.out.println("deleteRecord: page = "+rid.pageno+";slot = "+rid.slotno);

		Minibase.BufferManager.pinPage(rid.pageno, thisDirPage, false); // pin the first page
		diskMgr.read_page(rid.pageno, thisDirPage);  // read directory page from disk
		thisDirPage.deleteRecord(rid);  			 // add pageId(int) to the record
		diskMgr.write_page(rid.pageno, thisDirPage); // write directory page to disk
		Minibase.BufferManager.unpinPage(rid.pageno, true); // unpin previous first page

		return true;
    }
    
    public Tuple getRecord(RID rid) {
		Tuple thisTuple = new Tuple();
		thisTuple.tuple = readPage(rid.pageno.pid).selectRecord(rid);
        return thisTuple;
    }

    public RID insertRecord(byte[] record) throws SpaceNotAvailableException {
        // Inserts a new record into the page.
        if (record.length > GlobalConst.MAX_TUPSIZE) {
			throw new SpaceNotAvailableException("Record size exceeds MAX_TUPLE_SIZE");
		}

        // find a page to insert to
        PageId insertToPageId = locateInsertPageId(record.length, HFPage.SLOT_SIZE, curDirPageId);

        // insert a record
		HFPage thisPage = new HFPage();

		Minibase.BufferManager.pinPage(insertToPageId, thisPage, false); // pin page
		diskMgr.read_page(insertToPageId, thisPage);  // read directory page from disk
		RID thisRID = thisPage.insertRecord(record);  // add pageId(int) to the record
		diskMgr.write_page(insertToPageId, thisPage); // write directory page to disk
		Minibase.BufferManager.unpinPage(insertToPageId, true); // unpin page
		
        return thisRID;
    }
    
    PageId locateInsertPageId(int rSize, int sSize, PageId dirPageId) {
        // locate a suitable page to insert this record on this directory page
		// If no page in current directory is large enough, a new record page will be created
		// If a new directory page is needed, the new record page creation will take care of it
		// >> Parameters:
		// rSize = record size; 
		// sSize = slot info size; 
		// dirPageIndex = index of current directory page

        PageId insertToPageId = new PageId();
        insertToPageId.pid = -1;     // -1 = no page selected
		int totalSize = rSize+sSize; // free bytes needed in total

        // Search this directory page
        HFPage thisPage = readPage(dirPageId.pid);  // read directory page from disk
		short slotCnt = thisPage.getSlotCount();
        //System.out.println(">> locateInsertPageId: in directory page pid = " + thisPage.getCurPage().pid + ", " + slotCnt + " slot(s) found!");

        for (int sIndex = (slotCnt-1); sIndex >= 0; sIndex--) {
            // for each slot in this directory page
            RID thisRID = new RID();
            thisRID.pageno = thisPage.getCurPage();
            thisRID.slotno = sIndex;
            //System.out.println(">> locateInsertPageId: pageno = " + thisRID.pageno + "; slotno = " + thisRID.slotno +"\n");            
            byte[] thisRecord = thisPage.selectRecord(thisRID);
            int pid = getPid(thisRecord);
            //System.out.println(">> locateInsertPageId: pid = " + pid + "\n");            
            //System.out.println(">> locateInsertPageId: freeBytes = " + readPage(pid).getFreeSpace() + "; contFreeBytes = " + readPage(pid).getContFreeSpace() + "; totalSize = " + totalSize + "\n");
            if (totalSize <= readPage(pid).getFreeSpace()) {
                // this page can host this record
                //if (totalSize <= readPage(pid).getContFreeSpace()) {
                    // and the continous free block is large enough
                    insertToPageId.pid = pid;
					System.out.println(">> locateInsertPageId: pageno = " + thisRID.pageno + "; slotno = " + thisRID.slotno +"\n");
					break; // breakaway since the page has been found
                //} else {
                    // but the continous free block is not large enough
                //}
            }
        }

        // If no suitable page found, create a new page
        if (insertToPageId.pid == -1 ) {
            // create a new page
            insertToPageId = createRecPage();
        }
 
        return insertToPageId;
    }

    public boolean updateRecord(RID rid, heap.Tuple record) throws InvalidUpdateException {
        // Updates a record on the page.
        
		// get the current record tuple
		Tuple curTuple = getRecord(rid);
		System.out.println("updating record ..");
		// if exists and of the same length, update it
		if (curTuple != null) {
			// record exists
			System.out.println("updating record .. length = "+ curTuple.getLength()+" with "+record.getLength());
			if (curTuple.getLength() == record.getLength()) {
				// the two records are of same length
				HFPage thisDirPage = new HFPage();

				Minibase.BufferManager.pinPage(rid.pageno, thisDirPage, false); // pin page				
				diskMgr.read_page(rid.pageno, thisDirPage);  // read directory page from disk
				thisDirPage.updateRecord(rid, record);       // add pageId(int) to the record
				diskMgr.write_page(rid.pageno, thisDirPage); // write directory page to disk
				Minibase.BufferManager.unpinPage(rid.pageno, true); // unpin page				
			} else {
				throw new InvalidUpdateException();
			}
		}
		
        return true;
    }

    public int getRecCnt() {
        // Get number of records in the file
        HeapScan scan = openScan();

        RID rid = new RID(); // dummy record ID
		int recCnt = 0; // record count
        Tuple tuple = new Tuple(); // dummy tuple

        boolean done = false;
	    while (!done) { 
			tuple = scan.getNext(rid);
			if (tuple == null) {
				done = true;
				break;
			}
            recCnt++;
        }

        return recCnt;
    }

    public HeapScan openScan() {
        // Open a HeapScan protocol
        HeapScan scan = new HeapScan(this);

        return scan;
    }

    ////////////////////////////
    // record page operations
    ////////////////////////////

    PageId createRecPage() {
		PageId recPageId= diskMgr.allocate_page();  // create new head directory page

        HFPage recPage = new HFPage();       // new heap directory page
        recPage.setCurPage(recPageId);       // set page ID
		recPage.setType((short)1);           // set directory page type (0)
		recPage.setPrevPage(new PageId(-1)); // initialize previous page ID
		recPage.setNextPage(new PageId(-1)); // initialize next page ID

		// update previous page
		if (latestPageId != null) {
			recPage.setPrevPage(latestPageId); // set previous page ID

			HFPage thisPage = new HFPage();
			diskMgr.read_page(latestPageId, thisPage);
			thisPage.setNextPage(recPageId);
			diskMgr.write_page(latestPageId, thisPage);
		}

		diskMgr.write_page(recPageId, recPage); // write to disk
		latestPageId = recPageId;			    // update latest page ID

        // add to directory page
        addToDirPage(recPage); // if directory page is full, a new directory will be created here

        return recPageId;
    }

    ////////////////////////////
    // dirPage operations
    ////////////////////////////

    PageId createDirPage() {
        // Create a directory page
		PageId dirPageId= diskMgr.allocate_page();  // create new head directory page

        HFPage dirPage = new HFPage();          // new heap directory page
        dirPage.setCurPage(dirPageId);          // set page ID
		dirPage.setType((short)0);              // set directory page type (0)
		dirPage.setPrevPage(new PageId(-1));    // initialize previous page ID
		dirPage.setNextPage(new PageId(-1));    // initialize next page ID
        curDirPageId = dirPageId;               // set to current page list

		// update previous and next page
		if (latestPageId != null) {
			dirPage.setPrevPage(latestPageId);

			HFPage thisPage = new HFPage();
			diskMgr.read_page(latestPageId, thisPage);
			thisPage.setNextPage(dirPageId);
			diskMgr.write_page(latestPageId, thisPage);
		}

		diskMgr.write_page(dirPageId, dirPage); // write to disk
		latestPageId = dirPageId;			    // update latest page ID

		return dirPageId;
    }

    void addToDirPage(HFPage recPage) {
        // add record page to directory page
        byte [] pidByte = new byte[HFPage.INT_FIELD_SIZE];
        
        // see if the directory page has enough space
        if ((HFPage.SLOT_SIZE+pidByte.length) > readPage(curDirPageId.pid).getFreeSpace()) {
        	// if the directory is full  
			System.out.println(">> New directory is created.");
			curDirPageId = createDirPage();
		}
     
        // insert record page info into curDirPage
        Convert.setIntValue(recPage.getCurPage().pid, 0, pidByte);
        System.out.println("addToDirPage: record pid = " + recPage.getCurPage().pid +"\n");

		HFPage thisDirPage = new HFPage();
		diskMgr.read_page(curDirPageId, thisDirPage);  // read directory page from disk
		thisDirPage.insertRecord(pidByte);             // add pageId(int) to the record
		diskMgr.write_page(curDirPageId, thisDirPage); // write directory page to disk
    }

    ////////////////////////////
    // dirPage record operations
    ////////////////////////////

    int getPid(byte[] dirRecord) {
        // get page id
        return Convert.getIntValue(0, dirRecord);
    }

    ////////////////////////////
    // diskMgr operations
    ////////////////////////////
	HFPage readPage(int thisPageIdNum) {
		// utility function for easier diskMgr read_page
		PageId thisPageId = new PageId(thisPageIdNum);
		HFPage thisPage = new HFPage();
		diskMgr.read_page(thisPageId, thisPage);

		return thisPage;
	}
}
