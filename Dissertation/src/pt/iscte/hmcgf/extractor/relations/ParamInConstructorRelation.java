package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;

public class ParamInConstructorRelation extends Relation
{

	public ParamInConstructorRelation(String source, String destination, boolean isImplicit, String mainType,
			Collection<String> internalParameters, Collection<String> allParameters)
	{
		super(source, destination, destination, destination, isImplicit, mainType, internalParameters, allParameters);
	}

	@Override
	public boolean isStatic()
	{
		return true;
	}

	@Override
	public RelationType getRelationType()
	{
		return RelationType.PARAM_IN_CONSTRUCTOR;
	}

	@Override
	public String toString()
	{
		return String.format("new %s(%s)", methodName, destination);
	}

	@Override
	public double calculateCost()
	{
		return 1 + internalParameters.size();
	}

}
