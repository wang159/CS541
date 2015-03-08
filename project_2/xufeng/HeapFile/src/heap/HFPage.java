package heap;

import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.RID;
import global.PageId;
import global.Page;
import diskmgr.DiskMgr;

import java.util.Arrays;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

import chainexception.ChainException;

            
public class HFPage extends Page {
    public static final int SHORT_FIELD_SIZE = 2;
    public static final int INT_FIELD_SIZE   = 4;
    public static final int SLOT_SIZE = SHORT_FIELD_SIZE*2; // slot index size (offset, length)

    public static final int THISID_OFFSET    = 0;                                   // this page ID
    public static final int PREVID_OFFSET    = THISID_OFFSET    + INT_FIELD_SIZE;   // previous page ID
    public static final int NEXTID_OFFSET    = PREVID_OFFSET    + INT_FIELD_SIZE;   // next page ID
    public static final int SLOTCNT_OFFSET   = NEXTID_OFFSET    + INT_FIELD_SIZE;   // slot_count
    public static final int TYPE_OFFSET      = SLOTCNT_OFFSET   + SHORT_FIELD_SIZE; // type
    public static final int FREESPACE_OFFSET = TYPE_OFFSET      + SHORT_FIELD_SIZE; // freespace
    public static final int TOTAL_OFFSET     = FREESPACE_OFFSET + SHORT_FIELD_SIZE; // slot begin
    
    public HFPage () {
        // initialization
        byte[] zeroBytes = getData();
        Arrays.fill( zeroBytes, (byte) 0 );
        setData(zeroBytes);

        // set freeSpace (short) slot
        setShortValue((short)(PAGE_SIZE-TOTAL_OFFSET),FREESPACE_OFFSET);
    }

    public HFPage (Page page) {
		// set this page to another
		setPage(page);
    }

    
    ////////////////////////////
    // Slot operations
    ////////////////////////////

    short getSlotLength(int slotno) {
        // Gets the length of the record referenced by the given slot.
        short thisRecordLength;

        if (slotno == -1) {
            // no slot exists
            thisRecordLength = 0;
        } else {
            // there are existing slots
            short thisSlotPOS = (short)(TOTAL_OFFSET + slotno*SLOT_SIZE); // this slot head position
            thisRecordLength = getShortValue(thisSlotPOS + SHORT_FIELD_SIZE);
        }
        
        return thisRecordLength;
    }
    
    short getSlotOffset(int slotno) {
        // Gets the offset of the record referenced by the given slot.
        short thisRecordOffset;

        if (slotno == -1) {
            // no slot exists
            thisRecordOffset = PAGE_SIZE;
        } else {
            // there are existing slots
            short thisSlotPOS = (short)(TOTAL_OFFSET + slotno*SLOT_SIZE); // this slot head position
            thisRecordOffset = getShortValue(thisSlotPOS);
        }
        
        return thisRecordOffset;
    }

    ////////////////////////////
    // Record operations
    ////////////////////////////

    RID insertRecord(byte[] record) {
        // Inserts a new record into the page.

        // locate the starting point of this new record
        short newSlotPOS = (short)(TOTAL_OFFSET + getSlotCount()*SLOT_SIZE); // new slot head position
        short newRecordPOS = (short)(getSlotOffset(getSlotCount()-1)-record.length); // new record head position
        RID newRID = new RID(getCurPage(), (int)getSlotCount()+1); // new record ID

        // add new record slot
        setShortValue(newRecordPOS, newSlotPOS);
        setShortValue((short)record.length, newSlotPOS + SHORT_FIELD_SIZE);

        setSlotCount((short)(getSlotCount()+1)); // increate the slot count by 1

        // add new record data
        System.out.println(">> insertRecord: insert '"+Convert.getIntValue(0,record)+"', in page "+getCurPage().pid+", slot "+newRID.slotno);
        System.out.println(">> insertRecord: insert '"+Convert.getIntValue(0,record)+"', adding " + record.length + " bytes data to pos = " + newRecordPOS+" with "+getFreeSpace()+" bytes left.");
        for (int index = newRecordPOS; index < (newRecordPOS + record.length); index++) {
            getData()[index] = record[index-newRecordPOS];
        }

        // update freespace value
        setFreeSpace((short)(getFreeSpace()-SLOT_SIZE-record.length));

        return newRID;
    }
  
    byte[] selectRecord(RID rid) {
        // Selects a record from the page.
        short recordLength = getSlotLength(rid.slotno);
        short recordOffset = getSlotOffset(rid.slotno);
        System.out.println(">> selectRecord: rid.page = "+rid.pageno+"; rid.slotno = "+rid.slotno+"; page_size = " + PAGE_SIZE +"; recordLength = " + recordLength + "; recordOffset = " + recordOffset + "\n");

        return Arrays.copyOfRange(getData(),recordOffset,recordOffset+recordLength);
    }
    
    public void updateRecord(RID rid, heap.Tuple record) {
        // Updates a record on the page.
    }

