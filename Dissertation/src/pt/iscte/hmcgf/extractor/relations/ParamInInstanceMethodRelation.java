package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;

public class ParamInInstanceMethodRelation extends Relation
{

	public ParamInInstanceMethodRelation(String source, String destination, String intermediary, String methodName, boolean requiresCast, String mainType,
			Collection<String> internalParameters, Collection<String> allParameters)
	{
		super(source, destination, intermediary, methodName, requiresCast, mainType, internalParameters, allParameters);
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}

	@Override
	public RelationType getRelationType()
	{
		return RelationType.PARAM_IN_INSTANCE_METHOD;
	}

	@Override
	public String toString()
	{
		return String.format("(instance of %s).%s(%s)", intermediary, methodName, source);
	}

	@Override
	public double calculateCost()
	{
		return 2 + internalParameters.size();
	}

}