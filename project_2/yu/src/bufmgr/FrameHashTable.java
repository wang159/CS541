package bufmgr;



import java.util.Vector;

import global.PageId;
import chainexception.ChainException;
import diskmgr.DuplicateEntryException;

class bucket {

	private Vector<FPpair> buc;
	public bucket(){
		buc = new Vector<FPpair>();
	}
	public bucket(int dirsize){
		buc = new Vector<FPpair>(dirsize); 
	}
	public void Add(FPpair newpair){
		int addflag=0;
		for(FPpair obj:buc){
			if(obj.getPage()==newpair.getPage()){
				addflag=1;//throw new DuplicateEntryException(null,"Page exists in the buffer.");
			}
		}
		if(addflag==0){
			buc.add(newpair);
		}
	}
	public void Delete(FPpair newpair){
		for(FPpair obj:buc){
			if(obj.getPage()==newpair.getPage()){
				buc.remove((FPpair)obj);
			}
			
		}
		
	}
	public FPpair getpair(int pid){
		//Iterator<FPpair> i=buc.iterator();
		//System.out.println(pid);
		for (FPpair obj : buc) {
        		if (obj.getPage()==pid) {
            			return obj;
      		        }
  		}
		return null;
	}

}

public class FrameHashTable {
    private int HTSIZE = 19;
    private bucket[] dir;
    PageId pid = null;
    public FrameHashTable(){
    	dir = new bucket[HTSIZE];
    	for(int i=0;i<HTSIZE;i=i+1){
    		dir[i] = new bucket();
    	}
    }
    public FrameHashTable(int tbsize){
    	HTSIZE = tbsize;
    	dir = new bucket[HTSIZE];
    	for(int i=0;i<HTSIZE;i=i+1){
    		dir[i] = new bucket();
    	}
    }
    public void AddToDir(PageId pageid,int fid){
    	int bucketid = HashFunction(pageid.pid);
    	FPpair newpair = new FPpair(pageid.pid,fid);
    	dir[bucketid].Add(newpair);
    }
    public void DeleteFromDir(PageId pageid,int fid) {
    	if(pageid!=null){
    		int bucketid = HashFunction(pageid.pid);
    		FPpair newpair = new FPpair(pageid.pid,fid);
    		dir[bucketid].Delete(newpair);
    	}
    }
    public FPpair getPair(PageId pageid){
    	int bucketid = HashFunction(pageid.pid);
	//System.out.println(bucketid);
    	return dir[bucketid].getpair(pageid.pid);
    }
 
    
    public int HashFunction(int value){
    	return (5*value+10)%HTSIZE;
    }
}
