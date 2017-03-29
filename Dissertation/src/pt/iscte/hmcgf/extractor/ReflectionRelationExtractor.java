package pt.iscte.hmcgf.extractor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import pt.iscte.hmcgf.extractor.relations.InstanceInInstanceMethodRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInConstructorRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInInstanceMethodRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInStaticMethodRelation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;
import pt.iscte.hmcgf.extractor.relations.Type;

public class ReflectionRelationExtractor implements RelationExtractor
{
	public static final String				NO_TYPE	= "NO_TYPE";
	private String							namespace;
	private RelationStorage					storage;
	private Reflections						reflectionsInstance;
	private Multimap<Class<?>, Class<?>>	storedSubTypes;
	private HashMap<String, Type>			storedTypes;
	private boolean							exploreSubtypes;

	public ReflectionRelationExtractor(RelationStorage storage)
	{
		this.storage = storage;
	}

	@Override
	public boolean analyseClasses(String wildcard, boolean exploreSubtypes)
	{
		this.namespace = wildcard;
		this.exploreSubtypes = exploreSubtypes;
		this.reflectionsInstance = new Reflections(wildcard);
		this.storedSubTypes = ArrayListMultimap.create();
		this.storedTypes = new HashMap<>();
		this.storedTypes.put(namespace + "." + NO_TYPE, new Type(namespace + "." + NO_TYPE, false, false, false, false, false));
		Set<Class<?>> classes = getAllClasses();
		for (Class<?> c : classes)
		{
			if(c.isAnonymousClass())
				continue;
			try
			{
				handleMethods(c);
				handleConstructors(c);
			}
			catch (NoClassDefFoundError e)
			{
				// System.err.println(e);
				// SUPRESS WARNING WITH NOCLASSDEF ERRORS
			}
		}
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
				if (paramType.getCanonicalName().startsWith(namespace))
				{
					numValidParamsFound++;
					Type source = getTypeForClass(paramType);
					Type destination = getTypeForClass(c);
					storage.addRelation(
							new ParamInConstructorRelation(source, destination, false, null, convertParameterTypes(constructor.getParameterTypes())));
					if (exploreSubtypes)
					{
						for (Class<?> subType : getSubTypesOfClass(paramType))
						{
							Type subTypeSource = getTypeForClass(subType);
							storage.addRelation(new ParamInConstructorRelation(subTypeSource, destination, true, source,
									convertParameterTypes(constructor.getParameterTypes())));
						}
					}
				}
			}
			if (numValidParamsFound == 0)
			{
				// Constructor has no parameters or no API Types are used as parameters, create relationship for type NO_TYPE
				Type source = getTypeForCanonicalName(namespace + "." + NO_TYPE);
				Type destination = getTypeForClass(c);
				storage.addRelation(
						new ParamInConstructorRelation(source, destination, false, null, convertParameterTypes(constructor.getParameterTypes())));
			}
		}
	}

	private void handleMethods(Class<?> c) throws NoClassDefFoundError
	{
		Method[] methods = c.getMethods();
		for (Method method : methods)
		{
			if (method.getDeclaringClass().equals(Object.class)
					|| !isApiType(method.getReturnType().getCanonicalName()))
				continue;
			int numValidParamsFound = 0;
			for (Class<?> paramType : method.getParameterTypes())
			{
				if (method.getReturnType().getCanonicalName().startsWith(namespace)
						&& method.getReturnType().getCanonicalName().startsWith(namespace))
				{
					// internal relationship
					if (paramType.getCanonicalName().startsWith(namespace))
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

							if (Modifier.isStatic(method.getModifiers()))
							{
								// STATIC PARAM RELATION
								storage.addRelation(
										new ParamInStaticMethodRelation(source, destination, intermidiary, method.getName(),
												i > 0, ((i == 0) ? null : getTypeForClass(types.get(0))),
												convertParameterTypes(method.getParameterTypes())));
							}
							else
							{
								// NON STATIC PARAM RELATION
								storage.addRelation(
										new ParamInInstanceMethodRelation(source, destination, intermidiary, method.getName(),
												i > 0, ((i == 0) ? null : getTypeForClass(types.get(0))),
												convertParameterTypes(method.getParameterTypes())));
							}
						}
						// STATIC INSTANCE RELATION
						if (Modifier.isStatic(method.getModifiers()))
							storage.addRelation(
									new InstanceInInstanceMethodRelation(getTypeForClass(c), getTypeForClass(method.getReturnType()),
											getTypeForClass(c), method.getName(), false, null, convertParameterTypes(method.getParameterTypes())));
					}
				}
			}
			if (numValidParamsFound == 0)
			{
				// Method has no parameters or no API Types are used as parameters, create relationship for type NO_TYPE
				Type source = getTypeForCanonicalName(namespace + "." + NO_TYPE);
				Type destination = getTypeForClass(method.getReturnType());
				Type intermidiary = getTypeForClass(c);
				if (Modifier.isStatic(method.getModifiers()))
				{
					storage.addRelation(
							new ParamInStaticMethodRelation(source, destination, intermidiary, method.getName(),
									false, null, convertParameterTypes(method.getParameterTypes())));
				}
				else
				{
					// Static create both param and instance
					// TODO Check if create both makes sense in this case
					storage.addRelation(
							new ParamInInstanceMethodRelation(source, destination, intermidiary, method.getName(),
									false, null, convertParameterTypes(method.getParameterTypes())));
					storage.addRelation(
							new InstanceInInstanceMethodRelation(intermidiary, destination, intermidiary, method.getName(),
									false, null, convertParameterTypes(method.getParameterTypes())));

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
			typeParams.add(getTypeForClass(c));
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
				Class<?> c = Class.forName(fixedName);
				t = new Type(fixedName,
						!canonicalName.startsWith(namespace),
						areAllMethodsStatic(c.getMethods()),
						c.isEnum(),
						Modifier.isAbstract(c.getModifiers()),
						containsMethodByName(c.getDeclaredMethods(), "equals"));
			}
			catch (ClassNotFoundException e)
			{
				t = new Type(fixedName, !canonicalName.startsWith(namespace), false, false, false, false);
				// TODO CHECK THIS!!!
				// e.printStackTrace();
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
		if (!this.storedSubTypes.containsKey(c))
			this.storedSubTypes.putAll(c, reflectionsInstance.getSubTypesOf(c));
		return this.storedSubTypes.get(c);
	}

	private Set<Class<?>> getAllClasses()
	{
		Set<Class<?>> classes = null;
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		classLoadersList.add(ClassLoader.getSystemClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(
						false /* don't exclude Object.class */), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
				.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(namespace))));

		//new FastClasspathScanner("!!","org.eclipse.swt").scan().getNamesOfAllStandardClasses()
		classes = reflections.getSubTypesOf(Object.class);
		System.out.println(namespace + ": Found " + classes.size() + " types");
		return classes;
	}

	@Override
	public RelationStorage getRelationStorage()
	{
		return storage;
	}

}
