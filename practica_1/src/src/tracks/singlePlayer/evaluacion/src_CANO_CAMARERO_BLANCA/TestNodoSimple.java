package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import ontology.Types.ACTIONS;

public class TestNodoSimple {
    public static void main(String[] args) {
        // Creamos nodo con origen 
        NodoSimple inicial = new  NodoSimple(0,1); 
        NodoSimple sucesor = new NodoSimple(1,1,inicial.historialPasos, ACTIONS.ACTION_DOWN );
        NodoSimple ss = new NodoSimple(3, 4, sucesor.historialPasos, ACTIONS.ACTION_LEFT);
        System.out.println(ss.historialPasos);
    }

}
