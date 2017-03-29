package pt.iscte.hmcgf.extractor.relations;

public class Type
{
	private String	canonicalName;
	private boolean	isExternal;
	private boolean	isStatic;
	private boolean	isEnum;
	private boolean	isAbstract;
	private boolean	isValueObject;
	private boolean	isPrimitive;

	public Type(String canonicalName, boolean isExternal, boolean isStatic, boolean isEnum, boolean isAbstract, boolean isValueObject,
			boolean isPrimitive)
	{
		this.canonicalName = canonicalName;
		this.isExternal = isExternal;
		this.isStatic = isStatic;
		this.isEnum = isEnum;
		this.isAbstract = isAbstract;
		this.isValueObject = isValueObject;
		this.isPrimitive = isPrimitive;
	}

	public String getCanonicalName()
	{
		return this.canonicalName;
	}

	public String getName()
	{
		if (this.canonicalName.contains("."))
			return this.canonicalName.substring(this.canonicalName.lastIndexOf(".") + 1);
		return this.canonicalName;
	}
	// source.replaceAll("\\[\\]", "")

	public boolean IsExternal()
	{
		return this.isExternal;
	}

	public boolean IsPrimitive()
	{
		return this.isPrimitive;
	}

	public boolean IsStatic()
	{
		return this.isStatic;
	}
	public boolean IsEnum()
	{
		return this.isEnum;
	}
	public boolean IsAbstract()
	{
		return this.isAbstract;
	}

	@Override
	public String toString()
	{
		return this.canonicalName;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Type))
			return false;
		if (obj == this)
			return true;
		Type t = (Type) obj;
		return this.canonicalName.equals(t.canonicalName);
	}
	@Override
	public int hashCode()
	{
		return canonicalName.hashCode();
	}

	public boolean IsValueObject()
	{
		return isValueObject;
	}
}
