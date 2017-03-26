package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;

public class ParamInConstructorRelation extends Relation
{

	public ParamInConstructorRelation(Type source, Type destination, boolean isImplicit, Type mainType,
			Collection<Type> allParameters)
	{
		// name of method is the name of the class
		super(source, destination, destination, destination.getName(), isImplicit, mainType, allParameters);
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
		return 1 + getNumInternalParameters();
	}

}
