package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.base.CaseFormat;

public class InstanceInInstanceMethodRelation extends Relation {

	public InstanceInInstanceMethodRelation(Type source, Type destination, Type intermediary, String methodName,
			boolean isImplicit, Type mainType, Collection<Type> allParameters) {
		super(source, destination, intermediary, methodName, isImplicit, mainType, allParameters);
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public RelationType getRelationType() {
		return RelationType.INSTANCE_IN_INSTANCE_METHOD;
	}

	@Override
	public String toString() {
		return String.format("(instance of %s).%s(%s)", intermediary, methodName, source);
	}

	@Override
	public double calculateCost() {
		return 2 + getNumInternalParameters();
	}

	@Override
	public double calculateCost(List<Type> types) {
		List<Type> filteredTypes = new ArrayList<Type>(this.getInternalParamenters());
		filteredTypes.retainAll(types);
		return 1.0 + (types.contains(this.getSource()) ? 0 : 1)
				+ (this.getInternalParamenters().size() - filteredTypes.size());
	}

	@Override
	public String getUsageExample() {
		String d = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, destination.getName());
		String s = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, source.getName());
		return String.format("%s %s = %s.%s(%s);", destination.getName(), d, s, methodName,
				this.getInternalParamentersString());
	}

}
