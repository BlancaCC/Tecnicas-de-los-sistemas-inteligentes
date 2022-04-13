package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.ArrayList;
import java.util.List;

import ontology.Types.ACTIONS;

public class TestNodoSimple {
    public static void main(String[] args) {
        // Creamos nodo con origen 
        NodoSimple inicial = new  NodoSimple(0,1); 
        NodoSimple sucesor = new NodoSimple(1,11,inicial.historialPasos, ACTIONS.ACTION_DOWN );
        NodoSimple ss = new NodoSimple(3,2, sucesor.historialPasos, ACTIONS.ACTION_LEFT);
        System.out.println(ss.historialPasos);
        System.out.format("The coordinates are x: %f , y: %f",ss.x, ss.y); 

        ArrayList<ArrayList<Integer>> posiciones = new ArrayList<>();
        posiciones.add(new ArrayList<>(List.of(0,-1))); // arriba 
        posiciones.add(new ArrayList<>(List.of(0,1))); // abajo 
        posiciones.add(new ArrayList<>(List.of(-1,0))); // izquierda 
        posiciones.add(new ArrayList<>(List.of(1,0))); // derecha
        System.out.println(posiciones);
        System.out.println(posiciones.get(0).get(1));

        
    }

}
