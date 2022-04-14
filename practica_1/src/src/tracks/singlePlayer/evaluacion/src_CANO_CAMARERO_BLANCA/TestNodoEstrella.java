package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.PriorityQueue;

import ontology.Types.ACTIONS;

public class TestNodoEstrella {
    public static void main(String[] args) {
        PriorityQueue<NodoEstrella> abiertos = new PriorityQueue<>();
        NodoEstrella inicial = new NodoEstrella(0,0, 5, 5);
        NodoEstrella s1 = new NodoEstrella(1, 0, 5, 5, inicial, ACTIONS.ACTION_DOWN);
        NodoEstrella s2 = new NodoEstrella(0, 1, 5, 5, inicial, ACTIONS.ACTION_UP);
        NodoEstrella s3 = new NodoEstrella(2, 0, 5, 5, s1, ACTIONS.ACTION_UP);
        NodoEstrella s4 = new NodoEstrella(0, 3, 5, 5, s1, ACTIONS.ACTION_LEFT);
        NodoEstrella s5 = new NodoEstrella(3, 0, 5, 5, s1, ACTIONS.ACTION_LEFT);
        abiertos.add(s2);
        abiertos.add(s1);
        abiertos.add(inicial);
        abiertos.add(s3);
        abiertos.add(s5);
        abiertos.add(s4);

        // El orden para que estuviera correcto debiera ser: 
        while( !abiertos.isEmpty()){
            System.out.println(abiertos.poll());
        }
    } 
}