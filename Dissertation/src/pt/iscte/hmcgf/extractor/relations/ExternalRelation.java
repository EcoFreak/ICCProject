package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;

public class ExternalRelation extends Relation {

	private boolean isStatic;

	public ExternalRelation(String source, String destination, String intermediary, String methodName,
			boolean isImplicit, String mainType, Collection<String> internalParameters,
			Collection<String> allParameters, boolean isStatic) {
		super(source, destination, intermediary, methodName, isImplicit, mainType, internalParameters, allParameters);
	}

	@Override
	public boolean isStatic() {
		return this.isStatic;
	}

	@Override
	public RelationType getRelationType() {
		return RelationType.EXTERNAL;
	}

	@Override
	public String toString() {
		return "generates external type " + destination;
	}

	@Override
	public double calculateCost() {
		return (isStatic ? 1 : 2) + this.internalParameters.size();
	}

}
