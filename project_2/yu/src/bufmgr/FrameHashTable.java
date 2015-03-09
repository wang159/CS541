package bufmgr;



import java.util.Vector;

import global.PageId;


class bucket {

	private Vector<FPpair> buc;
	private int numitem;
	public bucket(){
		buc = new Vector<FPpair>();
		numitem = 0;
	}
	public bucket(int dirsize){
		buc = new Vector<FPpair>(dirsize); 
		numitem = 0;
	}
	public void Add(FPpair newpair){
		int addflag=0;
		for(int i = 0; i<buc.size(); i = i + 1){
			if(buc.get(i).getPage()==newpair.getPage() && buc.get(i).getFrame()==newpair.getFrame()){
				//System.out.println("I'm here");
				addflag=1;//throw new DuplicateEntryException(null,"Page exists in the buffer.");
			}
		}
		if(addflag==0){
			buc.add(newpair);
			numitem = numitem +1;
		}
	}
	public void Delete(FPpair newpair){
		for(int i = 0; i<buc.size(); i = i + 1){
			if(buc.get(i).getPage()==newpair.getPage() && buc.get(i).getFrame()==newpair.getFrame()){
				//System.out.println("I'm here. page is " + buc.get(i).getPage());
				buc.remove(i);
				numitem = numitem - 1;
				break;
			}
			
		}
		
	}
	public FPpair getpair(int pid){
		//Iterator<FPpair> i=buc.iterator();
		//System.out.println(pid);
		for (int i = 0; i<buc.size(); i = i + 1) {
        		if (buc.get(i).getPage()==pid) {
            			return buc.get(i);
      		        }
  		}
		return null;
	}
	public int itnum(){
		return numitem;
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
    		//System.out.println("bucket id is: "+bucketid);
    		FPpair newpair = new FPpair(pageid.pid,fid);
    		dir[bucketid].Delete(newpair);
    	}
    }
    public FPpair getPair(PageId pageid){
    	int bucketid = HashFunction(pageid.pid);
	//System.out.println(bucketid);
    	return dir[bucketid].getpair(pageid.pid);
    }
    
    public int getItemNum(){
    	int count = 0;
    	for(int i = 0;i < HTSIZE; i = i+1){
    		count = count + dir[i].itnum();
    	}
    	return count;
    }
    
    public int HashFunction(int value){
    	return (5*value+10)%HTSIZE;
    }
}
