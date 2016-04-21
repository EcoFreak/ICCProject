package pt.iscte.hmcgf.extractor.relations;

public class MethodRelation extends Relation
{
	private String	methodName;
	private boolean	isConstructor;
	private int		numParamenters;
	public MethodRelation(String source, String destination, String intermediary, String methodName, boolean isStatic, boolean isConstructor,
			int numParameters)
	{
		super(source, destination, intermediary, isStatic, RelationType.Method);
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
		String parameters = ", " + numParamenters + ((numParamenters > 1) ? " parameters" : " parameter");
		if (isConstructor())
			return "new " + getDestinationName() + "( " + getSourceName() + parameters + " )";
		if (IsStatic())
			return "static " + getIntermediaryName() + "." + getMethodName() + "( " + getSourceName() + parameters + " )";
		return "( instance of " + getIntermediaryName() + ")." + methodName + "( " + getSourceName() + parameters + " )";

	}
}
