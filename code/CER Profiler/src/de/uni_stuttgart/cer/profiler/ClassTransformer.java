// Declare package
package de.uni_stuttgart.cer.profiler;

// Import classes
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

// Transformer for classes
public class ClassTransformer implements ClassFileTransformer
{
	// Variables
	public static CtClass ctTargetClass;
	public static CtMethod [] ctTargetMethod;
	
	// Transform every class byte code
	public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
	{
		// Get the byte code of class
		byte[] byteCode = classfileBuffer;
		
		// Get and filter the classname
		String[] classNameParts = className.split("/");
		String classNameDots = className.replace("/",".");
				
		// If class is not from java, sun or cer
		if (instrumentClass(classNameDots)) 
		{			
			try
			{
				// Get the current class and its all declared functions
				ClassPool classPool = ClassPool.getDefault();
				CtClass ctClass = classPool.get(classNameDots);
				CtMethod [] ctMethods = ctClass.getDeclaredMethods();
				CtConstructor [] ctConstructors = ctClass.getDeclaredConstructors();
				
				// Create file if not created before, and instrument the target function
				if(!Agent.fileCreation)
				{
					// If target function is constructor
					if(Agent.functionName.isConstructor)
					{
						
					}
					// If target function is not constructor
					else
					{
						instrumentTargetFunction(classPool); 
					}
				}
				
				// Instrument functions other then target function
				// If target function is constructor
				if(Agent.functionName.isConstructor)
				{
					
				}
				// If target function is not constructor
				else
				{
					instrumentForTargetFunction(ctMethods, ctConstructors);
				}
				
				// Convert class to byte code and detach
				byteCode = ctClass.toBytecode();
				ctClass.detach();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		// Return the byte code
		return byteCode;
	}
	
	// Instrument the target function (not constructor)
	void instrumentTargetFunction(ClassPool classPool)
	{
		try
		{
			// Get the target function name
			ctTargetClass = classPool.get(Agent.functionName.onlyClassPackageName);
			ctTargetMethod = ctTargetClass.getDeclaredMethods(Agent.functionName.onlyName);
			
			Agent.functionName.setFunctionSignature(ctTargetMethod[0].getSignature());
			Agent.functionModifier = new FunctionModifier(ctTargetMethod[0].getModifiers());
			Agent.startingLineNumber = ctTargetMethod[0].getMethodInfo().getLineNumber(0);
			Agent.createFile();
									
			// Get the total outgoing function calls and instrument every called function for outgoing function calls
			ctTargetMethod[0].instrument(
			        new ExprEditor() {
			            public void edit(MethodCall methodCall)
			                          throws CannotCompileException
			            {
			            	int outgoingFunctionCallID;
			            	if(ProfileManager.totalOutgoingDistinctFuncCalls.containsKey(methodCall.getClassName() + "." + methodCall.getMethodName()+methodCall.getSignature()))
			            	{
			            		outgoingFunctionCallID = ProfileManager.totalOutgoingDistinctFuncCalls.get(methodCall.getClassName() + "." + methodCall.getMethodName()+methodCall.getSignature());
			            	}
			            	else
			            	{
			            		outgoingFunctionCallID = ProfileManager.addTotalOutgoingFunctionCall(methodCall.getClassName() + "." + methodCall.getMethodName(),methodCall.getSignature(), false);
			            		ProfileManager.totalOutgoingDistinctFuncCalls.put(methodCall.getClassName() + "." + methodCall.getMethodName()+methodCall.getSignature(), outgoingFunctionCallID);
			            	}
			            	methodCall.replace("{de.uni_stuttgart.cer.profiler.ProfileManager.addOutgoingFunctionCall("+ outgoingFunctionCallID + "); $_ = $proceed($$); de.uni_stuttgart.cer.profiler.ProfileManager.endOutgoingFunctionCall("+ outgoingFunctionCallID + ");}");
			            }
			        });
			
			// Get the total outgoing function calls (constructors) and instrument every called function for outgoing function calls (constructors)
			ctTargetMethod[0].instrument(
			        new ExprEditor() {
			            public void edit(ConstructorCall constructorCall)
			                          throws CannotCompileException
			            {					            	
			            	int outgoingFunctionCallID;
			            	if(ProfileManager.totalOutgoingDistinctFuncCalls.containsKey(constructorCall.getClassName()+ "." +constructorCall.getMethodName()+constructorCall.getSignature()))
			            	{
			            		outgoingFunctionCallID = ProfileManager.totalOutgoingDistinctFuncCalls.get(constructorCall.getClassName()+ "." +constructorCall.getMethodName()+constructorCall.getSignature());
			            	}
			            	else
			            	{
			            		outgoingFunctionCallID = ProfileManager.addTotalOutgoingFunctionCall(constructorCall.getClassName(),constructorCall.getSignature(), true);
			            		ProfileManager.totalOutgoingDistinctFuncCalls.put(constructorCall.getClassName()+ "." +constructorCall.getMethodName()+constructorCall.getSignature(), outgoingFunctionCallID);
			            	}
			            	constructorCall.replace("{de.uni_stuttgart.cer.profiler.ProfileManager.addOutgoingFunctionCall("+ outgoingFunctionCallID + "); $_ = $proceed($$); de.uni_stuttgart.cer.profiler.ProfileManager.endOutgoingFunctionCall("+ outgoingFunctionCallID + ");}");
			            }
			        });
			
			// Get the total outgoing function calls (new expr) and instrument every called function for outgoing function calls (new expr)
			ctTargetMethod[0].instrument(
			        new ExprEditor() {
			            public void edit(NewExpr newExpr)
			                          throws CannotCompileException
			            {
			            	String funcName = newExpr.getClassName();
			            	funcName = funcName.substring(funcName.lastIndexOf(".")+1);
			            	
			            	int outgoingFunctionCallID;
			            	if(ProfileManager.totalOutgoingDistinctFuncCalls.containsKey(newExpr.getClassName()+ "." +funcName+newExpr.getSignature()))
			            	{
			            		outgoingFunctionCallID = ProfileManager.totalOutgoingDistinctFuncCalls.get(newExpr.getClassName()+ "." +funcName+newExpr.getSignature());
			            	}
			            	else
			            	{
			            		outgoingFunctionCallID = ProfileManager.addTotalOutgoingFunctionCall(newExpr.getClassName(),newExpr.getSignature(), true);
			            		ProfileManager.totalOutgoingDistinctFuncCalls.put(newExpr.getClassName()+ "." +funcName+newExpr.getSignature(), outgoingFunctionCallID);
			            	}
			            	newExpr.replace("{de.uni_stuttgart.cer.profiler.ProfileManager.addOutgoingFunctionCall("+ outgoingFunctionCallID + "); $_ = $proceed($$); de.uni_stuttgart.cer.profiler.ProfileManager.endOutgoingFunctionCall("+ outgoingFunctionCallID + ");}");
			            }
			        });
			
			// Define depth indentifier
			ctTargetMethod[0].useCflow("de.uni_stuttgart_cer.depth");
								
			// When target function is started gather the information
			ctTargetMethod[0].insertBefore("de.uni_stuttgart.cer.profiler.ProfileManager.targetFunctionStart($cflow(de.uni_stuttgart_cer.depth));");
			
			// When target function is ended gather the information
			ctTargetMethod[0].insertAfter("de.uni_stuttgart.cer.profiler.ProfileManager.targetFunctionEnd();");
			
			// When any other similar target method is started or ended
			for(int i=1; i<ctTargetMethod.length; i++)
			{
				ctTargetMethod[i].insertBefore("de.uni_stuttgart.cer.profiler.ProfileManager.similarTargetFunctionStart();");
				ctTargetMethod[i].insertAfter("de.uni_stuttgart.cer.profiler.ProfileManager.similarTargetFunctionEnd();");
			}
			
			// Call pre start
			ProfileManager.preStart();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	// Instrument other functions for target function (not constructor)
	void instrumentForTargetFunction(CtMethod [] ctMethods, CtConstructor [] ctConstructors)
	{
		try
		{
			// Instrument all declared functions of current class
			for (CtMethod ctMethod : ctMethods)
			{
				// Instrument the total function callers
				ctMethod.instrument(
				        new ExprEditor() {
				            public void edit(MethodCall methodCall)
				                          throws CannotCompileException
				            {
				               // If it is not the target function (not a recursive call)
				               if(!((ctMethod.getLongName().equals(ctTargetMethod[0].getLongName())) && ctMethod.getSignature().equals(ctTargetMethod[0].getSignature())))
				               {
				            	   // If it is calling the target function
				            	   if((methodCall.getClassName() + "." + methodCall.getMethodName()).equals(Agent.functionName.longName) && (ctTargetMethod[0].getSignature().equals(methodCall.getSignature())))
					                {
				            		   int functionCallerID;
				            		   String funcName = ctMethod.getLongName().substring(0, ctMethod.getLongName().indexOf("("));
				            		   if(ProfileManager.totalDistinctFuncCallers.containsKey(funcName+ctMethod.getSignature()))
				            		   {
						            		functionCallerID = ProfileManager.totalDistinctFuncCallers.get(funcName+ctMethod.getSignature());
				            		   }
				            		   else
				            		   {
						            		functionCallerID = ProfileManager.addTotalFunctionCaller(funcName, ctMethod.getSignature(), false);
						            		ProfileManager.totalDistinctFuncCallers.put(funcName+ctMethod.getSignature(), functionCallerID);
				            		   }
				            		   //ctMethod.insertAt(methodCall.getLineNumber(), "de.uni_stuttgart.cer.profiler.ProfileManager.addFunctionCaller("+functionCallerID+");");
				            		   methodCall.replace("{de.uni_stuttgart.cer.profiler.ProfileManager.addFunctionCaller("+ functionCallerID + "); $_ = $proceed($$);}");
					                }
				               }
				            }
				        });
			}
			
			// Instrument all declared contructors of current class
			for (CtConstructor ctConstructor : ctConstructors)
			{
				// Instrument the total function callers
				ctConstructor.instrument(
				        new ExprEditor() {
				            public void edit(MethodCall methodCall)
				                          throws CannotCompileException
				            {
				               // If it is not the target function (not a recursive call)
				               if(!((ctConstructor.getLongName().equals(ctTargetMethod[0].getLongName())) && ctConstructor.getSignature().equals(ctTargetMethod[0].getSignature())))
				               {
					               // If it is calling the target function
				            	   if((methodCall.getClassName() + "." + methodCall.getMethodName()).equals(Agent.functionName.longName) && (ctTargetMethod[0].getSignature().equals(methodCall.getSignature())))
					                {
				            		   int functionCallerID;
				            		   String consName = ctConstructor.getLongName().substring(0, ctConstructor.getLongName().indexOf("("));
				            		   if(ProfileManager.totalDistinctFuncCallers.containsKey(consName+ctConstructor.getSignature()))
				            		   {
						            		functionCallerID = ProfileManager.totalDistinctFuncCallers.get(consName+ctConstructor.getSignature());
				            		   }
				            		   else
				            		   {
						            		functionCallerID = ProfileManager.addTotalFunctionCaller(consName, ctConstructor.getSignature(), true);
						            		ProfileManager.totalDistinctFuncCallers.put(consName+ctConstructor.getSignature(), functionCallerID);
				            		   }
					                	//ctConstructor.insertAt(methodCall.getLineNumber(), "de.uni_stuttgart.cer.profiler.ProfileManager.addFunctionCaller("+functionCallerID+");");
					                	methodCall.replace("{de.uni_stuttgart.cer.profiler.ProfileManager.addFunctionCaller("+ functionCallerID + "); $_ = $proceed($$);}");
					                }
				               }
				            }
				        });
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	// Check if class needs to be instrumented or not
	boolean instrumentClass(String classNameDots)
	{			
		// If is it from cer, do not instrument
		if ((classNameDots).equals("de.uni_stuttgart.cer"))
		{
			return false;
		}
		
		// If it is java or sun class, do not instrument
		else if (classNameDots.startsWith("java.") || classNameDots.startsWith("sun.") || classNameDots.startsWith("javax."))
		{
			return false;
		}
		
		// Instrument
		else
		{
			return true;
		}
	}
}

