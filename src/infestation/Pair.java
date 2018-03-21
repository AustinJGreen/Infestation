package infestation;

public class Pair<A, B> 
{
	private A ANode;
	private B BNode;
	
	public A getANode() {
		return ANode;
	}
	
	public void setANode(A aNode) {
		ANode = aNode;
	}
	
	public B getBNode() {
		return BNode;
	}
	public void setBNode(B bNode) {
		BNode = bNode;
	}
	
	public Pair(A a, B b)
	{
		this.ANode = a;
		this.BNode = b;
	}
}
