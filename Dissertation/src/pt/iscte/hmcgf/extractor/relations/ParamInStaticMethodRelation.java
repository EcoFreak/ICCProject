package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;

public class ParamInStaticMethodRelation extends Relation
{

	public ParamInStaticMethodRelation(String source, String destination, String intermediary, String methodName,
			boolean isImplicit, String mainType, Collection<String> internalParameters, Collection<String> allParameters)
	{
		super(source, destination, intermediary, methodName, isImplicit, mainType, internalParameters, allParameters);
	}

	@Override
	public RelationType getRelationType()
	{
		return RelationType.PARAM_IN_STATIC_METHOD;
	}

	@Override
	public String toString()
	{
		return String.format("%s.%s(%s)", intermediary, methodName, source);
	}

	@Override
	public double calculateCost()
	{
		return 1.0 + this.internalParameters.size();
	}

	@Override
	public boolean isStatic()
	{
		return true;
	}

}
