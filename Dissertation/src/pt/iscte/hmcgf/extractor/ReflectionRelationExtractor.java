package pt.iscte.hmcgf.extractor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
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
import pt.iscte.hmcgf.extractor.relations.InstanceInInstanceMethodRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInConstructorRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInInstanceMethodRelation;
import pt.iscte.hmcgf.extractor.relations.ParamInStaticMethodRelation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;

public class ReflectionRelationExtractor implements RelationExtractor
{
	public static final String				NO_TYPE	= "NO_TYPE";
	private RelationStorage					storage;
	private Reflections						reflectionsInstance;
	private Multimap<Class<?>, Class<?>>	storedSubTypes;
	public ReflectionRelationExtractor(RelationStorage storage)
	{
		this.storage = storage;
	}
	@Override
	public boolean analyseClasses(String wildcard)
	{
		this.reflectionsInstance = new Reflections(wildcard);
		this.storedSubTypes = ArrayListMultimap.create();
		Set<Class<?>> classes = getAllClasses(wildcard);
		for (Class<?> c : classes)
		{
			try
			{
				handleMethods(c, wildcard);
				handleConstructors(c, wildcard);
			}
			catch (NoClassDefFoundError e)
			{
				System.err.println(e);
				// SUPRESS WARNING WITH NOCLASSDEF ERRORS
			}
		}
		return classes.isEmpty();
	}
	private void handleConstructors(Class<?> c, String namespace)
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
					addConstructorRelationForAllSubTypes(paramType, c, c, constructor,
							convertParameterTypes(constructor.getParameterTypes(), namespace, true),
							convertParameterTypes(constructor.getParameterTypes(), namespace, false));
				}
			}
			if (numValidParamsFound == 0)
			{
				// Constructor has no parameters or no API Types are used as parameters, create relationship for type NO_TYPE
				storage.addRelation(
						new ParamInConstructorRelation(namespace + "." + NO_TYPE, c.getCanonicalName(), false, null,
								convertParameterTypes(constructor.getParameterTypes(), namespace, true),
								convertParameterTypes(constructor.getParameterTypes(), namespace, false)));
			}
		}
	}

	private void handleMethods(Class<?> c, String namespace) throws NoClassDefFoundError
	{
		Method[] methods = c.getMethods();
		for (Method method : methods)
		{
			if (method.getDeclaringClass().equals(Object.class) || (!method.getReturnType().getCanonicalName().startsWith(namespace)))
				continue;
			int numValidParamsFound = 0;
			for (Class<?> paramType : method.getParameterTypes())
			{
				if (paramType.getCanonicalName().startsWith(namespace))
				{
					numValidParamsFound++;
					addMethodRelationForAllSubTypes(paramType, c, method.getReturnType(), method,
							convertParameterTypes(method.getParameterTypes(), namespace, true),
							convertParameterTypes(method.getParameterTypes(), namespace, false));
				}
			}
			if (numValidParamsFound == 0)
			{
				// Method has no parameters or no API Types are used as parameters, create relationship for type NO_TYPE
				if (Modifier.isStatic(method.getModifiers()))
					storage.addRelation(
							new ParamInStaticMethodRelation(namespace + "." + NO_TYPE, method.getReturnType().getCanonicalName(),
									c.getCanonicalName(),
									method.getName(), false, null, convertParameterTypes(method.getParameterTypes(), namespace, true),
									convertParameterTypes(method.getParameterTypes(), namespace, false)));
				else
				{
					storage.addRelation(
							new ParamInInstanceMethodRelation(namespace + "." + NO_TYPE, method.getReturnType().getCanonicalName(),
									c.getCanonicalName(),
									method.getName(), false, null, convertParameterTypes(method.getParameterTypes(), namespace, true),
									convertParameterTypes(method.getParameterTypes(), namespace, false)));
					storage.addRelation(
							new InstanceInInstanceMethodRelation(c.getCanonicalName(), method.getReturnType().getCanonicalName(),
									c.getCanonicalName(), method.getName(), false, null,
									convertParameterTypes(method.getParameterTypes(), namespace, true),
									convertParameterTypes(method.getParameterTypes(), namespace, false)));

				}
			}
		}
	}
	// private String normalizeType(String canonicalName)
	// {
	// return canonicalName.replaceAll("\\[\\]", "");
	// }
	private Collection<String> convertParameterTypes(Class<?>[] params, String namespace, boolean filterNamespaceTypes)
	{
		ArrayList<String> stringParams = new ArrayList<>();
		for (Class<?> c : params)
		{
			if (!filterNamespaceTypes || c.getCanonicalName().startsWith(namespace))
				stringParams.add(c.getCanonicalName());
		}
		return stringParams;
	}
	private void addMethodRelationForAllSubTypes(Class<?> from, Class<?> intermidiary, Class<?> to, Method method,
			Collection<String> parameters, Collection<String> allParameters)
	{
		if (Modifier.isStatic(method.getModifiers()))
			storage.addRelation(new ParamInStaticMethodRelation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(),
					method.getName(), false, null, parameters, allParameters));
		else
		{
			storage.addRelation(new ParamInInstanceMethodRelation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(),
					method.getName(), false, null, parameters, allParameters));
			storage.addRelation(
					new InstanceInInstanceMethodRelation(intermidiary.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(),
							method.getName(), false, null, parameters, allParameters));

		}

		for (Class<?> subType : getSubTypesOfClass(from))
		{
			if (Modifier.isStatic(method.getModifiers()))
				storage.addRelation(new ParamInStaticMethodRelation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(),
						method.getName(), true, from.getCanonicalName(), parameters, allParameters));
			else
			{
				storage.addRelation(new ParamInInstanceMethodRelation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(),
						method.getName(), true, from.getCanonicalName(), parameters, allParameters));
				storage.addRelation(
						new InstanceInInstanceMethodRelation(intermidiary.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(),
								method.getName(), true, from.getCanonicalName(), parameters, allParameters));
			}
		}

	}
	private void addConstructorRelationForAllSubTypes(Class<?> from, Class<?> intermidiary, Class<?> to, Constructor<?> constructor,
			Collection<String> parameters, Collection<String> allParameters)
	{
		storage.addRelation(new ParamInConstructorRelation(from.getCanonicalName(), to.getCanonicalName(), false, null, parameters, allParameters));
		for (Class<?> subType : getSubTypesOfClass(from))
		{
			storage.addRelation(
					new ParamInConstructorRelation(from.getCanonicalName(), to.getCanonicalName(), true, from.getCanonicalName(), parameters,
							allParameters));
		}

	}
	private Collection<Class<?>> getSubTypesOfClass(Class<?> c)
	{
		if (!this.storedSubTypes.containsKey(c))
			this.storedSubTypes.putAll(c, reflectionsInstance.getSubTypesOf(c));
		return this.storedSubTypes.get(c);
	}
	private Set<Class<?>> getAllClasses(String wildcard)
	{
		Set<Class<?>> classes = null;
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		classLoadersList.add(ClassLoader.getSystemClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false).filterResultsBy(
						new FilterBuilder().include(Object.class.getCanonicalName())),
						new ResourcesScanner())
				// .setUrls(ClasspathHelper.forPackage(wildcard))
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
				.filterInputsBy(new FilterBuilder().includePackage(wildcard)));
		// .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(wildcard))));

		classes = reflections.getSubTypesOf(Object.class);
		System.out.println(wildcard + ": Found " + classes.size() + " types");
		return classes;
	}
	@Override
	public RelationStorage getRelationStorage()
	{
		return storage;
	}

}
