import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class GraphRelationStorage implements RelationStorage
{

	@Override
	public void addMethodRelation(IType source, IType destination, IMethod method)
	{
		System.out.println("Source: " + source.getFullyQualifiedName());
		System.out.println("Destination: " + destination.getFullyQualifiedName());
		System.out.println("Method: " + method.getElementName());
		System.out.println("----------------------------------------------------");
	}

	@Override
	public void addFieldRelation(IType source, IType destination, IField field)
	{
		System.out.println("Source: " + source.getFullyQualifiedName());
		System.out.println("Destination: " + destination.getFullyQualifiedName());
		System.out.println("Field: " + field.getElementName());
		System.out.println("----------------------------------------------------");

	}

}
