package pt.iscte.hmcgf.extractor.relations;
public class FieldRelation extends Relation
{
	private String fieldName;
	public FieldRelation(String source, String destination, boolean isStatic, String fieldName)
	{
		super(source, destination, isStatic, RelationType.Field);
		this.fieldName = fieldName;
	}
	public String getFieldName()
	{
		return this.fieldName;
	}
}
