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
array [Tareas, Estados] of var  int: tarea_estado_dia; 
array[Tareas] of var Trabajadores: tarea_trabajador; 
%%%% comenzamos las restricciones 
% Tarea inicial 
constraint tarea_estado_dia[A, inicio] == 1; % se realizará el día uno 
% Para el periodo de finalización  
constraint forall (trabajador in Trabajadores, tarea in Tareas)(
    tarea_trabajador[tarea] == trabajador
    ->
    tarea_estado_dia[tarea, fin] == tarea_estado_dia[tarea,inicio] -1 + duracion_tarea_trabajador[tarea, trabajador]
); 
% Restricción de inicio, deben de haber acabado las tareas que le preceden
constraint forall (tarea in Tareas diff {A})(
    tarea_estado_dia[tarea,inicio] > max([tarea_estado_dia[p,fin] | p in Predecesores[tarea]])
);
% Condición de asignación de tarea 
% El trabajador asignado no puede estar trabajando en otra tarea al mismo tiemo
constraint forall(t in Tareas)(
    not (
      tarea_trabajador[t] in [
          trabajador | trabajador in Trabajadores , tarea_paralela in Tareas diff {t} where
              tarea_trabajador[tarea_paralela] == trabajador
              /\
              tarea_estado_dia[tarea_paralela, inicio] <= tarea_estado_dia[t, inicio]
              /\
              tarea_estado_dia[tarea_paralela, fin] >=  tarea_estado_dia[t, inicio]
      ]
    )
);

solve minimize max([ tarea_estado_dia[T, fin] | T in Tareas]);
