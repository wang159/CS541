package bufmgr;

public class FPpair{

		private int page;
		private int frame;
		public FPpair(){}
		public FPpair(int p, int f){
			page = p;
			frame = f;
		}
		public int getPage(){
			return page;
		}
		public int getFrame(){
			return frame;
		}

}
