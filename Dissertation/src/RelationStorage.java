import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public interface RelationStorage
{
	public void addMethodRelation(IType source, IType destination,IMethod method);
	public void addFieldRelation(IType source, IType destination,IField method);
}
