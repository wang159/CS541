package bufmgr;

import diskmgr.*;

import java.util.*;
import java.io.*;

//import global.Convert;
import global.GlobalConst;
//import global.Minibase;
import global.Page;
import global.PageId;
import chainexception.ChainException;

public class BufMgr{ 
	//
	private final int HTSIZE = 29;
	private int numFrame = 0;
	private String ReplPolicy = "LIRS";
	private LIRS rep;
	private byte[][] BufPool;
	private FrameDesc[] frDescriptor;
	private FrameHashTable frHashTab;
	private Vector<Integer> unpinlist;
	public DiskMgr diskmanager;
/** 
* Create the BufMgr object. 
* Allocate pages (frames) for the buffer pool in main memory and 
* make the buffer manage aware that the replacement policy is 
* specified by replacerArg (e.g., LH, Clock, LRU, MRU, LIRS, etc.).  
* 
* ​ @param​  numbufs number of buffers in the buffer pool 
* ​ @param​  lookAheadSize number of pages to be looked ahead: not used
* ​ @param​  replacementPolicy Name of the replacement policy 
*/  
public BufMgr(int numfrms, int lookAheadSize, String replacementPolicy) {
	numFrame = numfrms;
	ReplPolicy=replacementPolicy;
	BufPool = new byte[numFrame][GlobalConst.PAGE_SIZE];
	frDescriptor = new FrameDesc[numFrame];
	frHashTab = new FrameHashTable(HTSIZE);
	unpinlist = new Vector<Integer>();
	if(ReplPolicy!="LIRS"){
		throw new UnsupportedOperationException("This replacement policy is not supported.\nSupported list:\nLIRS\n");
	}
	else{
		 rep = new LIRS(numfrms);
	}  
	
}
/** 
* Pin a page.  
* First check if this page is already in the buffer pool. 
* If it is, increment the pin_count and return a pointer to this 
* page. 
* If the pin_count was 0 before the call, the page was a 
* replacement candidate, but is no longer a candidate. 
* If the page is not in the pool, choose a frame (from the 
* set of replacement candidates) to hold this page, read the 
* page (using the appropriate method from {\em diskmgr} package) and pin it.  
* Also, must write out the old page in chosen frame if it is dirty 
* before reading new page.__ (You can assume that emptyPage==false for  
* this assignment.) 
* 
* @param pageno page number in the Minibase.  
* @param page the pointer point to the page.  
* @param emptyPage true (empty page); false (non­empty page) 
*/  
public void pinPage(PageId pageno, Page page, boolean emptyPage) throws ChainException {
	FPpair fp=frHashTab.getPair(pageno);
	if(fp!=null){
		int frmid=fp.getFrame();
		if(frDescriptor[frmid].getPinCount()>0){
			frDescriptor[frmid].IncrePinCount();
		}	
	}
	else{
		if(isBuffFull()){
			throw new BufferPoolExceededException(null,"Buffer pool is full. Cannot pin this page.");
		}
		else{
			//use LIRS to get the frameId
			rep.increGlobalOpId();
			int count=0,numunpinned = getNumUnpinned(),foundFid;
			int[] LIRSresult = new int[numunpinned];
			for(Iterator<Integer> i=unpinlist.iterator();i.hasNext();){
				LIRSresult[count] = rep.result(i.next());
				count = count+1;
			}
			//sort LIRSresult and select the 1st one
			foundFid = hasMaxLIRS(LIRSresult,numunpinned);
			
			//if this frame is dirty, flush it
			if(frDescriptor[foundFid].isDirty()==true){
				flushPage(frDescriptor[foundFid].getPageId());
				// remove the entry from the directory
				frHashTab.DeleteFromDir(pageno, foundFid);
			}
			// add the new page to the directory
			frHashTab.AddToDir(pageno, foundFid);
			//read new page 
			try{
				diskmanager.read_page(pageno, page);
			}catch(Exception e){
                throw new ChainException(e, "Unable to read page.");
			}
			//update in frDescriptor
			frDescriptor[foundFid]=new FrameDesc(pageno,foundFid,false);
			frDescriptor[foundFid].setPinCount(1);
		}
	}
	
	
	
}  
 
/** 
* Unpin a page specified by a pageId. 
* This method should be called with dirty==true if the client has 
* modified the page.  
* If so, this call should set the dirty bit 
* for this frame. 
* Further, if pin_count>0, this method should 
* decrement it. 
*If pin_count=0 before this call, throw an exception 
* to report error. 
*(For testing purposes, we ask you to throw 
* an exception named PageUnpinnedException in case of error.) 
* 
* @param pageno page number in the Minibase.  
* @param dirty the dirty bit of the frame 
*/  
public void unpinPage(PageId pageno, boolean dirty) throws ChainException{
	if(pageno == null){
		throw new ChainException(null,"Invalid page ID.");
	}
	FPpair fp=frHashTab.getPair(pageno);
	if(fp == null){
		throw new HashEntryNotFoundException(null, "This page does not exist in the buffer.");
	}
	else{
		if(dirty == true){
			frDescriptor[fp.getFrame()].SetDirtyBit(true);
			if(frDescriptor[fp.getFrame()].getPinCount()==0){
				throw new PageUnpinnedException(null,"The page already is unppinedd.");
			}
			else{
				frDescriptor[fp.getFrame()].DecrePinCount();
			}
		}
	}
	
}  
 
/** 
* Allocate new pages. 
* Call DB object to allocate a run of new pages and 
* find a frame in the buffer pool for the first page 
* and pin it. (This call allows a client of the Buffer Manager  
* to allocate pages on disk.) If buffer is full, i.e., you 
* can't find a frame for the first page, ask DB to deallocate 
* all these pages, and return null. 
* 
* @param firstpage the address of the first page.  
* @param howmany total number of allocated new pages. 
* 
* @return the first page id of the new pages.__ null, if error. 
 * @throws InvalidPageNumberException 
*/  
public PageId newPage(Page firstpage, int howmany) throws IOException,ChainException {
	if(firstpage == null){
		throw new ChainException(null,"the firstpage pointer cannot be null.");
	}
	if(howmany <= 0){
		throw new InvalidPageNumberException(null,"Number of pages to be allocated should be larger than 0.");
	}
	PageId firstPid = new PageId();
	if(!isBuffFull()){
		try{
			diskmanager.allocate_page(firstPid, howmany);
		}
		catch(Exception e){
			throw new ChainException(e, "Cannot allocate " + howmany + " pages.");
		}
		pinPage(firstPid,firstpage,false);
		return firstPid;
	}
	else {
		try{
			diskmanager.deallocate_page(firstPid, howmany);
		}
		catch(Exception e){
			throw new ChainException(e, "Cannot deallocate these pages."); 
		}
		return null;
	}
} 
 
/** 
* This method should be called to delete a page that is on disk.  
* This routine must call the method in diskmgr package to 
* deallocate the page. 
* 
* @param globalPageId the page number in the data base. 
*/  
public void freePage(PageId globalPageId) throws ChainException{
	if(globalPageId == null){
		throw new InvalidPageNumberException(null, "Invalid page ID.");
	}
	FPpair fp=frHashTab.getPair(globalPageId);
	if(fp == null){
		throw new HashEntryNotFoundException(null, "This page does not exist in the buffer.");
	}
	else{
		if(frDescriptor[fp.getFrame()].getPinCount()==0){
			throw new PagePinnedException(null, "This page is pinned in the buffer.");
		}
		else{
			try{
                diskmanager.deallocate_page(globalPageId);
			}catch(Exception e){
                throw new ChainException(e, "Unable to deallocate this page on disk.");
			}
		}
	}
} 
 
/** 
* Used to flush a particular page of the buffer pool to disk. 
* This method calls the write_page method of the diskmgr package. 
* 
* @param pageid the page number in the database.  
*/  
public void flushPage(PageId pageid) throws ChainException{
	if(pageid == null){
		throw new InvalidPageNumberException(null, "Invalid page ID.");
	}
	FPpair fp=frHashTab.getPair(pageid);
	if(fp == null){
		throw new HashEntryNotFoundException(null, "This page does not exist in the buffer.");
	}
	else{
		Page pageToWrite = new Page();
        pageToWrite.setpage(BufPool[fp.getFrame()]);       
        // Write frame to disk.
        try{
                diskmanager.write_page(pageid, pageToWrite);
        }catch(Exception e){
                throw new ChainException(e, "Unable to write to disk.");
        }
	}
} 
   
/** 
* Used to flush all dirty pages in the buffer pool to disk 
* 
*/  
public void flushAllPages() throws ChainException{
	int i;
	for(i = 0;i<numFrame;i=i+1){
		if(frDescriptor[i].isDirty()==true){
			flushPage(frDescriptor[i].getPageId());
		}
	}
}  
   
/** 
* Returns the total number of buffer frames. 
*/  
public int getNumBuffers() {
	return numFrame;
}  
   
/** 
* Returns the total number of unpinned buffer frames. 
*/  
public int getNumUnpinned() {
	int count = 0;
	for(int i = 0; i<frDescriptor.length;i=i+1){
		if(frDescriptor[i].getPinCount()==0){
			unpinlist.add(i);
			count = count + 1;
		}
	}
	return count;
} 

/** 
 * Returns trueness that the buffer is full.
*/
public boolean isBuffFull(){
	for(int i = 0; i<frDescriptor.length;i=i+1){
		if(frDescriptor[i].getPinCount()==0){
			return false;
		}
	}
	return true;
}
/**
 * insertion sort an array a
 * @param a, an array
 * @param alen, length of a
 * @return the index of the max value
 */
private int hasMaxLIRS(int[] a,int alen){
	int tmp,i;
	for(i=0;i<alen;i=i+1)
		for(int j=i+1 ;j<alen;j=j+1){
			if(a[j] > a[i]){
				tmp = a[j];
				a[j] = a[i];
				a[i] = tmp;
			}
		}
	int max_a=a[0];
	for(i=0;i<alen;i=i+1){
		if(a[i] == max_a){
			break;
		}
	}
	return i;
	
}
   
}
