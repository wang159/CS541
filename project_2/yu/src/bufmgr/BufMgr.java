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

	/**
	 * Create the BufMgr object.
	 * 
	 * @param numbufs
	 *            number of buffers in the buffer pool.
	 * @param replacerArg
	 *            name of the buffer replacement policy.
	 */
	public BufMgr(int numbufs,int lookAheadSize, String replacerArg) {
		// Allocate pages (frames) for the buffer pool in main memory
		this.numbufs = numbufs;
		bufPool = new byte[numbufs][GlobalConst.PAGE_SIZE];
		frDescriptor = new FrameDesc[numbufs];
		frHashTab = new FrameHashTable(29);
		//TODO handle policies
		if (replacerArg.charAt(0) == 'L') {
			//LRU POLICY
		} else {
			// for test only
			if (replacerArg.charAt(0) == 'C') {
			queue = new LinkedList<Integer>();
			for (int i = 0; i < numbufs; i++)
				queue.add(i);
			}
		}
	}

	/*
	 * Initializing the queue in beginning with all index of the array
	 */

	/*
	 * Return the first empty frame if the buffer is full throw an exception
	 */
	public int getFirstEmptyFrame() throws BufferPoolExceededException {
		if (queue.size() == 0)
			throw new BufferPoolExceededException(null, "BUFFER_POOL_EXCEED");
		else
			return queue.poll();
	}

	/*
	 * Check whether the buffer is full or not !
	 */
	public boolean isFull() {
		return (queue.size() == 0);
	}

	/*
	 * Return the frame that contains the given page id
	 */
	private int getFrameNumber(PageId pId) throws HashEntryNotFoundException {
		FPpair fp = new FPpair();
		fp = frHashTab.getPair(pId);
		if(fp!=null)
			return fp.getFrame();
		else {
			throw new HashEntryNotFoundException(null,
					"BUF_MNGR:HASH_ENTRY_NOT_FOUND_EXCEPTION");
		}
	}

	/**
	 * @param Page_Id_in_a_DB
	 *            page number in the minibase.
	 * @param page
	 *            the pointer point to the page.
	 * @param emptyPage
	 *            true (empty page); false (non-empty page)
	 */
	public void pinPage(PageId pageno, Page page, boolean emptyPage)
			throws DiskMgrException, BufferPoolExceededException,
			PagePinnedException, InvalidPageNumberException, FileIOException,
			IOException, HashEntryNotFoundException 
		{
		 //First check if this page is already in the buffer pool.
		FPpair fp = new FPpair();
		fp = frHashTab.getPair(pageno);
		if(fp!=null)
		{			
			int index = fp.getFrame();
			
			// If the pin_count was 0 before the call, the page was a
			// replacement candidate, but is no longer a candidate.
			///System.out.println("index is " + index);

			if(frDescriptor[index].getPinCount() == 0)
				queue.remove(index);
			// If it is, increment the pin_count and return a pointer to this
			// page.
			frDescriptor[index].IncrePinCount();
			page.setpage(bufPool[index]);
		}
		// If the page is not in the pool,
		else 
		{
			int index = -1;
			if (queue.size() != 0) 
			{
				// choose a frame (from the
				// set of replacement candidates) to hold this page
				index = getFirstEmptyFrame();
				// Also, must write out the old page in chosen frame if it is
				// dirty before reading new page.
				//if ((bufDescr[index] != null) && bufDescr[index].isDirtyBit()) {
				if ((frDescriptor[index]!= null) && frDescriptor[index].isDirty()) {
					
					flushPage(frDescriptor[index].getPageId());
					//directory.remove(bufDescr[index].getPageNumber().pid);
					frHashTab.DeleteFromDir(frDescriptor[index].getPageId(),index);
				}

			} 
			else
				throw new BufferPoolExceededException(null,
						"BUFMGR:PAGE_PIN_FAILED");
			Page temp = new Page();
			try {
				// read the page(using the appropriate method from 
				//{\em diskmgr} package)
				Minibase.DiskManager.read_page(new PageId(pageno.pid), temp);
			} catch (Exception e) {
				throw new DiskMgrException(e, "DB.java: pinPage() failed");
			}
			// and pin it.
			//bufPool[index] = new Page();
			//bufPool[index].setpage((temp.getpage().clone()));
			bufPool[index]=temp.getpage();
			page.setpage(bufPool[index]);
			frDescriptor[index] = new FrameDesc(new PageId(pageno.pid),1, false);
			//directory.put(pageno.pid, index);
			frHashTab.AddToDir(pageno, index);
		}
	}

	/**
	 * Unpin a page specified by a pageId.
	 * 
	 * @param globalPageId_in_a_DB
	 *            page number in the minibase.
	 * @param dirty
	 *            the dirty bit of the frame
	 * @throws DiskMgrException
	 */

	public void unpinPage(PageId pageno, boolean dirty)
			throws PageUnpinnedException, HashEntryNotFoundException,
			InvalidPageNumberException, FileIOException, IOException,
			DiskMgrException {
		FPpair fp = new FPpair();
		fp = frHashTab.getPair(pageno);
		//if(directory.conatin(pageno.pid)){
		if(fp!=null){
			int index = fp.getFrame();
			//System.out.println("pageid: "+ pageno.pid);
			//System.out.println("frameid: "+ index);
			//System.out.println("pin count: "+bufDescr[index].getPin_count());			
/*
			 * If pin_count=0 before this call, throw an exception to report
			 * error. (For testing purposes, we ask you to throw an exception
			 * named PageUnpinnedException in case of error.)
			 */
			if(frDescriptor[index].getPinCount() == 0){
				throw new PageUnpinnedException(null,
						"BUFMGR:PAGE_UNPIN_FAILED");
			} 
			else
			{
				// Set the dirty bit for this frame.
				frDescriptor[index].SetDirtyBit(dirty);
				// Further, if pin_count>0, this method should decrement it
				frDescriptor[index].DecrePinCount();
				if (frDescriptor[index].getPinCount() == 0){
					queue.add(index);
				}
			}
		}
		else 
			throw new HashEntryNotFoundException(null,
					"BUFMGR:PAGE_UNPIN_FAILED");
	}

	/**
	 * Allocate new pages.
	 * 
	 * @param firstpage
	 *            the address of the first page.
	 * @param howmany
	 *            total number of allocated new pages.
	 * 
	 * @return the first page id of the new pages. null, if error.
	 */

	public PageId newPage(Page firstpage, int howmany) throws DiskMgrException,
			FreePageException, BufferPoolExceededException,
			PagePinnedException, InvalidPageNumberException, FileIOException,
			HashEntryNotFoundException, IOException, InvalidRunSizeException {
		PageId id = new PageId();
		if (isFull()) 
			return null;
 
		try {
			// Call DB object to allocate a run of new pages
			Minibase.DiskManager.allocate_page(id, howmany);
		} catch (Exception e) {
			throw new DiskMgrException(e, "DB.java: newPage() failed");
		}
		/*
		 * If buffer is full, i.e., you cant find a frame for the first page,
		 * ask DB to deallocate all these pages, and return null.
		 */
		/*
		 * find a frame in the buffer pool for the first page and pin it
		*/
		pinPage(id, firstpage, false);
		return id;

	}

	/**
	 * This method should be called to delete a page that is on disk. This
	 * routine must call the method in diskmgr package to deallocate the page.
	 * 
	 * @param globalPageId
	 *            the page number in the data base.
	 */

	public void freePage(PageId globalPageId) throws PagePinnedException,
			InvalidRunSizeException, InvalidPageNumberException,
			FileIOException, DiskMgrException, IOException,ChainException {
		// Check whether this is a valid page or not
		FPpair fp = new FPpair(); 
		fp = frHashTab.getPair(globalPageId);
		if (fp!=null){	
			int i;
			try {
				// Getting the index of this page
				i = getFrameNumber(globalPageId);
				// If it has more than one pin on it
				// throw an Exception
				if (frDescriptor[i].getPinCount() > 1){
					throw new PagePinnedException(null,
							"DB.java: freePage() failed");
				}
				// If pin count !=0 unpin this page
				//Not sure from this condition :S :S 
				//if (frDescriptor[i].getPinCount() == 1)
					//unpinPage(frDescriptor[i].getPageId(),frDescriptor[i].isDirty());
				// If it is dirty flush it
				if(frDescriptor[i].isDirty())
					try {
						flushPage(globalPageId);
					} catch (Exception e) {
						throw new FreePageException(null,
								"BUFMGR: FAIL_PAGE_FREE");
					}
				// Remove it from the hash,bufferPool,bufferDescriptor
				frHashTab.DeleteFromDir(globalPageId,i);
				bufPool[i] = null;
				frDescriptor[i] = null;
				Minibase.DiskManager.deallocate_page(new PageId(
						globalPageId.pid));
			} catch (Exception e) {
				throw new PagePinnedException(e, "BUFMGR:FAIL_PAGE_FREE");
			}

		}	
		else{
			try{
				Minibase.DiskManager.deallocate_page(new PageId(globalPageId.pid));
			}catch(Exception e){ throw new ChainException(e,"Unable to allocate page.");}
		}
	}

	/**
	 * Used to flush a particular page of the buffer pool to disk. This method
	 * calls the write_page method of the diskmgr package.
	 * 
	 * @param pageid
	 *            the page number in the database.
	 */
	public void flushPage(PageId pageid) throws HashEntryNotFoundException,
			DiskMgrException {
		Page apage = null;
		int i = getFrameNumber(pageid);
		if(frDescriptor[i]!=null)
			apage = new Page(bufPool[i]);
		;
		try {
			if (apage != null) {
				Minibase.DiskManager.write_page(pageid, apage);
				frDescriptor[i].SetDirtyBit(false);
			} else
				throw new HashEntryNotFoundException(null,
						"BUF_MNGR: PAGE NOT FLUSHED ID EXCEPTION!");
		} catch (Exception e) {
			throw new DiskMgrException(e, "DB.java: flushPage() failed");
		}
	}

	public void flushAllPages() throws HashEntryNotFoundException,
			DiskMgrException {
		for (int i = 0; i < numbufs; i++) {
			if(frDescriptor[i] != null)
				flushPage(frDescriptor[i].getPageId());
		}
	}

	public int getNumUnpinned() {
		return queue.size();
	}
}
