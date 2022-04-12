package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.LinkedList;
import java.util.Queue;
import ontology.Types;
import tools.Vector2d;

/**
 * Encapsula la información elemental de un nodo 
 * usada en los problemas de BFS
 */
public class NodoSimple {
    Vector2d coordenadas; 
    Queue<Types.ACTIONS> historialPasos;


    /** Constructor para nodo inicial 
     * coordenadas iniciales x e y 
    */
    public NodoSimple( Double x, Double y){
        this.coordenadas = new Vector2d(x, y);
        this.historialPasos = new LinkedList<Types.ACTIONS>();
        
    }

    /**Constructor  a partir de un antecesor 
     * Coordenadas a
    */
    public NodoSimple(  Double x, Double y, Queue<Types.ACTIONS> _historialPasos, Types.ACTIONS nuevo_paso){
        this.coordenadas = new Vector2d(x, y);
        this.historialPasos = _historialPasos;
        this.historialPasos.add(nuevo_paso); 
    }
}
