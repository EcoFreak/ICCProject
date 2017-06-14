package pt.iscte.hmcgf.extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import pt.iscte.hmcgf.extractor.relations.Relation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;
import pt.iscte.hmcgf.extractor.relations.Type;

public class RelationAnalyser {
	public static final String DIRECTORY = "exports";

	public static void analiseGraph(String namespace, RelationStorage storage) {
		ArrayList<String> temp = new ArrayList<>();
		temp.add(namespace);
		analiseGraph(temp, storage);
	}

	public static void analiseGraph(List<String> namespaces, RelationStorage storage) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		sheetAnalysis(namespaces, storage, workbook);
		sheetRelationships(namespaces, storage, workbook);
		try {
			if (!new File(DIRECTORY).exists())
				new File(DIRECTORY).mkdir();
			FileOutputStream out = new FileOutputStream(new File(DIRECTORY + "/" + namespaces.get(0) + ".xls"));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void sheetAnalysis(List<String> namespace, RelationStorage storage, HSSFWorkbook workbook) {
		// create sheet for analysis
		HSSFSheet sheet = workbook.createSheet("INFO");
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("Type");
		cell = row.createCell(1);
		cell.setCellValue("Num incoming");
		cell = row.createCell(2);
		cell.setCellValue("Num outgoing");
		cell = row.createCell(3);
		cell.setCellValue("Num incoming (unique types)");
		cell = row.createCell(4);
		cell.setCellValue("Num outgoing (unique types)");
		cell = row.createCell(5);
		cell.setCellValue("Is Value Object?");
		cell = row.createCell(6);
		cell.setCellValue("Is Abstract Class?");
		int rownum = 1;
		for (Type vertex : storage.getAllTypes()) {
			if (!belongsToNamespaces(vertex.getCanonicalName(), namespace))
				continue;
			row = sheet.createRow(rownum++);
			int cellnum = 0;
			cell = row.createCell(cellnum++);
			cell.setCellValue(vertex.getCanonicalName());
			cell = row.createCell(cellnum++);
			cell.setCellValue(storage.getIncomingRelationsForType(vertex).size());
			cell = row.createCell(cellnum++);
			cell.setCellValue(storage.getOutgoingRelationsForType(vertex).size());
			cell = row.createCell(cellnum++);
			cell.setCellValue(RelationStorage
					.getUniqueIncomingTypesForRelationshipSet(storage.getIncomingRelationsForType(vertex)).size());
			cell = row.createCell(cellnum++);
			cell.setCellValue(RelationStorage
					.getUniqueOutgoingTypesForRelationshipSet(storage.getOutgoingRelationsForType(vertex)).size());
			cell = row.createCell(cellnum++);
			cell.setCellValue(vertex.IsValueObject());
			cell = row.createCell(cellnum++);
			cell.setCellValue(vertex.IsAbstract());
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);
		sheet.setAutoFilter(new CellRangeAddress(0, rownum - 1, 0, 6));
	}

	private static void sheetRelationships(List<String> namespace, RelationStorage storage, HSSFWorkbook workbook) {
		// create sheet for relationship extraction
		HSSFSheet sheet = workbook.createSheet("Relationships");
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("Incoming type");
		cell = row.createCell(1);
		cell.setCellValue("Outgoing type");
		cell = row.createCell(2);
		cell.setCellValue("Method");
		cell = row.createCell(3);
		cell.setCellValue("Num Params (internal)");
		cell = row.createCell(4);
		cell.setCellValue("Num Params (total)");
		cell = row.createCell(5);
		cell.setCellValue("Num Outgoing for outgoing type");
		cell = row.createCell(6);
		cell.setCellValue("Relation Type");
		cell = row.createCell(7);
		cell.setCellValue("Intermediary type");
		cell = row.createCell(8);
		cell.setCellValue("Is Implicit?");
		cell = row.createCell(9);
		cell.setCellValue("Parent type");
		cell = row.createCell(10);
		cell.setCellValue("Estimated cost");
		// iterate nodes for relationships
		int rownum = 1;
		for (Type vertex : storage.getAllTypes()) {
			if (!belongsToNamespaces(vertex.getCanonicalName(), namespace))
				continue;
			for (Relation relation : storage.getOutgoingRelationsForType(vertex)) {
				row = sheet.createRow(rownum++);
				int cellnum = 0;
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getSource().getCanonicalName());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getDestination().getCanonicalName());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getMethodName());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getNumInternalParameters());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getNumAllParameters());
				cell = row.createCell(cellnum++);
				cell.setCellValue(RelationStorage.getUniqueOutgoingTypesForRelationshipSet(
						storage.getOutgoingRelationsForType(relation.getDestination())).size());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getRelationType().toString());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getIntermediary().getCanonicalName());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.isImplicit());
				cell = row.createCell(cellnum++);
				cell.setCellValue((relation.isImplicit() ? relation.getMainType().getCanonicalName() : ""));
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.calculateCost());
				cell = row.createCell(cellnum++);
			}
		}
		sheet.setAutoFilter(new CellRangeAddress(0, rownum - 1, 0, 10));
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
	}

	private static boolean belongsToNamespaces(String canonicalName, List<String> namespaces) {
		if (canonicalName.equals(ReflectionRelationExtractor.NO_TYPE))
			return true;
		for (String n : namespaces) {
			if (canonicalName.startsWith(n))
				return true;
		}
		return false;
	}

}
