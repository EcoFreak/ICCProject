package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.base.CaseFormat;

public class ParamInConstructorRelation extends Relation {

	public ParamInConstructorRelation(Type source, Type destination, boolean isImplicit, Type mainType,
			Collection<Type> allParameters) {
		// name of method is the name of the class
		super(source, destination, destination, destination.getName(), isImplicit, mainType, allParameters);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public RelationType getRelationType() {
		return RelationType.PARAM_IN_CONSTRUCTOR;
	}

	@Override
	public String toString() {
		return "";// return String.format("new %s(%s)", methodName,
					// destination);
	}

	@Override
	public double calculateCost() {
		return 1 + getNumInternalParameters();
	}

	@Override
	public double calculateCost(List<Type> types) {
		List<Type> filteredTypes = new ArrayList<Type>(this.getInternalParamenters());
		filteredTypes.retainAll(types);
		return 1.0 + (this.getInternalParamenters().size() - filteredTypes.size());
	}

	@Override
	public String getUsageExample() {
		String d = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, destination.getName());
		return String.format("%s %s = new %s(%s);", destination.getName(), d, destination.getName(),
				this.getInternalParamentersString());
	}

	@Override
	public double getBaseValue() {
		return 1.0;
	}

}
