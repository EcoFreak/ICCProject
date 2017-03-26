package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;

public class InstanceInInstanceMethodRelation extends Relation
{

	public InstanceInInstanceMethodRelation(Type source, Type destination, Type intermediary, String methodName, boolean isImplicit,
			Type mainType, Collection<Type> allParameters)
	{
		super(source, destination, intermediary, methodName, isImplicit, mainType, allParameters);
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}

	@Override
	public RelationType getRelationType()
	{
		return RelationType.INSTANCE_IN_INSTANCE_METHOD;
	}

	@Override
	public String toString()
	{
		return String.format("(instance of %s).%s(%s)", intermediary, methodName, source);
	}

	@Override
	public double calculateCost()
	{
		return 2 + getNumInternalParameters();
	}

}
