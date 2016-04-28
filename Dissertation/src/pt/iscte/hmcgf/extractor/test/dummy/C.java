package pt.iscte.hmcgf.extractor.test.dummy;

public class C
{
	private int something;
	public C()
	{
		something = 3;
	}
	public static C getInstance()
	{
		return new C();
	}
	public static B getBInstance()
	{
		return new B();
	}
	public static B getBInstance(C c)
	{
		return new B();
	}

	public static B getBInstance(A c)
	{
		return new B();
	}

	public int getAInt(D c)
	{
		return 3;
	}
	public int getSomething()
	{
		return this.something;
	}
	public void userTheForce(C c, A a)
	{
		
	}
}
