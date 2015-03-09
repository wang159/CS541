/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bufmgr;

import chainexception.*;
import global.GlobalConst;
import global.Page;
import global.PageId;
import diskmgr.*;
import global.Convert;
import global.Minibase;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Fan Chen
 */

public class BufMgr {
    int _numBufs = 0;
    byte[][] _bufferPool = null;
    PageFrameTable _PageFrameTable = null;
    BufDescr[] _BufDescriptor = null;
    String _replacementPolicy = "LIRS";
    int _frame_ToPin = 0;
    int _temp_Frame = 0;
    LIRSReplace _replacement =new LIRSReplace(_numBufs);

    public BufMgr(int numBufs, int lookAheadSize, String replacementPolicy) throws ChainException {
        _numBufs = numBufs;
        _replacement = new LIRSReplace(_numBufs);
        _replacement.initializeLIRS();
        _BufDescriptor = new BufDescr[_numBufs];
        for (int ii=0; ii<numBufs; ii++)
        {
            _BufDescriptor[ii] = new BufDescr();
        }

        //System.out.print("_BufDescriptor 1 is "+_BufDescriptor[1].getPageID()+"\n");
        _bufferPool = new byte[_numBufs][GlobalConst.PAGE_SIZE];
        _PageFrameTable = new PageFrameTable(_numBufs);
        
    }
    public void testFrameTrue()
    {
        for(int ii=0; ii<_numBufs; ii++)
        {
            System.out.print("Frame :"+ii+ "is dirty: "+_BufDescriptor[ii].isDirty()+"\n");
        }
    }

    public void pinPage(PageId pageno, Page page, boolean emptyPage) throws ChainException, InvalidPageNumberException, FileIOException, IOException, BufferPoolExceededException {
        if(page == null) {
            throw new ChainException(null, "please give a valid page");            
        }
        if (isBufFull()) //if buffer is full, get the replacement
            {
                throw new BufferPoolExceededException(null,"BUFFER_IS_FULL");                
            }     
        int _this_Frame = _PageFrameTable.getFrameForPage(pageno.pid);
        //int data =1;
        //data = Convert.getIntValue ( 0,page.getpage());
       // System.out.print("page "+pageno.pid+ " has data " +data+"\n ");
        if (_this_Frame >= 0 ) //this means Page is already in the PageFrameTable
        {

            // if page has been changed but not set dirty, then setdirty true
            Page temp = new Page();
            try{
                Minibase.DiskManager.read_page(pageno,temp);
            }
            catch(Exception exception){
            throw new ChainException(exception, "Page could not be read");}
            if(_bufferPool[_this_Frame]!=temp.getpage()) _BufDescriptor[_this_Frame].setIsDirty(true);;
            
            _frame_ToPin = _this_Frame;
            
        }
        else //Page is not in the PageFrameTable
        {
           //_frame_ToPin = getFrame();
            _frame_ToPin = pageno.pid % _numBufs;
            if (isBufFull()) //if buffer is full, get the replacement
            {
                
                _frame_ToPin = _replacement.getFirstReplaceFrame();
                throw new BufferPoolExceededException(null,"BUFFER_IS_FULL");
                
            }       
            
           // System.out.print("Page "+pageno.pid+" pin on frame "+_frame_ToPin+"\n");
            _PageFrameTable.setPageFrameBucket(pageno.pid, _frame_ToPin); // add to pageframetable
        }

           //System.out.print("Frame dirty is "+_BufDescriptor[_frame_ToPin].isDirty()+"\n");
            if(_BufDescriptor[_frame_ToPin].isDirty()) {
                int page_temp_pid = _BufDescriptor[_frame_ToPin].getPageID();
                PageId page_temp = new PageId();
                page_temp.pid = page_temp_pid;
                flushPage(page_temp);                
            }       
            _BufDescriptor[_frame_ToPin].setIsDirty(false);
            //System.out.print("frame"+_frame_ToPin+" count is "+_BufDescriptor[_frame_ToPin].getPinCount()+" \n");
            _BufDescriptor[_frame_ToPin].increasePinCount();
            //System.out.print("frame"+_frame_ToPin+" count is "+_BufDescriptor[_frame_ToPin].getPinCount()+" \n");
            _BufDescriptor[_frame_ToPin].setPageNumber(pageno.pid);
            _replacement.setLIRS(_frame_ToPin);//set the LIRS sequence for this frame based on this pinning
            Page temp = new Page();
            try{
                Minibase.DiskManager.read_page(pageno,temp);
            }
            catch(Exception exception){
            throw new ChainException(exception, "Page could not be read");}
            _bufferPool[_frame_ToPin]=temp.getpage();
            page.setpage(_bufferPool[_frame_ToPin]); //pin page
            int _temp1 = _temp_Frame ++;

    }
    
