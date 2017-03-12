package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;

public class Relation
{
	private String				source;
	private String				intermediary;
	private String				destination;
	private String				methodName;
	/**
	 * mainType only used for subtype relation
	 */
	private String				mainType;
	private Collection<String>	parameters;
	private boolean				isStatic;
	private boolean				isConstructor;
	private boolean				requiresCast;

	public Relation(String source, String destination, String intermediary,
			String methodName, boolean isStatic, boolean isConstructor,
			boolean requiresCast, String mainType, Collection<String> parameters)
	{
		this.source = source;
		this.destination = destination;
		this.intermediary = intermediary;
		this.isStatic = isStatic;
		this.methodName = methodName;
		this.isConstructor = isConstructor;
		this.requiresCast = requiresCast;
		this.parameters = new ArrayList<String>(parameters);
		this.mainType = mainType;
	}

	public String getMethodName()
	{
		return this.methodName;
	}
	public boolean isConstructor()
	{
		return isConstructor;
	}
	public boolean requiresCast()
	{
		return this.requiresCast;
	}
	public Collection<String> getParamenters()
	{
		return parameters;
	}
	public int getNumParameters()
	{
		return parameters.size();
	}
	public String getSource()
	{
		return this.source;
	}
	public String getIntermediary()
	{
		return this.intermediary;
	}
	public String getDestination()
	{
		return this.destination;
	}
	public String getMainType()
	{
		return this.mainType;
	}
	public String getMainTypeName()
	{
		return (this.mainType == null) ? null : this.mainType.substring(this.mainType.lastIndexOf(".") + 1);
	}
	public String getSourceName()
	{
		return this.source.substring(this.source.lastIndexOf(".") + 1);
	}
	public String getIntermediaryName()
	{
		return this.intermediary.substring(this.intermediary.lastIndexOf(".") + 1);
	}
	public String getDestinationName()
	{
		return this.destination.substring(this.destination.lastIndexOf(".") + 1);
	}
	public boolean IsStatic()
	{
		return isStatic;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Relation))
			return false;
		if (obj == this)
			return true;
		Relation r = (Relation) obj;
		return isEquivalent(r)
				&& ((mainType == null && r.mainType == null) || (mainType != null && mainType.equals(r.mainType)))
				&& requiresCast == r.requiresCast && source.equals(r.source);
	}

	@Override
	public String toString()
	{
		String parameters = ", " + getNumParameters() + ((getNumParameters() > 1) ? " parameters" : " parameter");
		if (isConstructor())
			return "new " + getDestinationName() + "( " + getSourceName() + parameters + " )";
		if (IsStatic())
			return "static " + getIntermediaryName() + "." + getMethodName() + "( " + getSourceName() + parameters + " )";
		return "( instance of " + getIntermediaryName() + ")." + methodName + "( " + getSourceName() + parameters + " )";

	}
	/**
	 * Checks if two relations are equal or equivalent (equal except in the source type)
	 * 
	 * @param r
	 *            relation to test against
	 * @return
	 */
	public boolean isEquivalent(Relation r)
	{
		return destination.equals(r.destination) && intermediary.equals(r.intermediary)
				&& methodName.equals(r.methodName)
				&& parameters.equals(r.parameters) && isStatic == r.isStatic && isConstructor == r.isConstructor;
	}
	
	
	public double calculateCost()
	{
		double cost = 1.0;
		//include all parameters
		//TODO EXCLUDE basic tipes and remove duplicates
		cost += parameters.size();
		// account for the instance if it's required
		if(!isStatic && !isConstructor)
			cost ++;
		return cost;
	}
}
