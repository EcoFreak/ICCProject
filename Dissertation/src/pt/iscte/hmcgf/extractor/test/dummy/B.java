package pt.iscte.hmcgf.extractor.test.dummy;

public class B
{
	private int something;
	public B()
	{
		something = 3;
	}
	public B(A a)
	{
		something = 1;
	}
	public static C getCFromA(A a)
	{
		return new C();
	}
	public A getA()
	{
		return null;
	}
	public int getSomething()
	{
		return this.something;
	}
}
