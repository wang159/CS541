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
    int _frame_ToPin = -1;
    LIRSReplace _replacement =new LIRSReplace(_numBufs);

    public BufMgr(int numBufs, int lookAheadSize, String replacementPolicy) throws ChainException {
        _numBufs = numBufs;
        _replacement = new LIRSReplace(_numBufs);
        _replacement.initializeLIRS();
        _BufDescriptor = new BufDescr[_numBufs];
        _bufferPool = new byte[_numBufs][GlobalConst.PAGE_SIZE];
        _PageFrameTable = new PageFrameTable(_numBufs);
    }


    public void pinPage(PageId pageno, Page page, boolean emptyPage) throws ChainException {
        if(page == null) return;
        int _this_Frame = _PageFrameTable.getFrameForPage(pageno.pid);
        if (_this_Frame > 0 ) //this means Page is already in the PageFrameTable
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
            _frame_ToPin = -1;
            for (int ii=0; ii<_numBufs; ii++)
            {
                if (_BufDescriptor[ii].getPinCount() == 0) 
                {
                    _frame_ToPin = ii;
                    break;
                }
            }
            if (_frame_ToPin<0) //if buffer is full, get the replacement
            {
                _frame_ToPin = _replacement.getFirstReplaceFrame();
            }
            if(_BufDescriptor[_frame_ToPin].isDirty()) flushPage(pageno);
            page.setpage(_bufferPool[_frame_ToPin]); //pin page
            _PageFrameTable.setPageFrameBucket(pageno.pid, _frame_ToPin); // add to pageframetable
            _BufDescriptor[_frame_ToPin].setIsDirty(false);
            _BufDescriptor[_frame_ToPin].increasePinCount();
            _BufDescriptor[_frame_ToPin].setPageNumber(pageno.pid);
            try{
                (diskmgr.DiskMgr).read_page(pageno,page);
            }
            catch(Exception exception){
            throw new ChainException(exception, "Page couldn't be read");}          
                
        }
        
    }

    public void unpinPage(PageId pageno, Page page, boolean emptyPage) throws ChainException{
        
    }

    public int getNumUnpinned() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void freePage(PageId pid) {
        
    }

    public PageId newPage(Page firstpage, int howmany) throws ChainException{
        
    }
//Returns the total number of buffer frames
    public int getNumBuffers() {
        return _numBufs;
    }

    private void flushPage(PageId pageno) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void unpinPage(PageId pgid, boolean UNPIN_CLEAN) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
