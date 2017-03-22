package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Relation
{
	protected String				source;
	protected String				intermediary;
	protected String				destination;
	protected String				methodName;
	/**
	 * mainType only used for subtype relation
	 */
	protected String				mainType;
	protected Collection<String>	internalParameters;
	protected Collection<String>	allParameters;
	protected boolean				requiresCast;

	public Relation(String source, String destination, String intermediary,
			String methodName, boolean requiresCast, String mainType,
			Collection<String> internalParameters, Collection<String> allParameters)
	{
		setSource(source);
		setDestination(destination);
		setintermediary(intermediary);
		this.methodName = methodName;
		this.requiresCast = requiresCast;
		this.internalParameters = new ArrayList<String>(internalParameters);
		this.allParameters = new ArrayList<String>(allParameters);
		this.mainType = mainType;
	}

	public abstract boolean isStatic();
	public abstract RelationType getRelationType();
	public abstract String toString();

	public void setSource(String source)
	{
		this.source = source.replaceAll("\\[\\]", "");
	}

	public void setDestination(String destination)
	{
		this.destination = destination.replaceAll("\\[\\]", "");
	}

	public void setintermediary(String intermediary)
	{
		this.intermediary = intermediary.replaceAll("\\[\\]", "");
	}

	public String getMethodName()
	{
		return this.methodName;
	}
	public boolean requiresCast()
	{
		return this.requiresCast;
	}
	public Collection<String> getInternalParamenters()
	{
		return internalParameters;
	}
	public Collection<String> getAllParamenters()
	{
		return allParameters;
	}
	public int getNumInternalParameters()
	{
		return internalParameters.size();
	}
	public int getNumAllParameters()
	{
		return allParameters.size();
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
				&& internalParameters.equals(r.internalParameters);
	}

	public abstract double calculateCost();

	public enum RelationType
	{
		PARAM_IN_STATIC_METHOD, PARAM_IN_INSTANCE_METHOD, INSTANCE_IN_INSTANCE_METHOD, PARAM_IN_CONSTRUCTOR, EXTERNAL
	}
}
