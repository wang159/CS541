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


    public void pinPage(PageId pageno, Page page, boolean emptyPage) throws ChainException, InvalidPageNumberException, FileIOException, IOException {
        if(page == null) {
            throw new ChainException(null, "please give a valid page");            
        }
        int _this_Frame = _PageFrameTable.getFrameForPage(pageno.pid);
        if (_this_Frame >= 0 ) //this means Page is already in the PageFrameTable
        {
            if(_BufDescriptor[_this_Frame].getPinCount() == 0)
            {
                _replacement.removeFromList(_this_Frame);
                // if the pin count is zero, the frame would not be in the replacement list for LIRS policy
            }
            _BufDescriptor[_this_Frame].increasePinCount();
            page.setpage(_bufferPool[_this_Frame]);
        }
        else //Page is not in the PageFrameTable
        {
            // to find any frame that is not pined yet as the frame to be pined ; check whether buffer is full

//            for (int ii=0; ii<_numBufs; ii++)
//            {
//                System.out.print("ii is "+ii+"\n");
//                System.out.print("_frame_ToPin "+_frame_ToPin+"\n");
//                System.out.print("pin count for ii is "+_BufDescriptor[ii].getPinCount()+"\n");
//                if (_BufDescriptor[ii].getPinCount() == 0) 
//                {
//                    _frame_ToPin = ii;
//                    
//                    break;
//                }
//            }
            System.out.print("Page "+pageno.pid+" pin on frame "+_frame_ToPin+"\n");
            if (isBufFull()) //if buffer is full, get the replacement
            {
                _frame_ToPin = _replacement.getFirstReplaceFrame();
            }
            if(_BufDescriptor[_frame_ToPin].isDirty()) flushPage(pageno);
            page.setpage(_bufferPool[_frame_ToPin]); //pin page
            _PageFrameTable.setPageFrameBucket(pageno.pid, _frame_ToPin); // add to pageframetable
            _BufDescriptor[_frame_ToPin].setIsDirty(false);
            _BufDescriptor[_frame_ToPin].increasePinCount();
            _BufDescriptor[_frame_ToPin].setPageNumber(pageno.pid);
            _replacement.setLIRS(_frame_ToPin);//set the LIRS sequence for this frame based on this pinning
            try{
                Minibase.DiskManager.read_page(pageno,page);
            }
            catch(Exception exception){
            throw new ChainException(exception, "Page could not be read");}
            _frame_ToPin ++;
                
        }
        
    }

    public void unpinPage(PageId pageno, boolean dirty) throws ChainException, PageUnpinnedException{
        if(dirty==true)
        {
            int _frameId = _PageFrameTable.getFrameForPage(pageno.pid);
            //System.out.print("_frame_To unpin is "+_frameId+"\n");
            if (_frameId >= 0)
            {
                _replacement.setLIRS(_frameId);//set the LIRS sequence for this frame based on this pinning
                _PageFrameTable.removePageFrameBucket(pageno.pid); //remove from PageFrameTable
                if (_BufDescriptor[_frameId].getPinCount()>0) _BufDescriptor[_frameId].decreasePinCount();// decrease pin count
                else
                {
                    throw new PageUnpinnedException(null,"Page is not pined yet");
                }
            }
            else 
            {
                throw new ChainException(null, "Page" + pageno.pid + "doesn't exist in buffer pool");
            }
        }
    }



    public PageId newPage(Page firstpage, int howmany) throws ChainException, InvalidPageNumberException, FileIOException, IOException{
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
    // freePage is used to delete a page on disk
    public void freePage(PageId globalPageId) throws ChainException
    {
        Minibase.DiskManager.deallocate_page( globalPageId );
    }
    // used to flush a page of the buffer to disk
    private void flushPage(PageId pageno) throws ChainException, InvalidPageNumberException, FileIOException, IOException {
        int _this_Frame = _PageFrameTable.getFrameForPage(pageno.pid);
        Page _this_Page = new Page();
        _this_Page.setpage(_bufferPool[_this_Frame]);
        Minibase.DiskManager.write_page(pageno,_this_Page);
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
