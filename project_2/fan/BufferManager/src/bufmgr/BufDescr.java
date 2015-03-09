/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bufmgr;

import global.PageId;

/**
 *
 * @author Fan Chen
 */
public class BufDescr {
    
    private PageId _pageNumber = null;
    private int _pinCount;
    private boolean _isDirty;
    private boolean _isEmpty;

    public BufDescr() {
        this._pageNumber = new PageId();
        this._pinCount = 0;
        this._isDirty = false;
        this._isEmpty = false;
               
    }

    public int getPageID() {
        return _pageNumber.pid;
    }

    public int getPinCount() {
        return _pinCount;
    }

    public boolean isDirty() {
        return _isDirty;
    }

    public boolean isEmpty() {
        return _isEmpty;
    }

    public void setPageNumber(int _pageID) {
        this._pageNumber.pid = _pageID;
    }

    public void setPinCount(int _pinCount) {
        this._pinCount = _pinCount;
    }

    public void setIsDirty(boolean _isDirty) {
        this._isDirty = _isDirty;
    }

    public void setIsEmpty(boolean _isEmpty) {
        this._isEmpty = _isEmpty;
    }
    
    
   public void increasePinCount(){
        this._pinCount++;
   } 
   
   public void decreasePinCount(){
        this._pinCount--;
   } 

    
}
