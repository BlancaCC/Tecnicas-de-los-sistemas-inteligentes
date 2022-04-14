package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * Resuelve el mapa implementando el método de búsqueda en anchura 
 */
public class AgenteDFS extends AbstractPlayer  {
    public int num_actions;
    public Types.ACTIONS[] actions;
	Vector2d fescala;
	Vector2d avatar_coordenadas;
	Boolean planCalculado = false;
	int portal_x ;
	int portal_y;
	
	//ArrayList con el plan a seguir
	private Queue<Types.ACTIONS> plan = new LinkedList<>();

    // True si el nodo se puede explorar, false si no, 
	//si no está en el diccionario también se podrá visitar
    private Boolean [][] visitable;
	
	// límites superiores del mapa 
	Vector2d limite_mapa; 

	// Creamos vectores auxiliares para simplificar proceso  de cálculo de sucesores 
	ArrayList<ArrayList<Integer>> desplazamiento = new ArrayList<>();
	ArrayList<Types.ACTIONS> acciones = new ArrayList<>(
		List.of(
			Types.ACTIONS.ACTION_RIGHT,
			Types.ACTIONS.ACTION_LEFT,
			Types.ACTIONS.ACTION_DOWN,
			Types.ACTIONS.ACTION_UP	
		)
	);
	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public AgenteDFS(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos coordenadas del Portal
        Vector2d portal_coordenadas= posiciones[0].get(0).position;
        portal_x = (int) Math.floor(portal_coordenadas.x / fescala.x);
		portal_y = (int) Math.floor(portal_coordenadas.y / fescala.y);
		
		//Posición del avatar en coordenadas
        avatar_coordenadas = new Vector2d( 
			stateObs.getAvatarPosition().x / fescala.x,
      		stateObs.getAvatarPosition().y / fescala.y
		);
		// Límites del mapa 
		limite_mapa = new Vector2d(
			stateObs.getObservationGrid().length - 1,
			stateObs.getObservationGrid()[0].length-1
		);
		int limite_mapa_x = stateObs.getObservationGrid().length;
		int limite_mapa_y = stateObs.getObservationGrid()[0].length;
        // a priori todo los sitios son visitables, lo indicamos
		visitable = new Boolean[limite_mapa_x][limite_mapa_y];
		for(int x = 0; x <= limite_mapa.x; x++){
			Arrays.fill(visitable[x], true );
		}
		// Marcamos lo muros y trampas 

         //Obtenemos las posiciones de los muros y pinchos e indicamos que no sean visitados
         ArrayList<Observation>[] obstaculos = stateObs.getImmovablePositions();
		 int x,y; 
         for (int j=0; j <=1; j++) // muros y pinchos
         for (int i = 0; i < obstaculos[j].size(); i++){
             //Obtenemos la posición de cada uno
 				x = (int)Math.floor(obstaculos[j].get(i).position.x / fescala.x);
             	y = (int)Math.floor(obstaculos[j].get(i).position.y / fescala.y);
 				
             visitable[x][y]=false;
         } 
		// añadimos desplazamientos a calcular  (notemos que el orden de lectura será el de abajo arriba)
		desplazamiento.add(new ArrayList<>(List.of(1,0))); // derecha
		desplazamiento.add(new ArrayList<>(List.of(-1,0))); // izquierda 
		desplazamiento.add(new ArrayList<>(List.of(0,1))); // abajo 
		desplazamiento.add(new ArrayList<>(List.of(0,-1))); // arriba  	
	}

	/*
	A continuación construiremos funciones básicas que se encarguen de 
	1. Llamar a siguiente acción. 
	2. General plan. 
	
	Que cuentan con las funciones auxiliares que se encargarán de  
	3 Generar sucesores de un nodo.
	4 Indicar si cierta casilla es visitable.

	Dicho esto escribiremos las respectivas implementación en orden ascendente al expuesto
	*/

	/**
	 * Devuelve si la casilla de coordenadas x e y es visitable
	 * @param coordenadas 
	 * @return si es visitable o no
	 */
	public Boolean esVisitable(int x, int y){
		Boolean dentro_mapa = (x >= 0 
		&& y >= 0 
		&& x <= limite_mapa.x
		&& y <= limite_mapa.y
		);
		return dentro_mapa && ( // está dentro del mapa 
			visitable[x][y]==true // 
		);
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
	 * @return Array con los sucesores en orden de generación 
	 */
	public ArrayList<NodoSimple> calculaSucesores(NodoSimple nodo) {

		ArrayList<NodoSimple> sucesores= new ArrayList<>();
		for(int i=0; i<4; i++) {
			int coordenada_x_sucesor = nodo.x + desplazamiento.get(i).get(0);
			int coordenada_y_sucesor = nodo.y + desplazamiento.get(i).get(1);

			if( esVisitable(coordenada_x_sucesor , coordenada_y_sucesor )) {
    			sucesores.add(
					new NodoSimple(
						coordenada_x_sucesor, coordenada_y_sucesor,
						nodo.historialPasos, 
						acciones.get(i)
					)
				);
			}
		}		
		return sucesores;
	}
	/** 
	 * Genera el plan a seguir 
	 * lo que hace es añadir el plan a la variable  privada plan 
	 * El plan se genera mediante una búsqueda en profunidad, es por ello que la estructura que almacena 
	 * el plan sea una pila 
	 * devuelve true si se encuentra 
	 */
	private  boolean generarPlanBFS(){
		NodoSimple posicion_actual = new NodoSimple(
			avatar_coordenadas.x,
			avatar_coordenadas.y
		);
		if(posicion_actual.x == portal_x && posicion_actual.y == portal_y){
			plan = posicion_actual.historialPasos;
			return true;
		}
		// se supone que el portal es distinto a la posición inicial 
		Stack <NodoSimple> pendientesExplorar = new Stack<>();
		
		for (NodoSimple n :calculaSucesores(posicion_actual)){
			pendientesExplorar.push(n);
			visitable[n.x][n.y] = false; // Añadimos que ya se ha explorado
		}
		while ( !pendientesExplorar.isEmpty()){ // y si quedan nodos que explorar 

			posicion_actual = pendientesExplorar.pop();
			
			if(posicion_actual.x == portal_x && posicion_actual.y == portal_y){
				plan = posicion_actual.historialPasos;
				return true;
			}
			
			for (NodoSimple n :calculaSucesores(posicion_actual)){
				pendientesExplorar.add(n);
				visitable[n.x][n.y] = false; // Añadimos que ya se ha explorado
			}
		}
		return false; 
	}
	/**
	 * Return the best action to arrive faster to the closest portal
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 * @return best	ACTION to arrive faster to the closest portal
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        if(planCalculado  == false) {
        	//Llamamos al plan con la información del lugar dónde se encuentran los muros
        	planCalculado = generarPlanBFS();
			System.out.print(plan);
    		return plan.poll();
        }else {
    		return plan.poll();
        }   
		  	
	}
}
