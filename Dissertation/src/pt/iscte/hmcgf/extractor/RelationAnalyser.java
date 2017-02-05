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
import org.jgrapht.graph.DirectedPseudograph;

import pt.iscte.hmcgf.extractor.relations.Relation;

public class RelationAnalyser {

	public static void analiseGraph(String namespace, DirectedPseudograph<String, Relation> graph) {
		HSSFWorkbook workbook = new HSSFWorkbook();
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

		try

		{
			FileOutputStream out = new FileOutputStream(new File(namespace + ".xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static boolean containsMethodByName(Method[] methods, String name) {
		for (Method method : methods) {
			if (method.getName().equals(name))
				return true;
		}
		return false;
	}

}
