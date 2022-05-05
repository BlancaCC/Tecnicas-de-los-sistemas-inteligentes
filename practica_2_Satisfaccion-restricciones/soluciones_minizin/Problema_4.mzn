%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Problema 4  : Asignación de tareas
%% Alumna: Blanca Cano Camarero 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Codificación del problema  %= {A, B, C, D, F, G, H, I}; [1, 2, 3, 4, 5, 6, 7 , 8];
enum Tareas  = {A, B, C, D, E, F, G, H, I}; % Tareas
%array [1..3] of int : Trabajadores  = [1,2,3]; % Identificador de trabajador
enum Trabajadores = {T1, T2, T3};
% tiempo para la tarea que tarda el trabajado concret
array [Tareas, Trabajadores] of  int  : duracion_tarea_trabajador= [|4,7,10,
| 3,5,7,
| 3,1,4,
| 2,5,8,
| 4,2,6,
| 3,2,1,
| 1,2,3,
| 1,3,5,
| 2,3,4|];
% Tareas predecesores 
array [Tareas] of set of Tareas: Predecesores = [
    {}, % A: ninguna tarea debe de hacerse antes 
    {A}, % B: Para la tarea B antes debe de hacerse antes la A
    {B},
    {A},
    {C,D},
    {C,D},
    {C,D},
    {A},
   {F,H} 
];

% Variables que vamos a resolver
enum Estados = {inicio, fin}; % Variable auxiliar que inidica si una tarea s eencuentra en su inicio o en su fin  
array [Tareas, Estados] of var  1..100 : dia_tarea_estado; 
%array [Trabajado, 1..100] var of 0..1: trabajador_ocupado_dia; 
array[Tareas] of var Trabajadores: tarea_trabajador; 
%%%% comenzamos las restricciones 
constraint forall(T in Tareas , d in 1..90, o in Trabajadores)(
    % comporbamos que las tareas pendientes estén hechas
    forall (P in Predecesores[T])(
        dia_tarea_estado[P, fin] < d 
    )
    /\
    % no se encuentra trabajando 
    not (o in [
        tarea_trabajador[w] | w in Tareas where
            dia_tarea_estado[w,inicio]<= d 
            /\ 
            dia_tarea_estado[w,fin] >= d
        ]
    )
    ->
    dia_tarea_estado[T,inicio] == d
    /\ 
    dia_tarea_estado[T,fin] == d+duracion_tarea_trabajador[T, o]
    /\
    tarea_trabajador[T] == o
);

solve minimize max([ dia_tarea_estado[T, fin] | T in Tareas]);