    public void unpinPage(PageId pageno, boolean dirty) throws ChainException, PageUnpinnedException, HashEntryNotFoundException{
        if(dirty==true)
        {
            int _frameId = _PageFrameTable.getFrameForPage(pageno.pid);
            //System.out.print("page is "+pageno.pid+ " frame To unpin is "+_frameId+"\n");
            if (_frameId >= 0)
            {
                //_replacement.setLIRS(_frameId);//set the LIRS sequence for this frame based on this pinning
                //_PageFrameTable.removePageFrameBucket(pageno.pid); //remove from PageFrameTable
                _BufDescriptor[_frameId].setIsDirty(dirty);
                //_BufDescriptor[_frameId].increasePinCount();
                //System.out.print(_frameId+" Frame dirty is "+_BufDescriptor[_frameId].isDirty()+"\n");
                if (_BufDescriptor[_frameId].getPinCount()>0) _BufDescriptor[_frameId].decreasePinCount();// decrease pin count
                else
                {
                    throw new PageUnpinnedException(null,"Page is not pined yet");
                }
            }
            else 
            {
                throw new HashEntryNotFoundException(null, "Page doesn't exist in buffer pool");
            }
        }
    }



    public PageId newPage(Page firstpage, int howmany) throws ChainException, InvalidPageNumberException, FileIOException, IOException, BufferPoolExceededException{
           if (firstpage == null){
               throw new ChainException(null,"please set a valid page");
           }
           if(!isBufFull()) //in the case when Buffer is not full
           {
               PageId _this_PageId = new PageId();
               try{Minibase.DiskManager.allocate_page(_this_PageId, howmany);}
               catch(Exception exception){throw new ChainException(exception, "No" + howmany + "pages available.");}
               
               pinPage(_this_PageId, firstpage, false);
               return _this_PageId;
           }
           else{
                throw new ChainException(null,"Buffer is full");
           }
    }
    
    public boolean isBufFull()
    {
        for(int ii=0; ii<_numBufs; ii++)
        {
            if(_BufDescriptor[ii].getPinCount() == 0) return false;
        }
        return true;
    }
    public void testBufFull()
    {
        for(int ii=0; ii<_numBufs; ii++)
        {
            System.out.print("Frame :"+ii+" has count"+_BufDescriptor[ii].getPinCount()+"\n");
        }
    }
    public int getFrame()
    {
        int _frame = -1;
        for(int ii=0; ii<_numBufs; ii++)
        {
            if (_BufDescriptor[ii].getPinCount() == 0) {
                _frame=ii;
                //System.out.print("frame"+_frame+" count is "+_BufDescriptor[_frame].getPinCount()+" \n");
                break;
                
            }
        }
        return _frame;
    }
    // freePage is used to delete a page on disk
    public void freePage(PageId globalPageId) throws ChainException
    {
        //set all the frame count to be zero
        for(int ii=0; ii<_numBufs; ii++)
        {
            _BufDescriptor[ii].setPinCount(0);
        }
        Minibase.DiskManager.deallocate_page( globalPageId );
    }
    // used to flush a page of the buffer to disk
    private void flushPage(PageId pageno) throws ChainException, InvalidPageNumberException, FileIOException, IOException {
        int _this_Frame = _PageFrameTable.getFrameForPage(pageno.pid);
        if (_this_Frame>=0){
        //System.out.print("pid is"+pageno.pid+"\n");
        //System.out.print("Frame number is"+_this_Frame+"\n");
        Page _this_Page = new Page();
        _this_Page.setpage(_bufferPool[_this_Frame]);
        Minibase.DiskManager.write_page(pageno,_this_Page);
        //System.out.print("flushing page "+pageno.pid+" to disk\n");
        }
    }

    public void flushAllPages() throws ChainException, InvalidPageNumberException, FileIOException, IOException
    {
        ArrayList _all_PageList = _PageFrameTable.getAllPages();
        for (int ii=1; ii<_all_PageList.size();ii++)
        {
            PageId _this_PageId = new PageId();
            _this_PageId.pid = (int) _all_PageList.get(ii);
            flushPage(_this_PageId);
        }
    }
//Returns the total number of buffer frames
    public int getNumBuffers() {
        return _numBufs;
    }
    
    public int getNumUnpinned() {
        int _numUnpinned = 0;
        for (int ii=0; ii<_numBufs;ii++)
        {
            if(_BufDescriptor[ii].getPinCount()==0) _numUnpinned++;
        }
        return _numUnpinned;
    }




    
}
