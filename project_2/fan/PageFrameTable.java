/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bufmgr;

import chainexception.ChainException;
import com.sun.org.apache.bcel.internal.Constants;
import diskmgr.*;
import java.util.ArrayList;

/**
 *
 * @author Fan Chen
 */
public class PageFrameTable {    
    ArrayList[] _PageFrameTable = null;

    public PageFrameTable(int TableSize) {
        this._PageFrameTable = new ArrayList[TableSize];
        for(int i = 0; i<TableSize; i++){
        _PageFrameTable[i] = new ArrayList();
        }
    }    
    // FindExistingFramePageBucket returns the PageFrameBucket for the PageID
    public PageFrameBucket findExistingFramePageBucket (int PageID) throws ChainException{
        int _frameID = hashFunction(PageID);
        ArrayList _this_pair = _PageFrameTable[_frameID];
        if (_this_pair != null){
            for (Object _this_pair1 : _this_pair) {
                PageFrameBucket _this_PageFrameBucket = (PageFrameBucket) _this_pair1;
                if(_this_PageFrameBucket.getPageID() == PageID)
                { 
                    return _this_PageFrameBucket;
                }
            }
            //After the for loop, if the PageID has not been found yet, then return null
            return null;
        }
        else{
            return null;
        }
        
    }
    public void setPageFrameBucket(int PageID, int FrameID) throws ChainException
    {
        PageFrameBucket _this_PageFrameBucket = findExistingFramePageBucket(PageID);
        if (_this_PageFrameBucket != null)
        {
            throw new DuplicateEntryException(null, "Frame for page" + PageID + "is already set.");
        }
        else{
            PageFrameBucket _new_PageFrameBucket = new PageFrameBucket(PageID,FrameID);
            ArrayList _this_Bucket = _PageFrameTable[hashFunction(PageID)];
            _this_Bucket.add(_new_PageFrameBucket);
            
        }
    }
    
    public int getFrameForPage(int PageID) throws ChainException
    {
        PageFrameBucket _this_PageFrameBucket = findExistingFramePageBucket(PageID);
        int _notFound=-1;
        if(_this_PageFrameBucket != null)
        {
            return _this_PageFrameBucket.getFrameID();
        }
        else
        {
            //throw new DuplicateEntryException(null, "Frame for page" + PageID + "is not found.");
            return _notFound;
        }
    }
    
    public void removePageFrameBucket(int PageID) throws ChainException
    {
        PageFrameBucket _this_PageFrameBucket = findExistingFramePageBucket(PageID);
        if(_this_PageFrameBucket != null)
        {
            ArrayList _this_Bucket = _PageFrameTable[hashFunction(PageID)];
            _this_Bucket.remove(_this_PageFrameBucket);
        }
    }
    
    public void printAllPageFrameBucket()
    {
        for (int ii=0; ii<_PageFrameTable.length; ii++)
        {
            ArrayList _this_Bucket = _PageFrameTable[ii];
            for(int jj=0; jj<_this_Bucket.size ();jj++)
            {
                PageFrameBucket _this_PageFrameBucket = (PageFrameBucket) _this_Bucket.get(jj);
                System.out.println("PageID:" + _this_PageFrameBucket.getPageID() + "FrameID" + _this_PageFrameBucket.getFrameID());
            }
        }
    }
    
    private int hashFunction(int PageID){
        return (PageID*3 + 4) % 1023;
    }
    
    public ArrayList getAllPages()
    {
        ArrayList _return_Array = new ArrayList();
        for (int ii=0; ii<_PageFrameTable.length; ii++)
        {
            ArrayList _this_Bucket = _PageFrameTable[ii];
            for(int jj=0; jj<_this_Bucket.size ();jj++)
            {
                PageFrameBucket _this_PageFrameBucket = (PageFrameBucket) _this_Bucket.get(jj);
                _return_Array.add(new Integer(_this_PageFrameBucket.getPageID()));
            }
        }
        return _return_Array;
    }
    
}
