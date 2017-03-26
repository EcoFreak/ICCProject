package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;

public class ParamInStaticMethodRelation extends Relation
{

	public ParamInStaticMethodRelation(Type source, Type destination, Type intermediary, String methodName,
			boolean isImplicit, Type mainType, Collection<Type> allParameters)
	{
		super(source, destination, intermediary, methodName, isImplicit, mainType, allParameters);
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
		return 1.0 + this.getNumInternalParameters();
	}

	@Override
	public boolean isStatic()
	{
		return true;
	}

}
