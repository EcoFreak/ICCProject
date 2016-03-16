package pt.iscte.hmcgf.extractor.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFrame;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import pt.iscte.hmcgf.extractor.ASTVisitorImpl;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;

public class Main
{

	public static void main(String[] args) throws IOException
	{
		GraphRelationStorage storage = new GraphRelationStorage();
		ASTVisitor myVisitor = new ASTVisitorImpl(storage);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		Path p = new File("C:\\Users\\Henrique\\git\\ICCProject\\Dissertation\\src\\pt\\iscte\\hmcgf\\extractor\\test\\TestClass.java").toPath();
		parser.setSource(new String(Files.readAllBytes(p)).toCharArray());
		CompilationUnit ci = (CompilationUnit) parser.createAST(null);
		ci.accept(myVisitor);
		System.out.println(storage.getRelationsForType("String"));
		JGraph jgraph = new JGraph(new JGraphModelAdapter(storage.getGraph()));
		JFrame jframe = new JFrame("teste");
		jframe.setSize(300, 300);
		jframe.getContentPane().add(jgraph);
		jframe.setVisible(true);
	}

}
