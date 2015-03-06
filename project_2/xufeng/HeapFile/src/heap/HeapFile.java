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
    List<HFPage> hfp_list;
    
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
            PageId headPage = new PageId(); headPage.pid = -1;
            PageId noPage   = new PageId(); headPage.pid = 0;
            PageId cuPage   = new PageId();
                                    
            HFPage dirPage = new HFPage(); // create new head directory page
            hfp_list.add(dirPage);
            cuPage.pid = hfp_list.size();
            dirPage.setCurPage(cuPage);          // set current page ID
            dirPage.setPrevPage(headPage);       // previous page ID (head=-1)
            dirPage.setNextPage(noPage);         // next page ID
            dirPage.setSlotCount((short)0);      // slot_count
            dirPage.setType((short)0);           // type
            dirPage.setFreeSpace((short)0);      // free_space
            
            HFPage recPage = new HFPage(); // create new record page
            hfp_list.add(recPage);
            cuPage.pid = hfp_list.size();
            recPage.setCurPage(cuPage);          // set current page ID
            recPage.setPrevPage(noPage);         // previous page ID (pages are not linked)
            recPage.setNextPage(noPage);         // next page ID (pages are not linked)
            recPage.setSlotCount((short)0);      // slot_count
            recPage.setType((short)1);           // type
            recPage.setFreeSpace((short)0);      // free_space
                        
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
        
        // find a page
        
        // insert a record
        return null;
    }
    
    public boolean updateRecord(RID rid, heap.Tuple record) throws InvalidUpdateException {
        // Updates a record on the page.
        
        return false;
    }

    public int getRecCnt() {
        // Get number of records in the file
        return 1;
    }

    public HeapScan openScan() {
        return null;
    }
}
