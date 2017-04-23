package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.CaseFormat;

public abstract class Relation {
	protected Type source;
	protected Type intermediary;
	protected Type destination;
	protected String methodName;
	/**
	 * mainType only used for subtype relation
	 */
	protected Type mainType;
	protected Collection<Type> allParameters;
	protected List<Type> internalParameters;
	protected boolean isImplicit;

	public Relation(Type source, Type destination, Type intermediary, String methodName, boolean isImplicit,
			Type mainType, Collection<Type> allParameters) {
		this.source = source;
		this.destination = destination;
		this.intermediary = intermediary;
		this.methodName = methodName;
		this.isImplicit = isImplicit;
		this.allParameters = new ArrayList<Type>(allParameters);
		this.internalParameters = new ArrayList<>();
		initializeInternalParameters();
		this.mainType = mainType;
	}

	public abstract boolean isStatic();

	public abstract RelationType getRelationType();

	public abstract String toString();

	public boolean isImplicit() {
		return this.isImplicit;
	}

	private void initializeInternalParameters() {
		for (Type type : allParameters) {
			if (!type.IsEnum() && !type.IsPrimitive() && !type.IsExternal())
				internalParameters.add(type);
		}
	}

	public Collection<Type> getInternalParamenters() {
		return internalParameters;
	}

	public String getInternalParamentersString() {
		String s = "";
		for (int i = 0; i < this.internalParameters.size(); i++) {
			s += CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this.internalParameters.get(i).getName());
			if ((i + 1) < this.internalParameters.size())
				s += ", ";
		}
		return s;
	}

	public Collection<Type> getAllParamenters() {
		return allParameters;
	}

	public int getNumInternalParameters() {
		return getInternalParamenters().size();
	}

	public int getNumAllParameters() {
		return allParameters.size();
	}

	public Type getSource() {
		return this.source;
	}

	public Type getIntermediary() {
		return this.intermediary;
	}

	public Type getDestination() {
		return this.destination;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public Type getMainType() {
		return this.mainType;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Relation))
			return false;
		if (obj == this)
			return true;
		Relation r = (Relation) obj;
		return isEquivalent(r)
				&& ((mainType == null && r.mainType == null) || (mainType != null && mainType.equals(r.mainType)))
				&& isImplicit == r.isImplicit && source.equals(r.source);
	}

	/**
	 * Checks if two relations are equal or equivalent (equal except in the
	 * source type)
	 * 
	 * @param r
	 *            relation to test against
	 * @return
	 */
	public boolean isEquivalent(Relation r) {
		return destination.equals(r.destination) && intermediary.equals(r.intermediary)
				&& methodName.equals(r.methodName);
	}

	public abstract double calculateCost();

	public abstract double calculateCost(List<Type> types);

	public abstract String getUsageExample();

	public enum RelationType {
		PARAM_IN_STATIC_METHOD, PARAM_IN_INSTANCE_METHOD, INSTANCE_IN_INSTANCE_METHOD, PARAM_IN_CONSTRUCTOR, EXTERNAL
	}
}
