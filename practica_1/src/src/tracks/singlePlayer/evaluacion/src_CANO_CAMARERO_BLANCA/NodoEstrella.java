package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.Vector;

import ontology.Types;
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
public class NodoEstrella implements Comparable<NodoEstrella>{
    int x,y; // coordenadas del nodo 
    Vector<Types.ACTIONS> historialPasos;
    int h; // Distancia heurística
    int g; // distancia acumulada, coincide con la longitud del número de pasos
    int coordenada_padre_x, coordenada_padre_y; // para no repetir al expandir
    int f;

    /**
     *  Constructor de el nodo primero, solo se sabe sus coordenadas y el destino
     * @param _x
     * @param _y
     * @param destino_x
     * @param destino_y
     */
    public NodoEstrella(int _x, int _y, int destino_x, int destino_y){
        this.x = _x;
        this.y = _y;
        this.historialPasos = new Vector<ACTIONS>();
        this.h = distanciaManhattan(_x, _y, destino_x, destino_x);
        this.g = 0; 
        this.f = this.h; 
        // por ser el primer nodo él es su propio padre
        coordenada_padre_x = _x; 
        coordenada_padre_y = _y;
        
    }
    /**
     *  Constructor dado un nodo padre, destino, y paso nuevo con el que ha sido creado
     * @param _x
     * @param _y
     * @param destino_x
     * @param destino_y
     * @param padre
     * @param nuevo_paso
     */
    public NodoEstrella(int _x, int _y,
     int destino_x, int destino_y,
     NodoEstrella padre, 
     ACTIONS nuevo_paso){
        this.x = _x;
        this.y = _y;
        this.historialPasos = new Vector<ACTIONS>(padre.historialPasos);
        this.historialPasos.add(nuevo_paso); 
        this.h = distanciaManhattan(_x, _y, destino_x, destino_x);
        this.g = padre.g +1; 
        this.f = this.h + this.g; 
        // por ser el primer nodo él es su propio padre
        coordenada_padre_x = padre.x; 
        coordenada_padre_y = padre.y;   
    }
    /**
     * Compara si dos nodos estrella son iguales 
     * de acorde a sus coordenadas
     * @param n 
     * @return true si son iguales, else en caso contrario
     */
    public Boolean coordenadasIguales(NodoEstrella n) {
		if(this.x==n.x && this.y==n.y)
			return true;
		else
			return false;
	}
    /**
     *  Compara si son iguales coordenada a coordenada
     * @param _x
     * @param _y
     * @return
     */
    public Boolean coordenadasIguales(int _x, int _y) {
		if(this.x==_x && this.y==_y)
			return true;
		else
			return false;
	}
    /**
	 * Compara un nodo con otro con objeto de ordenarlos
	 * @param n Nodo con el que comparar
	 * @return devuelve 0 si son iguales, -1 si this es menor y 1 en caso contrario
     * 
     * El criterio es primer f, en caso de igualdad por las g (la que sea may), y en caso de igualdad por el nodo expandido
	 */
	@Override
	public int compareTo(NodoEstrella n ) {
        int valorComparacion = (int) Math.signum(-n.f +f);
        if(valorComparacion == 0){
            valorComparacion = (int) Math.signum(-n.g + g);
            if(valorComparacion == 0){
                int prioridad_propia = prioridadAccion(historialPasos.lastElement());
                int prioridad_n = prioridadAccion(n.historialPasos.lastElement());
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
        return "x: " + Integer.toString(x) 
                + ", y: " + Integer.toString(y)
                + ", f(n)= " +Integer.toString(f)
                + ", g(n)" + Integer.toString(g)
                + ", h: " + Integer.toString(h)
                ;
    }
}
