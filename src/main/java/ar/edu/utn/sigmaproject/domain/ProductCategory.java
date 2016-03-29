package ar.edu.utn.sigmaproject.domain;

import java.util.Arrays;

public enum ProductCategory {
	Armario,
	Biblioteca,
	Comoda,
	Cajonera,
	Cama,
	Escritorio,
	Mesa,
	Silla,
	Sillon;
    
    public static int indexOf(ProductCategory productCategory) {
        return Arrays.asList(values()).indexOf(productCategory);
    }
}
