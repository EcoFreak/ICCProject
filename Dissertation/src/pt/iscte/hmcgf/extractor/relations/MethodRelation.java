package pt.iscte.hmcgf.extractor.relations;

public class MethodRelation extends Relation
{
	private String	methodName;
	private boolean	isConstructor;
	private int		numParamenters;
	public MethodRelation(String source, String destination, String methodName, boolean isStatic, boolean isConstructor, int numParameters)
	{
		super(source, destination, isStatic, RelationType.Method);
		this.methodName = methodName;
		this.isConstructor = isConstructor;
		this.numParamenters = numParameters;
	}
	public String getMethodName()
	{
		return this.methodName;
	}
	public boolean isConstructor()
	{
		return isConstructor;
	}
	public int getNumParamenters()
	{
		return numParamenters;
	}
	@Override
	public String toString()
	{
		return ((IsStatic() ? "static " : "")) +
				((isConstructor() ? "new " : "")) +
				methodName + "( " + numParamenters + ((numParamenters > 1) ? " parameters" : " parameter") + " )";
	}
}