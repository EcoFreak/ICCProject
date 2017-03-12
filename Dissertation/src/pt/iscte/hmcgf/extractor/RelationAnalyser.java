package pt.iscte.hmcgf.extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jgrapht.graph.DirectedPseudograph;
import pt.iscte.hmcgf.extractor.relations.Relation;

public class RelationAnalyser {

	public static void analiseGraph(String namespace, DirectedPseudograph<String, Relation> graph) {

		HSSFWorkbook workbook = new HSSFWorkbook();
		sheetAnalysis(namespace, graph, workbook);
		sheetRelationships(namespace, graph, workbook);

		try {
			FileOutputStream out = new FileOutputStream(new File(namespace + ".xls"));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void sheetAnalysis(String namespace, DirectedPseudograph<String, Relation> graph,
			HSSFWorkbook workbook) {
		// create sheet for analysis
		HSSFSheet sheet = workbook.createSheet(namespace);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("Type");
		cell = row.createCell(1);
		cell.setCellValue("Num incoming");
		cell = row.createCell(2);
		cell.setCellValue("Num outgoing");
		cell = row.createCell(3);
		cell.setCellValue("Is Value Object?");
		cell = row.createCell(4);
		cell.setCellValue("Is Abstract Class?");
		int rownum = 1;
		for (String vertex : graph.vertexSet()) {
			if (!vertex.startsWith(namespace))
				continue;
			row = sheet.createRow(rownum++);
			int cellnum = 0;
			cell = row.createCell(cellnum++);
			cell.setCellValue(vertex);
			cell = row.createCell(cellnum++);
			cell.setCellValue(graph.incomingEdgesOf(vertex).size());
			cell = row.createCell(cellnum++);
			cell.setCellValue(graph.outgoingEdgesOf(vertex).size());
			cell = row.createCell(cellnum++);
			try {
				cell.setCellValue(containsMethodByName(Class.forName(vertex).getDeclaredMethods(), "equals"));
			} catch (SecurityException | ClassNotFoundException e) {
				cell.setCellValue("FALSE");
			}
			cell = row.createCell(cellnum++);
			try {
				cell.setCellValue(Modifier.isAbstract(Class.forName(vertex).getModifiers()));
			} catch (SecurityException | ClassNotFoundException e) {
				cell.setCellValue("FALSE");
			}
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.setAutoFilter(new CellRangeAddress(0, rownum - 1, 0, 4));
	}

	private static void sheetRelationships(String namespace, DirectedPseudograph<String, Relation> graph,
			HSSFWorkbook workbook) {
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
		cell.setCellValue("Num Params");
		cell = row.createCell(4);
		cell.setCellValue("Intermediary type");
		cell = row.createCell(5);
		cell.setCellValue("Estimated cost");
		// iterate nodes for relationships
		int rownum = 1;
		for (String vertex : graph.vertexSet()) {
			if (!vertex.startsWith(namespace))
				continue;
			for (Relation relation : graph.outgoingEdgesOf(vertex)) {
				row = sheet.createRow(rownum++);
				int cellnum = 0;
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getSource());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getDestination());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getMethodName());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getNumParameters());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.getIntermediary());
				cell = row.createCell(cellnum++);
				cell.setCellValue(relation.calculateCost());
				cell = row.createCell(cellnum++);
			}
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.setAutoFilter(new CellRangeAddress(0, rownum - 1, 0, 5));
	}

	private static boolean containsMethodByName(Method[] methods, String name) {
		for (Method method : methods) {
			if (method.getName().equals(name))
				return true;
		}
		return false;
	}

}
