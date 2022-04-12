package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
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
import tools.Pair;
import tools.Vector2d;

public class AgenteBFS extends AbstractPlayer  {
    public int num_actions;
    public Types.ACTIONS[] actions;
    
	Vector2d fescala;
	Vector2d portal_coordenadas;
	Vector2d avatar_coordenadas;
	
	//ArrayList con el plan a seguir
	private Stack<Types.ACTIONS> plan = new Stack<Types.ACTIONS>();

    // True si el nodo se puede explorar, false si no, 
	//si no está en el diccionario también se podrá visitar
    private HashMap<Vector2d, Boolean> visitable = new  HashMap<>();
	
	//Contador de las llamadas al método act
	int num_llamadas=0;
	
	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public AgenteBFS(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos coordenadas del Portal
        portal_coordenadas = posiciones[0].get(0).position;
        portal_coordenadas.x = Math.floor(portal_coordenadas.x / fescala.x);
        portal_coordenadas.y = Math.floor(portal_coordenadas.y / fescala.y);

		//Posición del avatar en coordenadas
        avatar_coordenadas = new Vector2d( 
			stateObs.getAvatarPosition().x / fescala.x,
      		stateObs.getAvatarPosition().y / fescala.y
		);
        
        //Obtenemos las posiciones de los muros y pinchos e indicamos que no sean visitados
        ArrayList<Observation>[] obstaculos = stateObs.getImmovablePositions();
        for (int j=0; j <=1; j++)
        for (int i = 0; i < obstaculos[j].size(); i++){
            //Obtenemos la posición de cada uno
			Vector2d obstaculos_coordenadas = new Vector2d(
				Math.floor(obstaculos[0].get(i).position.x / fescala.x), 
            	Math.floor(obstaculos[0].get(i).position.y / fescala.y)
				);
            visitable.put(obstaculos_coordenadas, false );
        }
      

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
	public Boolean esVisitable(Vector2d coordenadas){
		Boolean dentro_mapa = coordenadas.x >= 0 
		&& coordenadas.y >= 0 
		&& coordenadas.x <= stateObs.getObservationGrid().length - 1
		&& coordenadas.y <= stateObs.getObservationGrid()[0].length-1

		)
		return dentro_mapa && ( // está dentro del mapa 
			!(visitable.containsKey(coordenadas))  // no se ha marcado como ya visitado 
			|| visitable.get(coordenadas) // se ha incluido pero no está 
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

		// Creamos vectores auxiliares para simplificar proceso 
		ArrayList<ArrayList<Integer>> desplazamiento = new ArrayList<>();
        desplazamiento.add(new ArrayList<>(List.of(0,-1))); // arriba 
        desplazamiento.add(new ArrayList<>(List.of(0,1))); // abajo 
        desplazamiento.add(new ArrayList<>(List.of(-1,0))); // izquierda 
        desplazamiento.add(new ArrayList<>(List.of(1,0))); // derecha

		ArrayList<Types.ACTIONS> acciones = new ArrayList<>(
			List.of(
				Types.ACTIONS.ACTION_UP,
				Types.ACTIONS.ACTION_DOWN,
				Types.ACTIONS.ACTION_LEFT,
				Types.ACTIONS.ACTION_RIGHT
			)
		);

		for(int i=0; i<4; i++) {
			Vector2d coordenadas_sucesor = new Vector2d(
				 nodo.coordenadas.x + desplazamiento.get(i).get(0),
				 nodo.coordenadas.y + desplazamiento.get(i).get(1)
			);
			if( esVisitable(coordenadas_sucesor)) {
    			sucesores.add(
					new NodoSimple(
						nodo.coordenadas.x, nodo.coordenadas.y,
						nodo.historialPasos, 
						acciones.get(i)
					)
				);
			}
		}
	 		
		return sucesores;
	}

	
	// ----- Antiguo ------


	/**
	 * return the best action to arrive faster to the closest portal
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 * @return best	ACTION to arrive faster to the closest portal
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        if(num_llamadas==0) {
        	//Llamamos al plan con la información del lugar dónde se encuentran los muros
        	plan=planBFS(avatar,portal,muros_y_pinchos,stateObs);
        	num_llamadas++;
    		return plan.pop();
        }else {
    		return plan.pop();
        }
        	
	}

	/**
	 * Devuelve el plan de acción del agente para el resto de ejecuciones.
	 * @param nodo_inicio Nodo del que partimos para construir el plan.
	 * @param nodo_final Nodo al que queremos llegar.
	 * @param muros Array con los objetos inmóviles del mapa.
	 * @param stateObs Observation of the current state.
	 * @return pila con el plan a seguir por el agente.
	 */
	public Stack<Types.ACTIONS> planBFS(
        Nodo nodo_inicio, 
        Nodo nodo_final, 
        ArrayList<Nodo> muros,
        StateObservation stateObs){
		Nodo nodo_actual;
		Stack<Types.ACTIONS> plan= new Stack<Types.ACTIONS>();
		Hashtable<Double,Boolean> estado= new Hashtable<Double,Boolean>();
		//Marcamos el nodo inicial como visitado
		estado.put(nodo_inicio.id, true);
		Queue<Nodo> cola=new LinkedList<>();
		ArrayList<Nodo> sucesores= new ArrayList<>();
		
		//Metemos en la cola el nodo inicial
		cola.add(nodo_inicio);
		
		while(!cola.isEmpty()){
			nodo_actual=cola.peek();
			cola.remove();
			
			if(nodo_actual.coordenadas.equals(nodo_final.coordenadas)){
				System.out.println("calculamos el plan");
				return nodo_actual.calculaCamino();
			}
			
			sucesores=calculaSucesores(nodo_actual,muros,stateObs);
			for(int i=0;i<sucesores.size();i++) {
				if (!estado.containsKey(sucesores.get(i).id)) {
					estado.put(sucesores.get(i).id,true);
					sucesores.get(i).padre=nodo_actual;
					//visitados.add(sucesores.get(i));
					cola.add(sucesores.get(i));
				}
				
			}
			
		}
		
		return plan;	
	}
	
	





}
