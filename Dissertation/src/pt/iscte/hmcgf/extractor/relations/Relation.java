package pt.iscte.hmcgf.extractor.relations;

public abstract class Relation
{
	private String			source;
	private String			intermediary;
	private String			destination;
	private boolean			isStatic;
	private RelationType	type;
	public Relation(String source, String destination, String intermediary, boolean isStatic, RelationType type)
	{
		this.source = source;
		this.destination = destination;
		this.intermediary = intermediary;
		this.isStatic = isStatic;
		this.type = type;
	}
	public enum RelationType
	{
		Method
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
		// TODO Auto-generated method stub
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
		return source.equals(r.source) && destination.equals(r.destination) && type.equals(r.type);
	}
	@Override
	public String toString()
	{
		return type.toString() + ":" + this.source + "->" + this.destination;
	}
}
