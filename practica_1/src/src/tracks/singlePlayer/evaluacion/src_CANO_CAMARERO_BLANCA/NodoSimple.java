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
    //Vector2d coordenadas; 
    int x,y;
    Queue<Types.ACTIONS> historialPasos;


    /** Constructor para nodo inicial 
     * coordenadas iniciales x e y 
    */
    public NodoSimple( int _x, int  _y){
        this.x = _x;
        this.y = _y;
        this.historialPasos = new LinkedList<Types.ACTIONS>();
        
    }
    /** Constructor para nodo inicial 
     * coordenadas iniciales x e y 
    */
    public NodoSimple( Double _x, Double  _y){
        this.x = (int) Math.floor(_x);
        this.y = (int) Math.floor(_y);
        this.historialPasos = new LinkedList<Types.ACTIONS>();
        
    }

    /**Constructor  a partir de un antecesor 
     * Coordenadas a
    */
    public NodoSimple(  int _x, int _y, Queue<Types.ACTIONS> _historialPasos, Types.ACTIONS nuevo_paso){
        this.x = _x;
        this.y = _y;
        this.historialPasos = new LinkedList<Types.ACTIONS>(_historialPasos);
        this.historialPasos.add(nuevo_paso); 
    }
}
