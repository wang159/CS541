package heap;

import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.RID;
import global.PageId;
import global.Page;

import java.io.IOException;
import java.util.Arrays;

import chainexception.ChainException;

public class Tuple {
    // variables  
    byte [] tuple;

    // functions

    public Tuple() {

    }

    public Tuple(byte [] tuple_in, int r_start, int r_len) {
        tuple = Arrays.copyOfRange(tuple_in, r_start, r_start+r_len+1);
    }

    public void setTupleByteArray(byte [] tuple_in) {
        // set the tuple byte array

        tuple = tuple_in;
    }

    public byte [] getTupleByteArray() {
        // return the tuple byte array

        return tuple;
    }

    public int getLength() {
        // get the tuple length
        
        return tuple.length;
    }
}
