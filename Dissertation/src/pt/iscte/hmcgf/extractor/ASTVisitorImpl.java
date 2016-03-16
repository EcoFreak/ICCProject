package pt.iscte.hmcgf.extractor;

import java.lang.reflect.Modifier;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import pt.iscte.hmcgf.extractor.relations.MethodRelation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;

public class ASTVisitorImpl extends ASTVisitor
{
	private RelationStorage storage;
	public ASTVisitorImpl(RelationStorage storage)
	{
		this.storage = storage;
	}

	@Override
	public boolean visit(MethodDeclaration node)
	{
		String methodName = node.getName().getFullyQualifiedName();
		if (Modifier.isPublic(node.getModifiers()))
		{
			// TODO What to do with void returns?
			// TODO
			String returnValue = "";
			if (node.isConstructor())
			{
				if (!(node.getParent() instanceof TypeDeclaration))
					return false;
				TypeDeclaration typeDeclaration = (TypeDeclaration) node.getParent();
				returnValue = typeDeclaration.getName().getFullyQualifiedName();
			}
			else
				returnValue = getFullyQualifiedNameFromType(node.getReturnType2());
			for (Object parameter : node.parameters())
			{
				if (parameter instanceof SingleVariableDeclaration)
				{
					SingleVariableDeclaration variableDeclaration = (SingleVariableDeclaration) parameter;
					String s = getFullyQualifiedNameFromType(variableDeclaration.getType());
					/* Create Method Relation */
					storage.addMethodRelation(
							new MethodRelation(s, returnValue, methodName, Modifier.isStatic(node.getModifiers()), node.isConstructor(),
									node.parameters().size()));
				}
			}
		}
		return false;
	}

	private String getFullyQualifiedNameFromType(Type type)
	{
		if (type instanceof SimpleType)
			return ((SimpleType) type).getName().getFullyQualifiedName();
		else if (type instanceof PrimitiveType)
			return ((PrimitiveType) type).toString();
		else
			return "";
	}
	// @Override
	// public boolean visit(FieldDeclaration node)
	// {
	// Object fragment = node.fragments().get(0);
	// if (fragment instanceof VariableDeclarationFragment)
	// {
	// String fieldName = ((VariableDeclarationFragment) fragment).getName().toString();
	// storage.addFieldRelation(new FieldRelation(getFullyQualifiedNameFromType(node.getType()), returnValue, fieldName,
	// Modifier.isStatic(node.getModifiers()));
	// }
	// return false;
	// }

}