    public void deleteRecord(RID rid) {
        // Deletes a record from the page.
		// >> parameters
		// rid: the ID of the record to be deleted.
		// >> details
        // In this implementation of the Heap File, deleting a record does not compact the space. 
		// Instead, the total free space is kept inside the page, and unless all available pages are exhausted
		// no page compacting operation is executed. Therefore, the delete record operation becomes very simple by
		// setting the slot record offset to -1.

		if (rid.slotno < getSlotCount()) {
			// the requested record slot is found
        	short rmSlotPOS = (short)(TOTAL_OFFSET + rid.slotno*SLOT_SIZE); // slot head position

        	// set to -1
        	setShortValue((short)-1, rmSlotPOS);
		} else {
			// there is no such slot

		}
    }

    public RID firstRecord() {
        // Gets the RID of the first record on the page, or null if none.
        RID firstRid = new RID();
		firstRid.pageno = getCurPage();

		int nextSlotNo = 0; // first slot number

		while (nextSlotNo < getSlotCount() && getSlotOffset(nextSlotNo) == -1) {
			// seek next non-removed record
			nextSlotNo++;
		}


		if (nextSlotNo >= getSlotCount()) {
			// next record does not exist
			firstRid = null;
		} else {
			// next record is found
			firstRid.slotno = nextSlotNo;
		}

        return firstRid;
    }
    
    RID nextRecord(RID curRid) {
        // Gets the next RID after the given one, or null if no more.
		int nextSlotNo = curRid.slotno+1; // next slot number

		while (nextSlotNo < getSlotCount() && getSlotOffset(nextSlotNo) == -1) {
			// seek next non-removed record
			nextSlotNo++;
		}


		if (nextSlotNo >= getSlotCount()) {
			// next record does not exist
			curRid = null;
		} else {
			// next record is found
			curRid.slotno = nextSlotNo;
		}

        return curRid;
    }

    boolean hasNext(RID curRid) {
        // Returns true if the iteration has more elements.
        return true;
    }

    ////////////////////////////
    // SET information blocks
    ////////////////////////////
    
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

    ////////////////////////////
    // GET information blocks
    ////////////////////////////

    PageId getCurPage() {
        // Gets the current page's id.
        PageId pageId = new PageId(getIntValue(THISID_OFFSET));
        return pageId;
    }
    
    PageId getPrevPage() {
        // Gets the previous page's id.
        PageId pageId = new PageId(getIntValue(PREVID_OFFSET));
        return pageId;
    }

    PageId getNextPage() {
        // Gets the next page's id.
        PageId pageId = new PageId(getIntValue(NEXTID_OFFSET));
        return pageId;
    }
     
    short getType() {
        // Gets the arbitrary type of the page.
        return getShortValue(TYPE_OFFSET);
    }
  
    short getSlotCount() {
        // Gets the number of slots on the page.
		// >> details
		// Notice that this number is less or equal to the amount of records, since some records
		// may have been removed, resulting in a slot but with -1 offset.
        return getShortValue(SLOTCNT_OFFSET);
    }
    
    short getFreeSpace() {
        // Gets the amount of free space (in bytes).
        return getShortValue(FREESPACE_OFFSET);
    }

    short getContFreeSpace() {
        // Gets the amount of continous free space (in bytes).
        // this is defined as space between tail of slots block and head of last record
        short slotTailOffset = (short)(TOTAL_OFFSET+getSlotCount()*SLOT_SIZE);
        short recordHeadOffset = PAGE_SIZE;

        for (int index=(getSlotCount()-1); index>=0; index--) {
            // backward iterate through all slots.
            // this makes sure a removed record won't be selected

            if (getSlotOffset(index) >= 0) {
                // last record found
                recordHeadOffset = getSlotOffset(index);
                break;
            }
        }

        return (short)(recordHeadOffset - slotTailOffset);
    }
   
    ////////////////////////////
    // utilities
    ////////////////////////////
   
    void print() {
        // Prints the contents of a heap file page.
        List<String> log = new ArrayList<String>();
        log.add("*********************\n");
        log.add("Page id = " + getCurPage().pid + "\n");
        log.add("---------------------\n");

        log.add("(1) " + String.valueOf(getIntValue(THISID_OFFSET)) + " ");                // this pid
        log.add("(2) " + String.valueOf(getIntValue(PREVID_OFFSET)) + " ");   // prev pid
        log.add("(3) " + String.valueOf(getIntValue(NEXTID_OFFSET)) + " "); // next pid

        log.add("(4) " + String.valueOf(getShortValue(SLOTCNT_OFFSET)) + " ");   // slot count
        log.add("(5) " + String.valueOf(getShortValue(TYPE_OFFSET)) + " "); // type
        log.add("(6) " + String.valueOf(getShortValue(FREESPACE_OFFSET)) + " "); // free space

        for (int index = 1; index <= getSlotCount(); index++) {
            short offset_var = getShortValue(TOTAL_OFFSET+(index-1)*SHORT_FIELD_SIZE*2);
            short length_var = getShortValue(TOTAL_OFFSET+(index-1)*SHORT_FIELD_SIZE*2+SHORT_FIELD_SIZE);
            log.add("(slot:" + index + ") " + offset_var + " " + length_var + " ");
        }
        
        log.add("\n*********************\n");

        // Print all logs
        for (int index = 0; index < log.size(); index++) {
            System.out.print(log.get(index));
        }
    }
}
