package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
public class AgenteBFS extends AbstractPlayer  {
  
		// Situación del agente y meta
		Coordenadas avatar;
		Coordenadas portal;
		Boolean planCalculado = false; // Si la ruta se conoce ya

	//Cola con el plan a seguir
	private Queue<Types.ACTIONS> plan = new LinkedList<>();

   // Si un par de coordenadas pertenecen aquí no serán visitables
	// ya sea por ser un obstáculo o por haberse visitado con anterioridad
	Set<Coordenadas> noVisitable= new HashSet<>();


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

	// Métricas que mostrar en pantalla 
	int nodos_expandidos = 0;
	int nodos_en_memoria = 0;
	
	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public AgenteBFS(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
		Vector2d fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        	stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos coordenadas del Portal
        Vector2d portal_coordenadas= posiciones[0].get(0).position;
		portal = new Coordenadas(
			(int) Math.floor(portal_coordenadas.x / fescala.x),
			(int) Math.floor(portal_coordenadas.y / fescala.y)
		);
		//Posición del avatar en coordenadas
        avatar = new Coordenadas( 
			(int) Math.floor(stateObs.getAvatarPosition().x / fescala.x),
			(int) Math.floor(stateObs.getAvatarPosition().y / fescala.y)
		);
		// Marcamos lo muros y trampas 

		// Marcamos lo muros y trampas 
        //Obtenemos las posiciones de los muros y pinchos e indicamos que no sean visitados
        ArrayList<Observation>[] obstaculos = stateObs.getImmovablePositions();

        for (int j=0; j <=1; j++) // muros y pinchos
        for (int i = 0; i < obstaculos[j].size(); i++){
            //Obtenemos la posición de cada uno
			noVisitable.add(
				new Coordenadas(
					(int)Math.floor(obstaculos[j].get(i).position.x / fescala.x),
					(int)Math.floor(obstaculos[j].get(i).position.y / fescala.y)
			 	)
			);
        }
		// Inicializamos desplazamientos a calcular 
		desplazamiento.add(new ArrayList<>(List.of(0,-1))); // arriba 
		desplazamiento.add(new ArrayList<>(List.of(0,1))); // abajo 
		desplazamiento.add(new ArrayList<>(List.of(-1,0))); // izquierda 
		desplazamiento.add(new ArrayList<>(List.of(1,0))); // derecha	
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
	 * @return si es visitable o no, ya sea por ser un obstáculo o porque se visitó antes
	 * No vamos a comprobar si se sale del límite del mapa ya que ese caso nunca se dará
	 * por las circunstancias
	 */
	public Boolean esVisitable(Coordenadas c){
		return !noVisitable.contains(c);
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
			Coordenadas sucesor = new Coordenadas(
				nodo.c.x + desplazamiento.get(i).get(0),
				nodo.c.y + desplazamiento.get(i).get(1)
			);
			if( esVisitable(sucesor)) {
    			sucesores.add(
					new NodoSimple(
						sucesor,
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
	 * El plan se genera mediante una búsqueda en anchura, es por ello que la estructura que almacena 
	 * el plan sea una cola 
	 * devuelve true si se encuentra 
	 */
	private  boolean generarPlanBFS(){
		NodoSimple posicion_actual = new NodoSimple(
			avatar.x,
			avatar.y
		);
		if(posicion_actual.c.equals(portal)){
			plan = posicion_actual.historialPasos;
			return true;
		}
		nodos_expandidos++;
		// se supone que el portal es distinto a la posición inicial 
		Queue <NodoSimple> pendientesExplorar = new LinkedList<>();
		for (NodoSimple n :calculaSucesores(posicion_actual)){
			pendientesExplorar.add(n);
			noVisitable.add(n.c);
			nodos_en_memoria++;
		}
		while ( !pendientesExplorar.isEmpty()){ // y si quedan nodos que explorar 

			posicion_actual = pendientesExplorar.poll();
			// Actualizamos estadísticas de expansión y nodos en memoria
			// Se comprueba si se ha alcanzado el objetivo
			if(posicion_actual.c.equals(portal)){
				plan = posicion_actual.historialPasos;
				return true;
			}
			nodos_expandidos++;
			// Calculamos sucesor
			for (NodoSimple n :calculaSucesores(posicion_actual)){
				pendientesExplorar.add(n);
				noVisitable.add(n.c); // Añadimos que ya se ha añadido a pendientes de explorar
				nodos_en_memoria++; // cada sucesor nuevo supone un nuevo nodo en memoria
			}
			// Actualizamos métrica
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
			
			long tiempo_inicio = System.nanoTime();
        	planCalculado = generarPlanBFS();
        	long tiempo_fin = System.nanoTime();
			//calculamos tiempo de ejecución
    		double runtime = (double)((tiempo_fin - tiempo_inicio))/1000000;
    		//Calculamos el tamaño del plan
    		int tam_plan = plan.size();

			//Mostramos los valores para rellenar la tabla
        	System.out.println("Runtime: "+runtime);
        	System.out.println("Tamaño ruta calculada: "+tam_plan);
        	System.out.println("Número de nodos expandidos: "+nodos_expandidos);
        	System.out.println("Máximo número de nodos en memoria: "+nodos_en_memoria);

    		return plan.poll();
        }else {
    		return plan.poll();
        }   
		  	
	}
}
