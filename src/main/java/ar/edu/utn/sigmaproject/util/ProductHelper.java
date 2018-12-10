package ar.edu.utn.sigmaproject.util;

import java.util.Arrays;
import java.util.List;

public class ProductHelper {
	
	private List<List<String>> piecesCubeSeat = Arrays.asList(
			Arrays.asList("2", "tapas frontal y posterior", "36.3", "32.1", "1.8"),
			Arrays.asList("2", "tapas laterales", "40", "32.1", "1.8"),
			Arrays.asList("1", "fondo", "40", "40", "1.8"),
			Arrays.asList("1", "tapa superior", "40", "40", "1.8"),
			Arrays.asList("2", "tacos de tapa", "32.4", "4.5", "1.8"),
			Arrays.asList("4", "tacos verticales", "30.1", "4.5", "1.8"),
			Arrays.asList("2", "tacos horizontales", "36.3", "4.5", "1.8"),
			Arrays.asList("1", "tapa", "0", "0", "0"),
			Arrays.asList("1", "cubo completo", "0", "0", "0")
			);

	private List<List<String>> suppliesCubeSeat= Arrays.asList(
			Arrays.asList("4", "ruedas"),
			Arrays.asList("48", "tornillos para madera - largo: 3 cm, di\u00e1metro: 0.4 cm"),
			Arrays.asList("16", "tornillos para madera - largo: 1.5 cm, di\u00e1metro: 0.4 cm")
			);

	private List<List<String>> piecesLibrary = Arrays.asList(
			Arrays.asList("4", "patas", "198", "4.5", "4.5"),
			Arrays.asList("12", "travesa\u00f1os frontales y posteriores", "97", "3.5", "3.5"),
			Arrays.asList("12", "travesa\u00f1o laterales", "41", "3.5", "3.5"),
			Arrays.asList("2", "tapas laterales", "198", "50", "2.5"),
			Arrays.asList("1", "tapa posterior", "198", "106", "2.5"),
			Arrays.asList("1", "tapa superior", "104", "50", "0.3"),
			Arrays.asList("5", "estantes", "104", "50", "0.55"),
			Arrays.asList("1", "biblioteca completa", "0", "0", "0")
			);

	private List<List<String>> suppliesLibrary= Arrays.asList(
			Arrays.asList("48", "tornillos - largo: 8 cm, di\u00e1metro: 0.55 cm"),
			Arrays.asList("48", "tuercas - di\u00e1metro: 0.55 cm"),
			Arrays.asList("60", "clavos finos - largo: 2.5 cm"),
			Arrays.asList("4", "tornillos para madera - largo: 5.5 cm, di\u00e1metro: 0.3 cm")
			);

	private List<List<String>> piecesStool = Arrays.asList(
			Arrays.asList("4", "patas", "43", "4", "4"),
			Arrays.asList("2", "travesa\u00f1os laterales", "12", "7", "2.5"),
			Arrays.asList("1", "travesa\u00f1o central", "22", "7", "2.5"),
			Arrays.asList("1", "tapa", "32", "32", "1.5"),
			Arrays.asList("1", "banco completo", "0", "0", "0")
			);

	private List<List<String>> suppliesStool= Arrays.asList(
			Arrays.asList("4", "tornillos para madera - largo: 3.5 cm, di\u00e1metro: 0.3 cm"),
			Arrays.asList("10", "tornillos para madera - largo: 2.5 cm, di\u00e1metro: 0.3 cm")
			);

	private List<List<String>> piecesTable = Arrays.asList(
			Arrays.asList("4", "patas", "75", "4", "4"),
			Arrays.asList("2", "travesa\u00f1os laterales", "112", "9.5", "2.5"),
			Arrays.asList("2", "travesa\u00f1os de cabecera", "72", "9.5", "2.5"),
			Arrays.asList("2", "travesa\u00f1os de refuerzo", "73.5", "9.5", "2.5"),
			Arrays.asList("8", "tacos de fijacion", "5", "2.5", "2.5"),
			Arrays.asList("1", "tabla principal", "120", "80", "1.2"),
			Arrays.asList("1", "mesa completa", "0", "0", "0")
			);

	private List<List<String>> suppliesTable= Arrays.asList(
			Arrays.asList("8", "tornillos - largo: 8 cm, di\u00e1metro: 0.55 cm"),
			Arrays.asList("8", "tuercas - di\u00e1metro: 0.55 cm"),
			Arrays.asList("8", "tornillos para madera - largo: 3.5 cm, di\u00e1metro: 0.3 cm")
			);

	private List<List<String>> piecesChair = Arrays.asList(
			Arrays.asList("2", "patas frontales", "44", "3.5", "3.5"),
			Arrays.asList("2", "patas posteriores", "79.5", "3.5", "3.5"),
			Arrays.asList("2", "travesa\u00f1os laterales", "37.3", "7", "2.5"),
			Arrays.asList("2", "travesa\u00f1os frontal y posterior", "34", "7", "2.5"),
			Arrays.asList("1", "asiento", "43.5", "41", "0.55"),
			Arrays.asList("1", "respaldo", "41", "14", "0.55"),
			Arrays.asList("1", "silla completa", "0", "0", "0")
			);

	private List<List<String>> suppliesChair= Arrays.asList(
			Arrays.asList("8", "tornillos - largo: 7 cm, di\u00e1metro: 0.55 cm"),
			Arrays.asList("8", "tuercas - di\u00e1metro: 0.55 cm"),
			Arrays.asList("8", "tornillos para madera - largo: 1.5 cm, di\u00e1metro: 0.3 cm")
			);

	private List<List<String>> piecesBed = Arrays.asList(
			Arrays.asList("2", "patas piecera", "36", "4.5", "4.5"),
			Arrays.asList("2", "patas cabecera", "59", "4.5", "4.5"),
			Arrays.asList("5", "travesa\u00f1os piecera, cabecera y frontal", "190", "9.5", "2.5"),
			Arrays.asList("2", "travesa\u00f1os", "190", "9.5", "2.5"),
			Arrays.asList("2", "soportes parilla", "182", "2.5", "2.5"),
			Arrays.asList("14", "parilla", "82", "9.5", "2.5"),
			Arrays.asList("1", "cabecera", "0", "0", "0"),
			Arrays.asList("1", "piecera", "0", "0", "0"),
			Arrays.asList("1", "parilla", "0", "0", "0")
			);

	private List<List<String>> suppliesBed= Arrays.asList(
			Arrays.asList("16", "tornillos - largo: 8 cm, di\u00e1metro: 0.55 cm"),
			Arrays.asList("16", "tuercas - di\u00e1metro: 0.55 cm"),
			Arrays.asList("42", "tornillos para madera - largo: 4 cm, di\u00e1metro: 4 mm")
			);

}
