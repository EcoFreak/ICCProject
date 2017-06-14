package pt.iscte.hmcgf.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import pt.iscte.hmcgf.extractor.relations.Relation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;
import pt.iscte.hmcgf.extractor.relations.Type;

public class RelationSimulator {
	private static final int NUM_SUGGESTIONS = 20;
	private List<Type> scope;
	private Type lastTypeAdded;
	private boolean chooseFirst;

	public RelationSimulator(boolean chooseFirst) {
		this.chooseFirst = chooseFirst;
		scope = new ArrayList<>();
	}

	public List<Type> getScope() {
		return scope;
	}

	public void simulate(String namespace, RelationStorage storage, int numLines) {
		ArrayList<String> temp = new ArrayList<>();
		temp.add(namespace);
		simulate(temp, storage, numLines);
	}

	public void simulate(List<String> namespaces, RelationStorage storage, int numLines) {
		// step 1. get all no cost relations, sort by outgoing types (unique)
		GainCalculator<Relation> calculator = new SimpleGainCalulator(storage);
		ReflectionSimulatorComparator comparator = new ReflectionSimulatorComparator(storage, calculator);
		RelationWithNoCost noCostFilter = new RelationWithNoCost(scope);
		NewTypeProduction newTypeFilter = new NewTypeProduction(scope);
		Collection<Relation> allRelations = storage.getAllRelationsInNamespace(namespaces);
		// Clone collection
		List<Relation> filtered = new ArrayList<>(allRelations);
		// filter collection
		filterRelations(filtered, noCostFilter);
		Collections.sort(filtered, comparator);
		processRelations(filtered);
		// step 2. with the produced type get relations also sorted by outgoing
		// types (unique)
		for (int i = 1; i < numLines; i++) {
			System.out.println(scope);
			calculator.setScope(scope);
			List<Relation> auxList = new ArrayList<>(allRelations);
			// List<Relation> auxList =
			// storage.getOutgoingRelationsForType(lastTypeAdded);
			filterRelations(auxList, noCostFilter);
			filterRelations(auxList, newTypeFilter);
			Collections.sort(auxList, comparator);
			processRelations(auxList);
		}

	}

	private void processRelations(List<Relation> relations) {
		Relation r = null;
		int choice = -1;
		if (chooseFirst)
			r = relations.get(0);
		else {
			do {
				for (int i = 0; i < NUM_SUGGESTIONS && i < relations.size(); i++)
					System.out.println((i + 1) + ": " + relations.get(i).getUsageExample());
				System.out.print("Choice: ");
				try {
					choice = Integer.parseInt(new Scanner(System.in).nextLine());
				} catch (NumberFormatException e) {
					choice = -1;
				}
			} while (choice < 1 || choice > NUM_SUGGESTIONS || choice >= relations.size());
		}
		r = relations.get(choice - 1);
		Type t = r.getDestination();
		processTypeForStack(t);
		lastTypeAdded = t;
		System.out.println(r.getUsageExample());
	}

	private void processTypeForStack(Type t) {
		if (!scope.contains(t))
			scope.add(t);

		for (Type subtype : t.getSupertypes()) {
			if (!scope.contains(subtype))
				scope.add(subtype);
		}

	}

	public static void filterRelations(List<Relation> col, Filter<Relation> filter) {
		for (Iterator<Relation> i = col.iterator(); i.hasNext();) {
			Relation obj = i.next();
			if (filter.filter(obj)) {
				i.remove();
			}
		}
	}

	public Type getLastTypeAdded() {
		return lastTypeAdded;
	}

	public class ReflectionSimulatorComparator implements Comparator<Relation> {
		private RelationStorage storage;
		private GainCalculator<Relation> calculator;

		public ReflectionSimulatorComparator(RelationStorage storage, GainCalculator<Relation> calculator) {
			this.storage = storage;
			this.calculator = calculator;
		}

		@Override
		public int compare(Relation r1, Relation r2) {

			return (int) ((calculator.calculateGainForRelation(r2) * r2.getBaseGainValue())
					- (calculator.calculateGainForRelation(r1) * r1.getBaseGainValue()));
		}
	}

	public class RelationWithNoCost implements Filter<Relation> {

		private List<Type> scope;

		public RelationWithNoCost(List<Type> scope) {
			this.scope = scope;
		}

		@Override
		public boolean filter(Relation t) {
			return t.calculateCost(scope) != 1;
		}

	}

	public class NewTypeProduction implements Filter<Relation> {

		private List<Type> scope;

		public NewTypeProduction(List<Type> scope) {
			this.scope = scope;
		}

		@Override
		public boolean filter(Relation t) {
			return scope.contains(t.getDestination());
		}

	}

	public interface Filter<R extends Relation> {
		public boolean filter(R r);
	}

	public interface GainCalculator<R extends Relation> {
		public double calculateGainForRelation(R r);

		public void setScope(List<Type> scope);
	}

	public class SimpleGainCalulator implements GainCalculator<Relation> {
		private RelationStorage storage;
		private List<Type> scope;

		public SimpleGainCalulator(RelationStorage storage) {
			this.storage = storage;
			this.scope = new ArrayList<>();
		}

		public void setScope(List<Type> scope) {
			this.scope = new ArrayList<>(scope);
		}

		@Override
		public double calculateGainForRelation(Relation r) {
			List<Type> auxScope = new ArrayList<>(scope);
			auxScope.retainAll(r.getInternalParamenters());
			return (RelationStorage
					.getUniqueOutgoingTypesForRelationshipSet(storage.getOutgoingRelationsForType(r.getDestination()))
					.size() * r.getNewTypeGainValue()) + (auxScope.size() * r.getUsingTypeGainValue());
		}

	}
}
