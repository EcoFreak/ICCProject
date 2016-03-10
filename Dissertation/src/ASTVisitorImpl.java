import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

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
		for (Object parameter : node.parameters())
		{
			System.out.println(parameter);
		}		
		return true;
	}
}
