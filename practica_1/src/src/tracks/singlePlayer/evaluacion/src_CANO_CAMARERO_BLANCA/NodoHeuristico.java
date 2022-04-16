package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import ontology.Types.ACTIONS;

/**
 * Estructura de un Nodo heurístico 
 * Esta clase cuenta con los siguiente métodos: 
 * - Constructor para el nodo inicial
 * - Constructor para nodo expandido de otro
 * - Sobrecarga de la comparación 
 * - Si `CoordenadasIguales` dado otro nodo si sus posición es la misma 
 * Métodos auxiliares: 
 * - Distancia manhattan
 * - Prioridad según la acción 
 */
public class NodoHeuristico implements Comparable<NodoHeuristico>{
    Coordenadas c; // coordenadas del nodo 
    ACTIONS ultima_accion;
    int h, f, g; // Distancia heurística 
    

    /**
     *  Constructor para el nodo inicial
     * @param _x
     * @param _y
     * @param _h heurística que se calcula
     */
    public NodoHeuristico(Coordenadas c, int h){
        this.c = c;
        this.h = h;
        this.g = 0;
        this.f = h;
    }
    /**
     *  Constructor dado un nodo padre, destino, y paso nuevo con el que ha sido creado
     * @param _x
     * @param _y
     * @param padre
     * @param nuevo_paso
     */

    public NodoHeuristico(Coordenadas c,
        int h,
        ACTIONS nuevo_paso, NodoHeuristico padre){
        this.c = c; 
        this.ultima_accion = nuevo_paso;
        this.g = 1;
        this.h = h; 
        this.f = this.g + h;
  
    }
    /**
     * Compara si dos nodos estrella son iguales 
     * de acorde a sus coordenadas
     * @param n 
     * @return true si son iguales, else en caso contrario
     */
    public Boolean coordenadasIguales(NodoHeuristico n) {
		return c.equals(n.c);
	}
    
    /**
	 * Compara un nodo con otro con objeto de ordenarlos
	 * @param n Nodo con el que comparar
	 * @return devuelve 0 si son iguales, -1 si this es mayor y 1 en caso contrario
     * 
     * El criterio es primer f, en caso de igualdad por las g (la que sea may), y en caso de igualdad por el nodo expandido
	 */
	@Override
	public int compareTo(NodoHeuristico n ) {
        int valorComparacion = (int) Math.signum(-n.f +f);
        if(valorComparacion == 0){
            valorComparacion = (int) Math.signum(-n.g + g);
            if(valorComparacion == 0){
                int prioridad_propia = prioridadAccion(this.ultima_accion);
                int prioridad_n = prioridadAccion(n.ultima_accion);
                //signo debería ir al revés
                valorComparacion = (int) Math.signum(-prioridad_n + prioridad_propia);
 
            }
        }
        return valorComparacion;
	}
    /**
     *  Calcula la distancia Manhattan de dos puntos de coordenas
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return distancia Manhattan
     */
    private int distanciaManhattan(int x1, int y1, int x2, int y2){
        return Math.abs(x1-x2) + Math.abs(y1-y2); 
    }  
    /**
     * 
     * @param action
     * @return
     * 
     * 
     * La prioridades para las que está implementado son 
        ACTION_UP 
        ACTION_LEFT
        ACTION_DOWN
        ACTION_RIGHT
     */
    private int prioridadAccion(ACTIONS action){
        int prioridad = 5; 
        switch (action){
            case ACTION_UP:
                prioridad = 1;
                break;
            case ACTION_DOWN:
                prioridad = 2;
            break;
            case ACTION_LEFT:
                prioridad = 3;
                break;
            case ACTION_RIGHT:
                prioridad = 4;
            break;
            default:
                System.out.print("Error en prioridad action, no se ha encontrado");
                System.out.println(action);
        }
        return prioridad;
    }
    /**
     * Sobrecarga del método para mostrarlo en pantalla 
     */
    public String toString() {
        return "coordenadas : " + c.toString() 
                + ", h: " + Integer.toString(h)
                + ", f: " + Integer.toString(f)
                ;
    }
}
