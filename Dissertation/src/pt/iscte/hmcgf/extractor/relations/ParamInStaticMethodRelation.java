package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.base.CaseFormat;

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

	@Override
	public double calculateCost(List<Type> types)
	{
		List<Type> filteredTypes = new ArrayList<Type>(this.getInternalParamenters());
		filteredTypes.retainAll(types);
		return 1.0 + (this.getInternalParamenters().size() - filteredTypes.size());
	}

	@Override
	public String getUsageExample()
	{
		String d = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, destination.getName());
		return String.format("%s %s = %s.%s(%d internal parameters)", destination.getName(), d, intermediary.getName(), methodName,
				this.getNumInternalParameters());
	}

}
