package bufmgr;

import java.io.*;
import java.util.*;


import global.*;
import diskmgr.*;
import chainexception.ChainException;

public class BufMgr {
	private byte[][] bufPool;
	private FrameDesc[] frDescriptor;
	private Queue<Integer> queue;
	private int numbufs;
	private FrameHashTable frHashTab;
	private LIRS rep;
	private LinkedList<Integer> unpinlist;

	/**
	* Create the BufMgr object.
	* Allocate pages (frames) for the buffer pool in main memory and
	* make the buffer manage aware that the replacement policy is* specified by replacerArg (e.g., LH, Clock, LRU, MRU, LIRS, etc.).
	*
	* @param numbufs number of buffers in the buffer pool
	* @param lookAheadSize number of pages to be looked ahead
	* @param replacementPolicy Name of the replacement policy
	*/

	public BufMgr(int numbufs,int lookAheadSize, String replacerArg) {
		// Allocate pages (frames) for the buffer pool in main memory
		this.numbufs = numbufs;
		bufPool = new byte[numbufs][GlobalConst.PAGE_SIZE];
		frDescriptor = new FrameDesc[numbufs];
		frHashTab = new FrameHashTable(29);
		if (replacerArg.charAt(0) == 'L') {//LIRS replacement policy
			rep = new LIRS(numbufs);
			unpinlist = new LinkedList<Integer>();
			for(int i = 0;i<numbufs;i=i+1){
				frDescriptor[i] = new FrameDesc(null,0,false);
				unpinlist.add(i);
			}
		} 
		else {// for test only
			if (replacerArg.charAt(0) == 'C') {
			queue = new LinkedList<Integer>();
			for (int i = 0; i < numbufs; i++)
				queue.add(i);
			}
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
	* @param emptyPage true (empty page)? false (non­empty page)
	*/
	public void pinPage(PageId pageno, Page page, boolean emptyPage) throws ChainException{
		FPpair fp = new FPpair();
		fp = frHashTab.getPair(pageno);
		if(fp!=null) // The page is already in the buffer pool
		{			
			int index = fp.getFrame();
			// If it is, increment the pin_count and return a pointer to this
			// page.
			frDescriptor[index].IncrePinCount();
			unpinlist.remove((Integer)index);
			page.setpage(bufPool[index]);
			rep.increGlobalOpId();
			rep.setFrameOpId(index);
			if ((frDescriptor[index].getPageId()!= null) && frDescriptor[index].isDirty()) {
				flushPage(new PageId(frDescriptor[index].getPageId().pid));
				//frHashTab.DeleteFromDir(frDescriptor[index].getPageId(),index);
			}
			frHashTab.AddToDir(new PageId(pageno.pid), index);
			//System.out.println("index = " + index);
		}
		// If the page is not in the pool,
		else 
		{
			int index = -1;
			//if (queue.size() != 0){ 
			if(!isFull()){

				for(int i = 0; i< numbufs;i = i+1){
					if(frDescriptor[i].getPageId()==null) {//
						index = i;
						break;
					}
				}
				/*
				System.out.println("emptylist:");
				for(int i = 0; i< emptylist.size();i++){
					System.out.print(emptylist.get(i)+",");
				}
				System.out.println();
				*/
				rep.increGlobalOpId();
				rep.setFrameOpId(index);
				//index = getFirstEmptyFrame();
				
				if ((frDescriptor[index].getPageId()!= null) && frDescriptor[index].isDirty()) {			
					flushPage(new PageId(frDescriptor[index].getPageId().pid));
					frHashTab.DeleteFromDir(frDescriptor[index].getPageId(),index);
				}
				frDescriptor[index] = new FrameDesc(new PageId(pageno.pid),1, false);
				unpinlist.remove((Integer)index);
				frHashTab.AddToDir(new PageId(pageno.pid), index);
			} 
			else{
				if(getNumUnpinned()!=0){
					//System.out.println("num of unpinned: " + getNumUnpinned());
					int tmp = -1;
					//index = unpinlist.get(0);
					//System.out.println("unpinlist:");
					for(int i = 0; i < unpinlist.size(); i = i+1 ){	
						//System.out.print(unpinlist.get(i)+",");
						if(rep.result(unpinlist.get(i))>tmp){
							tmp = rep.result(unpinlist.get(i));
							//System.out.println("tmp: " + tmp);
							index = unpinlist.get(i);
							//System.out.println("index = " + index);
						}
					}
					//
					//index = hasMaxLIRS(LIRSresult);
					/*
					System.out.println();
					System.out.println("index: "+index);
					*/
					rep.increGlobalOpId();
					rep.setFrameOpId(index);
					if ((frDescriptor[index].getPageId()!= null) && frDescriptor[index].isDirty()) {
						flushPage(new PageId(frDescriptor[index].getPageId().pid));
						frHashTab.DeleteFromDir(frDescriptor[index].getPageId(),index);
					}
					frDescriptor[index] = new FrameDesc(new PageId(pageno.pid),1, false);
					unpinlist.remove((Integer)index);
					frHashTab.AddToDir(new PageId(pageno.pid), index);
				}
				else{
					throw new BufferPoolExceededException(null,"No unpinned buffer frame.");
				}
			}
			//System.out.println("index:" + index);
			
			Page temp = new Page();
			try {
				Minibase.DiskManager.read_page(new PageId(pageno.pid), temp);
			} catch (Exception e) {
				throw new DiskMgrException(e, "Unable to read a page.");
			}
			// and pin it.
			//bufPool[index] = new Page();
			//bufPool[index].setpage((temp.getpage().clone()));
			bufPool[index]=temp.getpage();
			page.setpage(bufPool[index]);
			//pageno.equals(frDescriptor[index].getPageId());
			//System.out.println("pid: " + pageno.pid);
			
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
	public void unpinPage(PageId pageno, boolean dirty) throws ChainException {
		FPpair fp = new FPpair();
		//System.out.println("Unpinning a page.");
		fp = frHashTab.getPair(pageno);
		//if(directory.conatin(pageno.pid)){
		if(fp!=null){
			int index = fp.getFrame();
			//System.out.println("pageid: "+ pageno.pid);
			//System.out.println("unpinning frameid: "+ index);
			//System.out.println("pin count: "+bufDescr[index].getPin_count());			
			
			if(frDescriptor[index].getPinCount() == 0){
				throw new PageUnpinnedException(null, "Trying to unpin a page that is already unpinned.");
			} 
			else
			{
				// Set the dirty bit for this frame.
				if(dirty == true){
					frDescriptor[index].SetDirtyBit(dirty);
				}
				// Further, if pin_count>0, this method should decrement it
				frDescriptor[index].DecrePinCount();
				if (frDescriptor[index].getPinCount() == 0){
					//queue.add(index);
					int addflag = 1;
					for(int i = 0 ; i < unpinlist.size(); i = i+1){
						if(unpinlist.get(i) == index){
							addflag = 0;
						}
					}
					if(addflag == 1){
						unpinlist.add((Integer)index);
					}
				}
			}
		}
		else{
			throw new HashEntryNotFoundException(null, "Entry was not found in the directory.");
		}
	}

	/**
	* Allocate new pages.* Call DB object to allocate a run of new pages and
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
	 * @throws BufferPoolExceededException 
	*/
	public PageId newPage(Page firstpage, int howmany) 
			throws ChainException{
		PageId id = new PageId(); 
		try {
			// Call DB object to allocate a run of new pages
			 Minibase.DiskManager.allocate_page(id,howmany);
		} catch (Exception e) {
			throw new DiskMgrException(e, "Unable to allocate " + howmany + " pages.");
		}

		/*
		 * find a frame in the buffer pool for the first page and pin it
		*/
		pinPage(id, firstpage, false);
		//System.out.println("pid: " + id);
		return id;

	}

	/**
	* This method should be called to delete a page that is on disk.
	* This routine must call the method in diskmgr package to
	* deallocate the page.
	*
	* @param globalPageId the page number in the data base.
	*/
	public void freePage(PageId globalPageId) throws ChainException {
		// Check whether this is a valid page or not
		//System.out.println("freeing a page.");
		FPpair fp = new FPpair(); 
		fp = frHashTab.getPair(globalPageId);
		if (fp!=null){	
			int index = fp.getFrame();
			// Getting the index of this page
			if (frDescriptor[index].getPinCount() > 0){
				throw new PagePinnedException(null, "Trying to free a page that is being pinned.");
			}

			// If it is dirty flush it
			if(frDescriptor[index].isDirty()){
				try {
					flushPage(new PageId(frDescriptor[index].getPageId().pid));//
				} catch (Exception e) {
					throw new ChainException(null, "Unable to flush a page.");
				}
			}
			// Remove it from the hash,bufferPool,bufferDescriptor
			frHashTab.DeleteFromDir(frDescriptor[index].getPageId(),index);
			frDescriptor[index] = new FrameDesc(null,0,false);
			bufPool[index] = null;
			int addflag = 1;
			for(int i = 0; i <unpinlist.size();i = i+1){
				if(unpinlist.get(i) == index){
					addflag = 0;
				}
			}
			if(addflag == 1){
				unpinlist.add((Integer)index);
			}
			try{
			Minibase.DiskManager.deallocate_page(new PageId(globalPageId.pid));
			} catch (Exception e) {
				throw new PagePinnedException(e, "Unable to deallocate pages.");
			}

		}	
		else{
			try{
				Minibase.DiskManager.deallocate_page(new PageId(globalPageId.pid));
			}catch(Exception e){ throw new ChainException(e,"Unable to allocate page.");}
		}
	}

	/**
	* Used to flush a particular page of the buffer pool to disk.
	* This method calls the write_page method of the diskmgr package.
	*
	* @param pageid the page number in the database.
	*/
	public void flushPage(PageId pageid) throws ChainException {
		Page apage = new Page();
		FPpair fp = frHashTab.getPair(pageid); 
		if(fp != null){
			int index = fp.getFrame();
			apage.setpage(bufPool[index]);
			try {
				Minibase.DiskManager.write_page(pageid, apage);
				frDescriptor[index].SetDirtyBit(false);
			} catch (Exception e) {
				throw new DiskMgrException(e, "Unable to flush a page.");
			}
		}else{
			throw new HashEntryNotFoundException(null,"Hash entry not found.");
		}
	}
	/**
	* Used to flush all dirty pages in the buffer pool to disk
	*
	*/
	public void flushAllPages() throws ChainException {
		for (int i = 0; i < numbufs; i++) {
			if(frDescriptor[i] != null)
				flushPage(new PageId(frDescriptor[i].getPageId().pid));
		}
	}
	/**
	* Returns the total number of buffer frames.
	*/
	public int getNumBuffers(){
		return numbufs;
	}
	/**
	* Returns the total number of unpinned buffer frames.
	*/
	public int getNumUnpinned() {
		return unpinlist.size();
	}
	/*
	 * Check whether the buffer is full or not !
	 */
	public boolean isFull() {
		for(int i = 0; i< numbufs; i = i+1){
			if(frDescriptor[i].getPageId()==null){
				return false;
			}
		}
		return true;
	}
	/**
	 * insertion sort an array a
	 * @param a, an array
	 * @return the index of the largest item
	 
	private int hasMaxLIRS(int[] a){
		int tmp=0,i;
		for(i=0;i<a.length;i=i+1)
			for(int j=i+1 ;j<a.length;j=j+1){
				if(a[j] > a[i]){
					tmp = j;
				}
				else{
					tmp = i;
				}
			}
		return tmp;
	}
*/
	
}
