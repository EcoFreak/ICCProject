package pt.iscte.hmcgf.extractor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import pt.iscte.hmcgf.extractor.relations.InstanceInInstanceMethodRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInConstructorRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInInstanceMethodRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInStaticMethodRelation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;
import pt.iscte.hmcgf.extractor.relations.Type;

public class ReflectionRelationExtractor implements RelationExtractor
{
	public static final String				NO_TYPE	= "NO_TYPE";
	private ArrayList<String>				namespaces;
	private RelationStorage					storage;
	private Multimap<Class<?>, Class<?>>	storedSuperTypes;
	private HashMap<String, Type>			storedTypes;
	private boolean							exploreSubtypes;
	private FastClasspathScanner			scanner;
	private ScanResult						scanResult;

	public ReflectionRelationExtractor(RelationStorage storage)
	{
		this.storage = storage;
	}

	@Override
	public boolean analyseClasses(String namespace, boolean exploreSubtypes)
	{
		ArrayList<String> temp = new ArrayList<>();
		temp.add(namespace);
		return analyseClasses(temp, exploreSubtypes);
	}
	@Override
	public boolean analyseClasses(List<String> namespaces, boolean exploreSubtypes)
	{
		this.namespaces = new ArrayList<>(namespaces);
		this.exploreSubtypes = exploreSubtypes;
		this.storedSuperTypes = ArrayListMultimap.create();
		this.storedTypes = new HashMap<>();
		this.storedTypes.put(NO_TYPE, new Type(NO_TYPE, false, false, false, false, false, false));
		this.storedTypes.put("void", new Type("void", false, false, false, false, false, true));
		Set<Class<?>> classes = getAllClasses();
		int counter = 0;
		for (Class<?> c : classes)
		{
			try
			{
				if (c.isAnonymousClass())
				{
					counter++;
					continue;
				}
				else if (c.getCanonicalName() == null)
				{
					counter++;
					continue;
				}
				else if (c.isAnnotation())
				{
					// ignore annotation
					continue;
				}
				handleMethods(c);
				handleConstructors(c);
			}
			catch (Throwable e)
			{
				// System.err.println(e);
				// SUPRESS WARNING WITH NOCLASSDEF ERRORS
			}
		}
		System.out.println(namespaces + ": " + counter + " classes not loaded");
		return classes.isEmpty();
	}

	private void handleConstructors(Class<?> c)
	{
		Constructor<?>[] constructors = c.getConstructors();
		for (Constructor<?> constructor : constructors)
		{
			int numValidParamsFound = 0;
			for (Class<?> paramType : constructor.getParameterTypes())
			{
				if (belongsToNamespaces(paramType.getCanonicalName()))
				{
					numValidParamsFound++;
					Type source = getTypeForClass(paramType);
					Type destination = getTypeForClass(c);
					if (source != null && destination != null)
					{
						storage.addRelation(new ParamInConstructorRelation(source, destination, false, null,
								convertParameterTypes(constructor.getParameterTypes())));
						if (exploreSubtypes)
						{
							for (Class<?> subType : getSubTypesOfClass(paramType))
							{
								Type subTypeSource = getTypeForClass(subType);
								storage.addRelation(new ParamInConstructorRelation(subTypeSource, destination, true,
										source, convertParameterTypes(constructor.getParameterTypes())));
							}
						}

					}
				}
			}
			if (numValidParamsFound == 0)
			{
				// Constructor has no parameters or no API Types are used as
				// parameters, create relationship for type NO_TYPE
				Type source = getTypeForCanonicalName(NO_TYPE);
				Type destination = getTypeForClass(c);
				if (destination != null && source != null)
					storage.addRelation(new ParamInConstructorRelation(source, destination, false, null,
							convertParameterTypes(constructor.getParameterTypes())));
			}
		}
	}

	private boolean belongsToNamespaces(String canonicalName)
	{
		for (String n : namespaces)
		{
			if (canonicalName.startsWith(n))
				return true;
		}
		return false;
	}

