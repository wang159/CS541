package bufmgr;

public class PageUnpinnedException extends PagePinnedException {
	public PageUnpinnedException(Exception e, String name)
	  { 
	    super(e, name); 
	  }
}
