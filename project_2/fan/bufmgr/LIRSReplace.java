/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bufmgr;
import java.util.*;

/**
 *
 * @author Fan Chen
 */
public class LIRSReplace {
    ArrayList _replaceFrameList = new ArrayList();
    int _numBufs;
    ArrayList _ReuseDistance = new ArrayList();
    ArrayList _Recency = new ArrayList();
    ArrayList _numPins = new ArrayList();
    
    public LIRSReplace(int numBufs) {
        _numBufs = numBufs;
        //System.out.print("size of buffer is"+_numBufs+"\n");
        //_ReuseDistance = new ArrayList(100);
        //_ReuseDistance.set(0, 33);
        //System.out.print("size of array is"+_ReuseDistance.size()+"\n");
    }
    public void addToList(int FrameID)
    {
        _replaceFrameList.add(FrameID);
    }
    public void removeFromList(int FrameID)
    {
        for(int ii = 0; ii<_replaceFrameList.size(); ii++)
        {
            if((int)_replaceFrameList.get(ii) == FrameID)
            {
                _replaceFrameList.remove(ii);
                break;
            }
        }
    }
 
    public int getFirstReplaceFrame()
    {
        int _return_frame = -1;
        if(_replaceFrameList.isEmpty()) return -1;
        else 
        {
            int old_max_weight = 0;
            int max_weight = 0;
            
            for(int ii=0;ii<_numBufs;ii++)
            {
                //choose the frame with highest LIRS weight
                int _this_Rencency = (int)_Recency.get(ii);
                int _this_ReuseDistance = (int)_ReuseDistance.get(ii);
                if ( _this_ReuseDistance <= _this_Rencency ) 
                {
                    max_weight = _this_Rencency;
                }
                else 
                {
                    max_weight = _this_ReuseDistance;
                }
                if (max_weight>old_max_weight)
                {
                    old_max_weight = max_weight;
                    _return_frame = ii;
                }
            }
        }
        return _return_frame;
    }
    public void initializeLIRS()
    {
        //Assume FrameID starts from 1 to _numBufs

        for(int ii=0; ii<_numBufs; ii++)
        {
            //System.out.print(_ReuseDistance.size());
            _ReuseDistance.add(ii,-1);
            _Recency.add(ii,0);
            _numPins.add(ii,0);
            _replaceFrameList.add(ii,ii); 
        //Frame will be added to replacement from begining
        }
    }
    public void setLIRS (int FrameID)
    {
        //all the frame that is not called has their recency plus one
        for (int ii=0; ii<_numBufs; ii++)
        {
            if(ii != FrameID) 
            {
               int _this_recency = (int) _Recency.get(ii); 
               _this_recency++;
               _Recency.set(ii,_this_recency);
            }                
        }
        //when a Frame is called, the recency number become reusedistance, and the recency number for this frame is set to be zero
        int _this_numPins = (int) _numPins.get(FrameID);
        _this_numPins++;
        if(_this_numPins>1) _ReuseDistance.set(FrameID,_Recency.get(FrameID));
        _Recency.set(FrameID,0);
        if(_this_numPins==1) _replaceFrameList.add(FrameID); 
        //Frame will be added when the first time called
        

    }
    public int getReuseDistance (int FrameID)
    {
        return (int) _ReuseDistance.get(FrameID);
    }
    
    public int getRecency(int FrameID)
    {
        return (int) _Recency.get(FrameID);
    }


}