	private void handleMethods(Class<?> c) throws NoClassDefFoundError
	{
		Method[] methods = c.getMethods();
		for (Method method : methods)
		{

			if (method.getDeclaringClass().equals(Object.class))// || !isApiType(method.getReturnType().getCanonicalName()))
				continue;
			int numValidParamsFound = 0;
			for (Class<?> paramType : method.getParameterTypes())
			{
				if (belongsToNamespaces(method.getReturnType().getCanonicalName()) || !isApiType(method.getReturnType().getCanonicalName()))
				{
					// internal relationship
					if (belongsToNamespaces(paramType.getCanonicalName()))
					{
						// internal relationship with internal parameters
						numValidParamsFound++;
						ArrayList<Class<?>> types = new ArrayList<>();
						types.add(paramType);
						if (exploreSubtypes)
							types.addAll(getSubTypesOfClass(paramType));
						for (int i = 0; i < types.size(); i++)
						{
							Class<?> t = types.get(i);
							Type source = getTypeForClass(t);
							Type destination = getTypeForClass(method.getReturnType());
							Type intermidiary = getTypeForClass(c);
							if (source == null || destination == null || intermidiary == null)
								continue;
							if (Modifier.isStatic(method.getModifiers()))
							{
								// STATIC PARAM RELATION
								storage.addRelation(new ParamInStaticMethodRelation(source, destination, intermidiary,
										method.getName(), i > 0, ((i == 0) ? null : getTypeForClass(types.get(0))),
										convertParameterTypes(method.getParameterTypes())));
							}
							else
							{
								// NON STATIC PARAM RELATION
								storage.addRelation(new ParamInInstanceMethodRelation(source, destination, intermidiary,
										method.getName(), i > 0, ((i == 0) ? null : getTypeForClass(types.get(0))),
										convertParameterTypes(method.getParameterTypes())));
							}
						}
						// STATIC INSTANCE RELATION
						if (!Modifier.isStatic(method.getModifiers()))
							if (getTypeForClass(c) != null && getTypeForClass(method.getReturnType()) != null)
								storage.addRelation(new InstanceInInstanceMethodRelation(getTypeForClass(c),
										getTypeForClass(method.getReturnType()), getTypeForClass(c), method.getName(),
										false, null, convertParameterTypes(method.getParameterTypes())));
					}
				}
			}
			if (numValidParamsFound == 0)
			{
				// Method has no parameters or no API Types are used as
				// parameters, create relationship for type NO_TYPE
				Type source = getTypeForCanonicalName(NO_TYPE);
				Type destination = getTypeForClass(method.getReturnType());
				Type intermidiary = getTypeForClass(c);
				if (source != null && destination != null && intermidiary != null)
				{

					if (Modifier.isStatic(method.getModifiers()))
					{
						storage.addRelation(new ParamInStaticMethodRelation(source, destination, intermidiary,
								method.getName(), false, null, convertParameterTypes(method.getParameterTypes())));
					}
					else
					{
						// Static create both param and instance
						// TODO Check if create both makes sense in this case
						if (source != null && destination != null && intermidiary != null)
						{
							storage.addRelation(new ParamInInstanceMethodRelation(source, destination, intermidiary,
									method.getName(), false, null, convertParameterTypes(method.getParameterTypes())));
							storage.addRelation(new InstanceInInstanceMethodRelation(intermidiary, destination,
									intermidiary, method.getName(), false, null,
									convertParameterTypes(method.getParameterTypes())));
						}
					}
				}
			}
		}
	}

	private boolean isApiType(String type)
	{
		return type.contains(".");
	}

	private Collection<Type> convertParameterTypes(Class<?>[] params)
	{
		ArrayList<Type> typeParams = new ArrayList<>();
		for (Class<?> c : params)
		{
			Type t = getTypeForClass(c);
			if (t != null)
				typeParams.add(t);
		}
		return typeParams;
	}

	private Type getTypeForClass(Class<?> c)
	{
		return getTypeForCanonicalName(c.getCanonicalName());
	}

	private Type getTypeForCanonicalName(String canonicalName)
	{
		if (!storedTypes.containsKey(canonicalName))
		{
			Type t = null;
			String fixedName = canonicalName.replaceAll("\\[\\]", "");
			try
			{
				//This removes strange annonymous classes not being defined as so by Class, like javax.mail.Session$1
				if(canonicalName.contains("$"))
					return null;
				Class<?> c = Class.forName(fixedName);
				t = new Type(fixedName, !belongsToNamespaces(canonicalName), areAllMethodsStatic(c.getMethods()),
						c.isEnum(), Modifier.isAbstract(c.getModifiers()),
						containsMethodByName(c.getDeclaredMethods(), "equals"), c.isPrimitive());
//				for (Class subclass : getSubTypesOfClass(c))
//				{
//					Type subtype = getTypeForClass(subclass);
//					if (subtype != null)
//						t.getSubtypes().add(subtype);
//				}
				for (Class superclass : getSuperTypesOfClass(c))
				{
					Type supertype = getTypeForClass(superclass );
					if (supertype != null)
						t.getSupertypes().add(supertype);
				}
			}
			catch (Throwable e)
			{
				//System.out.println(e);
				return null;
			}
			storedTypes.put(canonicalName, t);

		}
		return storedTypes.get(canonicalName);
	}

	private static boolean areAllMethodsStatic(Method[] methods)
	{
		for (Method method : methods)
			if (Modifier.isStatic(method.getModifiers()))
				return false;
		return true;
	}

	private static boolean containsMethodByName(Method[] methods, String name)
	{
		for (Method method : methods)
		{
			if (method.getName().equals(name))
				return true;
		}
		return false;
	}

	private Collection<Class<?>> getSubTypesOfClass(Class<?> c)
	{
		if (!this.storedSuperTypes.containsKey(c))
			this.storedSuperTypes.putAll(c, scanResult.classNamesToClassRefs(scanResult.getNamesOfSubclassesOf(c), true));
		return this.storedSuperTypes.get(c);
	}

	private Collection<Class<?>> getSuperTypesOfClass(Class<?> c)
	{
		if (!this.storedSuperTypes.containsKey(c))
			this.storedSuperTypes.putAll(c, scanResult.classNamesToClassRefs(scanResult.getNamesOfSuperclassesOf(c), true));
		return this.storedSuperTypes.get(c);
	}

	private Set<Class<?>> getAllClasses()
	{
		Set<Class<?>> classes = null;
		ArrayList<String> tempList = new ArrayList<>();
		tempList.add("!!");
		tempList.addAll(this.namespaces);
		this.scanner = new FastClasspathScanner(tempList.toArray(new String[tempList.size()]));
		this.scanResult = scanner.scan();
		//System.out.println(scanResult.getNamesOfAllClasses());
		classes = new HashSet<>(scanResult.classNamesToClassRefs(scanResult.getNamesOfAllStandardClasses(), true));
		//System.out.println(namespaces + ": Found " + classes.size() + " types");
		return classes;
	}

	@Override
	public RelationStorage getRelationStorage()
	{
		return storage;
	}

}
