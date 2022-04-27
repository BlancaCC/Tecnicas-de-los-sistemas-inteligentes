package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.LinkedList;
import java.util.Queue;
import ontology.Types;

/**
 * Encapsula la información elemental de un nodo 
 * usada en los problemas de BFS
 */
public class NodoSimple {
    Coordenadas c;
    Queue<Types.ACTIONS> historialPasos;
    
    /** Constructor para nodo inicial 
     * coordenadas iniciales x e y 
    */
    public NodoSimple( int x, int  y){
        c = new Coordenadas(x, y);
        this.historialPasos = new LinkedList<Types.ACTIONS>();
        
    }
    public NodoSimple(Coordenadas c){
        this.c = c;
        this.historialPasos = new LinkedList<Types.ACTIONS>();
        
    }
 
    /**Constructor  a partir de un antecesor 
     * Coordenadas a
    */
    public NodoSimple(  int x, int y, Queue<Types.ACTIONS> _historialPasos, Types.ACTIONS nuevo_paso){
        c = new Coordenadas(x, y);
        this.historialPasos = new LinkedList<Types.ACTIONS>(_historialPasos);
        this.historialPasos.add(nuevo_paso); 
    }
    public NodoSimple(  Coordenadas c, Queue<Types.ACTIONS> _historialPasos, Types.ACTIONS nuevo_paso){
        this.c = c;
        this.historialPasos = new LinkedList<Types.ACTIONS>(_historialPasos);
        this.historialPasos.add(nuevo_paso); 
    }
}
