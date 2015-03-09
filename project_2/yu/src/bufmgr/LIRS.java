package bufmgr;


import global.PageId;


public class LIRS {
	private final int INF = 9999;
	private int FrNum =0;
	private static int GlobalOpId;
	private int[][] frameOpIdPair;
	public LIRS(int numfrms){
		GlobalOpId = 0;
		FrNum = numfrms;
		frameOpIdPair = new int[FrNum][2];
		for(int i =0;i<FrNum;i=i+1){
			frameOpIdPair[i][0] = 0;
			frameOpIdPair[i][1] = -INF;
		}
	}
	public void increGlobalOpId(){
		GlobalOpId = GlobalOpId + 1;
	}
	public void clearGlobalOpId(){
		GlobalOpId = 0;
	}
	public void setFrameOpId(int frameId){
		if(frameId>=0 && frameId<=FrNum){
			frameOpIdPair[frameId][0] = frameOpIdPair[frameId][1];
			frameOpIdPair[frameId][1] = GlobalOpId;
		}
		else{
			// throw an exception
		} 
	}
	public void clearFrameOpId(int frameId){
		
		if(frameId>=0 && frameId<=FrNum){
			frameOpIdPair[frameId][0] = 0;
			frameOpIdPair[frameId][1] = 0;
		}
		else{
			// throw an exception
		}
	}
    public int reuse_dis(int frameId){
    	return (frameOpIdPair[frameId][1]-frameOpIdPair[frameId][0]);
    }
    public int recency(int frameId){
		return GlobalOpId - frameOpIdPair[frameId][1];
    }
    public int result(int fid){
    	return (reuse_dis(fid)>recency(fid)?reuse_dis(fid):recency(fid));
    }
} 
