package bufmgr;


import global.PageId;


public class FrameDesc {
    private PageId page_number;
    private int pin_count;
    private boolean dirtybit;
    public FrameDesc(){}
    
    public FrameDesc(PageId pid, int pcount, boolean dbit){
    	page_number = pid;
    	pin_count = pcount;
    	dirtybit = dbit;
    }
    
    public PageId getPageId(){
    	return page_number;
    }
    
    public int getPinCount(){
    	return pin_count;
    }
    
    public void setPinCount(int pcount){
    	pin_count = pcount;
    }
    
    public void IncrePinCount(){
    	pin_count = pin_count + 1;
    }
    
    public void DecrePinCount(){
    	if(pin_count>=1){
    		pin_count = pin_count - 1;
    	}
    	else{
    		pin_count = 0;
    	}
    }
    
    public boolean isDirty(){
    	return dirtybit;
    }
    
    public void SetDirtyBit(boolean truefalse){
    	dirtybit = truefalse;
    }
    
}
