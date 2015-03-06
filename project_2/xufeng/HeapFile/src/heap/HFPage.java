package heap;

import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.RID;
import global.PageId;
import global.Page;

import java.io.IOException;

import chainexception.ChainException;

            
public class HFPage extends Page {
    public static final int SHORT_FIELD_SIZE = 2;
    public static final int INT_FIELD_SIZE   = 4;

    public static final int THISID_OFFSET    = 0;                                   // this page ID
    public static final int PREVID_OFFSET    = INT_FIELD_SIZE;                      // previous page ID
    public static final int NEXTID_OFFSET    = INT_FIELD_SIZE*2;                    // next page ID
    public static final int SLOTCNT_OFFSET   = INT_FIELD_SIZE*2+SHORT_FIELD_SIZE;   // slot_count
    public static final int TYPE_OFFSET      = INT_FIELD_SIZE*2+SHORT_FIELD_SIZE*2; // type
    public static final int FREESPACE_OFFSET = INT_FIELD_SIZE*2+SHORT_FIELD_SIZE*3; // free_space
    public static final int TOTAL_OFFSET     = FREESPACE_OFFSET+SHORT_FIELD_SIZE;   // free_space
    
    public HFPage () {
  
    }

    public HFPage (Page page) {

    }

    public void deleteRecord(RID rid) {
        // Deletes a record from the page, compacting the records space.
    }

    public RID firstRecord() {
        // Gets the RID of the first record on the page, or null if none.
        return null;
    }
    
    PageId getCurPage() {
        // Gets the current page's id.
        return null;
    }
    
    short getFreeSpace() {
        // Gets the amount of free space (in bytes).
        return 1;
    }
    
    PageId getNextPage() {
        // Gets the next page's id.
        return null;
    }
    
    PageId getPrevPage() {
        // Gets the previous page's id.
        return null;
    }
    
    short getSlotCount() {
        // Gets the number of slots on the page.
        return 1;
    }
    
    short getSlotLength(int slotno) {
        // Gets the length of the record referenced by the given slot.
        return 1;
    }
    
    short getSlotOffset(int slotno) {
        // Gets the offset of the record referenced by the given slot.
                return 1;
    }
    
    short getType() {
        // Gets the arbitrary type of the page.
                return 1;
    }
    
    boolean hasNext(RID curRid) {
        // Returns true if the iteration has more elements.
                return true;
    }
    
    RID insertRecord(byte[] record) {
        // Inserts a new record into the page.
                return null;
    }
    
    RID nextRecord(RID curRid) {
        // Gets the next RID after the given one, or null if no more.
                return null;
    }
    
    void print() {
        // Prints the contents of a heap file page.
    }
    
    byte[] selectRecord(RID rid) {
        // Selects a record from the page.
                return null;
    }
    
    void setCurPage(PageId pageno) {
        // Sets the current page's id.
        setIntValue(pageno.pid,THISID_OFFSET);
    }
    
    void setNextPage(PageId pageno) {
        // Sets the next page's id.
        setIntValue(pageno.pid,NEXTID_OFFSET);
    }
    
    void setPrevPage(PageId pageno) {
        // Sets the previous page's id.
        setIntValue(pageno.pid,PREVID_OFFSET);
    }
    
    void setType(short type) {
        // Sets the arbitrary type of the page.
        // 0 = directory page; 1 = record page
        setShortValue(type,TYPE_OFFSET);
    }
    
    void setSlotCount(short slotCnt) {
        // Sets the total slot count of the page.
        setShortValue(slotCnt,SLOTCNT_OFFSET);
    }
    
    void setFreeSpace(short freeSpace) {
        // Sets the total free space of the page.
        setShortValue(freeSpace,FREESPACE_OFFSET);
    }   
     
    public void updateRecord(RID rid, heap.Tuple record) {
        // Updates a record on the page.
    }
}
