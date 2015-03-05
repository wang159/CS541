package heap;

import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.RID;
import global.PageId;
import global.Page;

import java.io.IOException;

import chainexception.ChainException;

public class HeapFile {
    void deleteRecord(RID rid) {
        // Deletes a record from the page, compacting the records space.
    }
    
    RID firstRecord() {
        // Gets the RID of the first record on the page, or null if none.
    }
    
    PageId getCurPage() {
        // Gets the current page's id.
    }
    
    short getFreeSpace() {
        // Gets the amount of free space (in bytes).
    }
    
    PageId getNextPage() {
        // Gets the next page's id.
    }
    
    PageId getPrevPage() {
        // Gets the previous page's id.
    }
    
    short getSlotCount() {
        // Gets the number of slots on the page.
    }
    
    short getSlotLength(int slotno) {
        // Gets the length of the record referenced by the given slot.
    }
    
    short getSlotOffset(int slotno) {
        // Gets the offset of the record referenced by the given slot.
    }
    
    short getType() {
        // Gets the arbitrary type of the page.
    }
    
    boolean hasNext(RID curRid) {
        // Returns true if the iteration has more elements.
    }
    
    RID insertRecord(byte[] record) {
        // Inserts a new record into the page.
    }
    
    RID nextRecord(RID curRid) {
        // Gets the next RID after the given one, or null if no more.
    }
    
    void print() {
        // Prints the contents of a heap file page.
    }
    
    byte[] selectRecord(RID rid) {
        // Selects a record from the page.
    }
    
    void setCurPage(PageId pageno) {
        // Sets the current page's id.
    }
    
    void setNextPage(PageId pageno) {
        // Sets the next page's id.
    }
    
    void setPrevPage(PageId pageno) {
        // Sets the previous page's id.
    }
    
    void setType(short type) {
        // Sets the arbitrary type of the page.
    }
    
    void updateRecord(RID rid, heap.Tuple record) {
        // Updates a record on the page.
    }
}
