/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bufmgr;

/**
 *
 * @author Fan Chen
 */
public class PageFrameBucket {

    int _pageID;
    int _frameID;
    public PageFrameBucket(int PageID, int FrameID){
        this._pageID = PageID;
        this._frameID = FrameID;
    }

    public int getPageID() {
        return _pageID;
    }

    public int getFrameID() {
        return _frameID;
    }

    public void setPageID(int PageID) {
        this._pageID = PageID;
    }

    public void setFrameID(int FrameID) {
        this._frameID = FrameID;
    } 
    
}
    

