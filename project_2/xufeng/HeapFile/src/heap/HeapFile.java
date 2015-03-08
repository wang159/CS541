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

			diskMgr.add_file_entry(heapFileName, new PageId(0));  // register this heap file to header page

            firstPageId=createDirPage(); // create directory page; this is first page
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

		//hfp_list.get(rid.pageno.pid).deleteRecord(rid);

        return true;
    }
    
    public Tuple getRecord(RID rid) {
        return null;

    }

    public RID insertRecord(byte[] record) throws InvalidUpdateException {
        // Inserts a new record into the page.
        
        // find a page to insert to
        PageId insertToPageId = locateInsertPageId(record.length, HFPage.SLOT_SIZE, curDirPageId);

        // insert a record
        //RID thisRID = hfp_list.get(insertToPageId.pid).insertRecord(record);
		HFPage thisDirPage = new HFPage();
		diskMgr.read_page(insertToPageId, thisDirPage);  // read directory page from disk
		RID thisRID = thisDirPage.insertRecord(record);  // add pageId(int) to the record
		diskMgr.write_page(insertToPageId, thisDirPage); // write directory page to disk

        return thisRID;
    }
    
    PageId locateInsertPageId(int rSize, int sSize, PageId dirPageId) {
        // locate a suitable page to insert this record on this directory page
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
        System.out.println(">> locateInsertPageId: in directory page pid = " + thisPage.getCurPage().pid + ", " + slotCnt + " slot(s) found!");

        for (int sIndex = 0; sIndex < slotCnt; sIndex++) {
            // for each slot in this directory page
            RID thisRID = new RID();
            thisRID.pageno = thisPage.getCurPage();
            thisRID.slotno = sIndex;
            System.out.println(">> locateInsertPageId: pageno = " + thisRID.pageno + "; slotno = " + thisRID.slotno +"\n");            
            byte[] thisRecord = thisPage.selectRecord(thisRID);
            int pid = getPid(thisRecord);
            System.out.println(">> locateInsertPageId: pid = " + pid + "\n");            
            System.out.println(">> locateInsertPageId: freeBytes = " + readPage(pid).getFreeSpace() + "; contFreeBytes = " + readPage(pid).getContFreeSpace() + "; totalSize = " + totalSize + "\n");
            if (totalSize <= readPage(pid).getFreeSpace()) {
                // this page can host this record
                if (totalSize <= readPage(pid).getContFreeSpace()) {
                    // and the continous free block is large enough
                    insertToPageId.pid = pid;
                } else {
                    // but the continous free block is not large enough
                }
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
        
        return false;
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
        addToDirPage(recPage);

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

		// update previous page
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
        
        // see if the directory page has enough space
        
        // insert record page info into curDirPage
        byte [] pidByte = new byte[HFPage.INT_FIELD_SIZE];
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
