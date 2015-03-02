package bufmgr;


import java.util.Iterator;
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
	public void Add(FPpair newpair) throws ChainException{
		for(Iterator<FPpair> i=buc.iterator();i.hasNext();){
			if(i.next().getPage()==newpair.getPage()){
				throw new DuplicateEntryException(null,"Page exists in the buffer.");
			}
		}
		buc.add(newpair);
	}
	public void Delete(FPpair newpair) throws ChainException{
		for(Iterator<FPpair> i=buc.iterator();i.hasNext();){
			if(i.next().getPage()==newpair.getPage()){
				buc.remove(i.next());
			}
			else{
				throw new DuplicateEntryException(null,"Page does not in the buffer.");
			}
		}
		buc.add(newpair);
	}
	public FPpair getpair(int pid){
		System.out.println(pid);
		Iterator<FPpair> i=buc.iterator();
		while(i.hasNext()){
			if(i.next().getPage()==pid){
				i.next();
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
    public void AddToDir(PageId pageid,int fid) throws ChainException{
    	int bucketid = HashFunction(pageid.pid);
    	FPpair newpair = new FPpair(pageid.pid,fid);
    	dir[bucketid].Add(newpair);
    }
    public void DeleteFromDir(PageId pageid,int fid) throws ChainException{
    	int bucketid = HashFunction(pageid.pid);
    	FPpair newpair = new FPpair(pageid.pid,fid);
    	dir[bucketid].Delete(newpair);
    }
    public FPpair getPair(PageId pageid){
    	int bucketid = HashFunction(pageid.pid);
    	return dir[bucketid].getpair(pageid.pid);
    }
    
    public int HashFunction(int value){
    	return (5*value+10)%HTSIZE;
    }
}
