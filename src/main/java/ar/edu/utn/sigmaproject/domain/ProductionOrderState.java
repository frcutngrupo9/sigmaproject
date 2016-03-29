package ar.edu.utn.sigmaproject.domain;

import java.util.Arrays;

public enum ProductionOrderState {
	Generada,
	Iniciada,
	Finalizada,
	Cancelada;
    
    public static int indexOf(ProductionOrderState productionOrderState) {
        return Arrays.asList(values()).indexOf(productionOrderState);
    }
}
