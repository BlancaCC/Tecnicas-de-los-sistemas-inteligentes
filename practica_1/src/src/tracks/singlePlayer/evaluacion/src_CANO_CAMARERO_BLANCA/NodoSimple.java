package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.LinkedList;
import java.util.Queue;
import ontology.Types;

/**
 * Encapsula la información elemental de un nodo 
 * usada en los problemas de BFS
 */
public class NodoSimple {
    int x;
    int y; 
    Queue<Types.ACTIONS> historialPasos;


    /** Constructor para nodo inicial 
     * coordenadas iniciales x e y 
    */
    public NodoSimple( int _x, int _y){
        this.x = _x;
        this.y = _y;
        this.historialPasos = new LinkedList<Types.ACTIONS>();
        
    }

    /**Constructor  a partir de un antecesor 
     * Coordenadas a
    */
    public NodoSimple( int _x, int _y, Queue<Types.ACTIONS> _historialPasos, Types.ACTIONS nuevo_paso){
        this.x = _x;
        this.y = _y;
        this.historialPasos = _historialPasos;
        this.historialPasos.add(nuevo_paso); 
    }
}
