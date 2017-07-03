// Declare Package
package de.uni_stuttgart.cer.generator;

// Import classes
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

// Method visitor to extract methods from java class
public class MethodVisitor extends VoidVisitorAdapter 
{
	@Override
	public void visit(MethodDeclaration n, Object arg)
	{
		ExecutionProfile ep = (ExecutionProfile) arg;
		if(ep.methodName.onlyName.equals(n.getName()))
		{
			if(ep.linesDifference == -1)
			{
				if((ep.methodProperties.startingLineNumber-n.getBeginLine()) >= 0)
				{
					ep.linesDifference = ep.methodProperties.startingLineNumber-n.getBeginLine();
					ep.sourceCode = n.toString();
				}
			}
			else if(((ep.methodProperties.startingLineNumber-n.getBeginLine()) >= 0) && ((ep.methodProperties.startingLineNumber-n.getBeginLine())<ep.linesDifference))
			{
				ep.linesDifference = ep.methodProperties.startingLineNumber-n.getBeginLine();
				ep.sourceCode = n.toString();
			}
		}
	}
}