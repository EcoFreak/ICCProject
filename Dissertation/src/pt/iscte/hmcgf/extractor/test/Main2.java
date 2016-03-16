package pt.iscte.hmcgf.extractor.test;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import pt.iscte.hmcgf.extractor.ASTVisitorImpl;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;

public class Main2
{
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	public static void main(String[] args)
	{
		// TODO HOW TO IMPORT AST BETTER?
		// TODO USE JAR FILE INSTEAD OF SOURCE CODE
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		ASTVisitor myVisitor = new ASTVisitorImpl(new GraphRelationStorage());

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		// Loop over all projects
		for (IProject project : projects)
		{
			try
			{
				if (project.isNatureEnabled(JDT_NATURE))
				{
					IPackageFragment[] packages = JavaCore.create(project)
							.getPackageFragments();
					// parse(JavaCore.create(project));
					for (IPackageFragment mypackage : packages)
					{
						if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE)
						{
							for (ICompilationUnit unit : mypackage.getCompilationUnits())
							{
								// now create the AST for the ICompilationUnits
								CompilationUnit parse = parse(unit);
								parse.accept(myVisitor);
							}
						}

					}
				}
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
		/*
		 * parser.setSource(
		 * "public class A { int i = 9;  \n int j; \n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; public Integer test(String s){return s.length;}}"
		 * .toCharArray());
		 */
	}

	private static CompilationUnit parse(ICompilationUnit unit)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}

}
