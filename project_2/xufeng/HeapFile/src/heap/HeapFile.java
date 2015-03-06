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

public class HeapFile {
    String heapFileName;
    List<HFPage> hfp_list; // list of all pages. index is pageID
    int curDirPageIndex;
    
    public HeapFile () {

    }

    public HeapFile (String fileName) {
        // create heapfile via filename
        System.out.println("HeapFile: file " + fileName + " is created.\n");
        heapFileName = fileName;
        
        hfp_list = new ArrayList<HFPage>(); // list of all pages (directory and record pages)
        
        File f = new File(fileName);
        if (f.exists() && !f.isDirectory()) {
            // existing heap file found
            
        } else {
            // NO existing heap file, setup new file        
            createDirPage();
            createRecPage();
                        
            // write to Heap File
            write_heap();
        }
    }
    
    void write_heap() {
        // Write all pages to heap file
        try {
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(new FileOutputStream(heapFileName));
                
                for (int i=0; i<hfp_list.size(); i++) {
                    // for each heapfile page
                    byte [] this_data = hfp_list.get(i).getData();
                    output.write(this_data);
                }
            }
            finally {
                output.close();
            }
        }
        catch(FileNotFoundException ex){
            System.err.println("File not found.");
        }
        catch(IOException ex){
            System.err.println(ex);
        }
    }



    
    public boolean deleteRecord(RID rid) {
        // Deletes a record from the page, compacting the records space.
        //

        return true;
    }
    
    public Tuple getRecord(RID rid) {
        return null;

    }

    public RID insertRecord(byte[] record) throws InvalidUpdateException {
        // Inserts a new record into the page.
        
        // find a page to insert to
        PageId insertToPageId = locateInsertPageId(record.length, curDirPageIndex);

        // insert a record
        RID thisRID = hfp_list.get(insertToPageId.pid).insertRecord(record);

        return thisRID;
    }
    
    PageId locateInsertPageId(int rSize, int dirPageIndex) {
        // locate a suitable page to insert this record on this directory page

        PageId insertToPageId = new PageId();
        insertToPageId.pid = -1; // -1 = no page selected

        // Search this directory page
        HFPage thisPage = hfp_list.get(dirPageIndex);
        short slotCnt = thisPage.getSlotCount();
        System.out.println(">> locateInsertPageId: in directory page pid = " + thisPage.getCurPage().pid + ", " + slotCnt + " slot(s) found! \n");
        for (int sIndex = 0; sIndex < slotCnt; sIndex++) {
            // for each slot in this directory page
            RID thisRID = new RID();
            thisRID.pageno = thisPage.getCurPage();
            thisRID.slotno = sIndex;
            System.out.println(">> locateInsertPageId: pageno = " + thisRID.pageno + "; slotno = " + thisRID.slotno +"\n");            
            byte[] thisRecord = thisPage.selectRecord(thisRID);
            int pid = getPid(thisRecord);
            System.out.println(">> locateInsertPageId: pid = " + pid + "\n");            
            System.out.println(">> locateInsertPageId: freeBytes = " + hfp_list.get(pid).getFreeSpace() + "; contFreeBytes = " + hfp_list.get(pid).getContFreeSpace() + "; rSize = " + rSize + "\n");
            if (rSize <= hfp_list.get(pid).getFreeSpace()) {
                // this page can host this record
                if (rSize <= hfp_list.get(pid).getContFreeSpace()) {
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
        HeapScan scan = new openScan();
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
        HeapScan scan = new openScan();
        scan.hfp_list = hfp_list;
        return scan;
    }
    ////////////////////////////
    // record page operations
    ////////////////////////////

    PageId createRecPage() {
        // Create a record page
        HFPage recPage = new HFPage();   // create new record page

        PageId cuPageId = new PageId(hfp_list.size());
        recPage.setCurPage(cuPageId);    // set page ID

        hfp_list.add(recPage);           // add to master page list

        // add to directory page
        addToDirPage(recPage);

        return cuPageId;
    }

    ////////////////////////////
    // dirPage operations
    ////////////////////////////

    void createDirPage() {
        // Create a directory page
        HFPage dirPage = new HFPage();     // create new head directory page

        PageId cuPageId = new PageId(hfp_list.size());
        dirPage.setCurPage(cuPageId);      // set page ID
        curDirPageIndex = hfp_list.size(); // set to current page list

        hfp_list.add(dirPage);             // add to master page list

        dirPage.print();
    }

    void addToDirPage(HFPage recPage) {
        // add record page to directory page
        
        // see if the directory page has enough space
        
        // insert record page info into curDirPage
        byte [] pidByte = new byte[HFPage.INT_FIELD_SIZE];
        Convert.setIntValue(recPage.getCurPage().pid, 0, pidByte);
        System.out.println("addToDirPage: record pid = " + recPage.getCurPage().pid +"\n");

        hfp_list.get(curDirPageIndex).insertRecord(pidByte); // add pageId(int) to the record
    }

    ////////////////////////////
    // dirPage record operations
    ////////////////////////////

    int getPid(byte[] dirRecord) {
        // get page id
        return Convert.getIntValue(0, dirRecord);
    }
}
