package pt.iscte.hmcgf.extractor.test.dummy;

public class A
{
	private B b;
	public A()
	{
		this.b = new B();
	}
	public A(Integer i)
	{
		this.b = new B(this);
	}
	public A(B b)
	{
		this.b = b;
	}
	public B getB()
	{
		return b;
	}
}
