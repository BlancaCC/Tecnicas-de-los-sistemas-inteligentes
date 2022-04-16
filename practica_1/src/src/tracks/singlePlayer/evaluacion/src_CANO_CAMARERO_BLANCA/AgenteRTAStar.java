package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteRTAStar extends AbstractPlayer {
 
	//Tabla Hash para actualizar las h(n) en el algoritmo en tiempo constante
    Hashtable<Coordenadas, Integer> heuristica = new Hashtable<>();

    // Variables para Act y métricas
    Boolean planCalculado = false;
    double runtime = 0;
    int tam_plan = 0;

    // Variables auxiliares relacionadas con coordenadas 
   //Conjunto que contiene los obstáculos relacionada con el almacén de obstáculos 
    // si hay un obstáculo en (x,y) entonces tales coordenadas pertenecen a obstáculos
	Set<Coordenadas> obstaculos= new HashSet<>();

    Coordenadas coord_portal;
    NodoHeuristico avatar; // contiene la posición actua del avatar

    // Otras variables auxiliares 
    // Creamos vectores auxiliares para simplificar proceso  de cálculo de sucesores 
	ArrayList<ArrayList<Integer>> desplazamiento = new ArrayList<>();
	ArrayList<Types.ACTIONS> acciones = new ArrayList<>(
			List.of(
				Types.ACTIONS.ACTION_UP,
				Types.ACTIONS.ACTION_DOWN,
				Types.ACTIONS.ACTION_LEFT,
				Types.ACTIONS.ACTION_RIGHT
			)
		);

    /**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public AgenteRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
       
        // Cálculos iniciales relacionados con el cáculo de coordenadas del mapa 

        //Calculamos el factor de escala entre mundos (pixeles -> grid)
        Vector2d fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos coordenadas del Portal
        Vector2d portal_coordenadas= posiciones[0].get(0).position;
        int portal_x = (int) Math.floor(portal_coordenadas.x / fescala.x);
		int portal_y = (int) Math.floor(portal_coordenadas.y / fescala.y);
        coord_portal = new Coordenadas(portal_x, portal_y);

        //Posición del avatar en coordenadas
        int avatar_x = (int) Math.floor(
			stateObs.getAvatarPosition().x / fescala.x);
        int avatar_y = (int) Math.floor(
      		stateObs.getAvatarPosition().y / fescala.y);

        Coordenadas coord_avatar = new Coordenadas(avatar_x, avatar_y);
        avatar = new NodoHeuristico(coord_avatar, calcularHeuristica(coord_avatar));
	
        //Obtenemos las posiciones de los muros y pinchos e indicamos que no sean visitados
        ArrayList<Observation>[] vector_obstaculos = stateObs.getImmovablePositions();
        for (int j=0; j <=1; j++) // muros y pinchos
        for (int i = 0; i < vector_obstaculos[j].size(); i++){
            //Obtenemos la posición de cada uno obstáculos 
            Coordenadas coordenadas = new Coordenadas(
 			(int)Math.floor(vector_obstaculos[j].get(i).position.x / fescala.x),
            (int)Math.floor(vector_obstaculos[j].get(i).position.y / fescala.y));
            obstaculos.add(coordenadas);
        }
         
        // Inicializamos desplazamientos a calcular 
        desplazamiento.add(new ArrayList<>(List.of(0,-1))); // arriba 
        desplazamiento.add(new ArrayList<>(List.of(0,1))); // abajo 
        desplazamiento.add(new ArrayList<>(List.of(-1,0))); // izquierda 
        desplazamiento.add(new ArrayList<>(List.of(1,0))); // derecha	
    }

    /**
     * Los métodos del AgenteRTAStart van encaminados a obtener una ruta a partir del algoritmo RTA*, 
     * para ello se han definido los siguientes métodos: 
     * Ver si un nodo es visitable 
     * Realizaremos su implementación en orden de la quinta a la primera. 
    */
   
    /**
	 * Devuelve si la casilla de coordenadas es visitable
	 * @param coordenadas 
	 * @return si es visitable o no
     * 
     * No es necesario calcular si está dentro o no de los límites del mapa puesto los bordes están recubiertos de muros y la única forma de que se saliera, es que se generara un sucesor en el borde, pero nunca se generarán nodos en un muro. 
	 */
	public Boolean esVisitable(Coordenadas c){
		return  !obstaculos.contains(c); 	
	} 
    /**
     * Devuelve si un nodo de coordenadas indicadas es objetivo o no
     * @param x
     * @param y
     * @return
     */
    boolean esObjetivo(Coordenadas c){
        return coord_portal.equals(c);
    }
    /**
     * Devuelve el valor de la heurística
     * @param c
     * @return
     */
    int calcularHeuristica(Coordenadas c){
        if(heuristica.containsKey(c)){
            return heuristica.get(c);
        }
        int h = distanciaManhattan(c);
        heuristica.put(c,h);
        return h;

    }
    /**
     *  Calcula la distancia Manhattan de dos puntos de coordenadas, será utilizado como heurística
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return distancia Manhattan
     */
    private int distanciaManhattan(Coordenadas c){
        return Math.abs(c.x-coord_portal.x) + Math.abs(c.y-coord_portal.y); 
    } 

    /**
	 * Calcula los sucesores que sean visitables de un nodo
	 * Un nodo sucesor será visitable si:
	 * 		1. No se ha visitado con anterioridad
	 * 		2. No  es un obstáculo 
	 * 		3. No se sale de los límites del mapa
	 * Los nodos serán expandidos de acorde al orden: 
	 * 	arriba, abajo, izquierda, derecha.
	 * @param nodo
	 * @return Array con los sucesores en orden de generación.
	 */
	public PriorityQueue<NodoHeuristico> calcularSucesores(NodoHeuristico nodo) {

		PriorityQueue<NodoHeuristico> sucesores= new PriorityQueue<>();
		for(int i=0; i<4; i++) {
            Coordenadas coordenadas_sucesor = new Coordenadas(
			nodo.c.x + desplazamiento.get(i).get(0),
			nodo.c.y + desplazamiento.get(i).get(1));
            // calculo de la heurística 

			if( esVisitable(coordenadas_sucesor)) {
                // lo añadimos como nodo generado
    			sucesores.add(
					new NodoHeuristico(coordenadas_sucesor, calcularHeuristica(coordenadas_sucesor),acciones.get(i), nodo)
				);
			}
		}
		return sucesores;
	} 
    /** 
     * Generamos plan en base al algoritmo IDAStar
     * @return
     */
    ACTIONS generarPlanIDAStar(){
        // calculamos los sucesores del la posición actual
        PriorityQueue<NodoHeuristico> sucesores = calcularSucesores(avatar);
        // regla de aprendizaje 
        NodoHeuristico mejor_sucesor= sucesores.poll(); // sacamos el mejor camino
        if(!sucesores.isEmpty()){
            NodoHeuristico segundo_mejor_sucesor = sucesores.poll();
            heuristica.put(avatar.c, Math.max(this.heuristica.get(avatar.c), segundo_mejor_sucesor.f));
        }
        else heuristica.put(avatar.c, mejor_sucesor.f);
        avatar = mejor_sucesor;
        return avatar.ultima_accion;
    }
    /**
	 * Return the best action to arrive faster to the closest portal
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 * @return best	ACTION to arrive faster to the closest portal
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        long tInicio = System.nanoTime();
        ACTIONS accion = generarPlanIDAStar();
        long tFin = System.nanoTime();
		//Lo vamos acumulando
		runtime += (double)((tFin - tInicio))/1000000;
		tam_plan++;
		//Solo cuando el avatar llega al portal mostramos las estadísticas
		if(avatar.c.equals(coord_portal)) {
			System.out.println("Runtime: "+runtime);
            System.out.println("Tamaño ruta calculada: "+tam_plan);
        	System.out.println("Número de nodos expandidos: "+tam_plan); // coincide con la ruta en este caso
        	System.out.println("Máximo número de nodos en memoria: "+heuristica.size()); // tamaño de la tabla hash
		}
        return accion;
	}
}
