% Problema P2 Problema de los horarios 

% Autora: Blanca Cano Camarero 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 

include "globals.mzn";
 
%%%%%%%%%%%% Declaración de variables  %%%%%%%%%%%%
%% Estructura de datos que abstraen el problema 
%%% Codificación  
set of int: dias = 1..5; % 1: Lunes, 2: martes, ..., 5: viernes
set of int: horas = 8..14; % Hora de comienzo de bloque de una hora
set of int: asignaturas = 1..9; % Identificador de la asignatura 0  recreo, el resto asignaturas
set of int: profesores = 1..4; % Identificador de profesor
array[dias,horas] of var 0..9 : HORARIO; % solución al problema: fila el día de la semana, a la hora tal y almacena el número de asignatura
array[asignaturas] of var profesores :profesor_asociado_a_asignatura; 
array[asignaturas] of var 1..4 : horas_semanales; 
array[profesores, horas]  of var 0..1 : disponibilidad; % horario disponiple (0: si no y 1: si sí )
disponibilidad = [
    |0,0,0,0,1,1    % disponibilidad profesor 1 (dos últimas horas)
    |1,1,0,0,0,0    % disponibilidad profesor 2 (dos primeras horas)  
    |0,0,1,0,0,0    % disponibilidad profesor 3 (antes del recreo) 
    |1,1,1,1,1,1|   % disponibilidad profesor 4 (siempre) 
]
% Indicamos el profesor que corresponde respectiva asignatura
profesor_asociado_a_asignatura = [1,4,1,2,2,3,4,4,3]; 
horas_semanales = [4,2,4,4,4,2,2,2,1]; % horas semanales que debe de durar una asignatura

% Asignaturas con bloque sde dos horas consecutivas
array[asignaturas] of var asignaturas: bloques_consecutivos; 
bloques_consecutivos = [2,1,2,2,2,1,1,2,1]; 
%%%%%%%%%%%%  Restricciones %%%%%%%%%%%% 
%%%% Inicializamos variables 
% a. Introducimos el recreo 
constraint forall(i in dias)(HORARIO[i,10]=0);

% b. Condición de que se impartan las horas semanales correspondientes 
constraint forall(a in asignaturas)(
    sum(d in dias)(count(HORARIO[d,horas],a)) == horas_semanales[a]
)
% c. se imparten en el bloque correspondiente
constraint forall(a in asignatura)(
    forall( d in dias)(
        bloques_consecutivos[a] == count(HORARIO[d,horas],a) \/ 0 == count(HORARIO[d,horas],a)
    )
);

% h. el horario coincide con una hora a la que puede el profesor
constraint forall(d  in dias)(
    forall(h in hora)(
        1 == disponibilidad[ % comprobamos si está disponible
            profesor_asociado_a_asignatura[HORARIO[d,h]] % el profesor que da clase el dia d a la hora h
            ,
            h
        ]
    )
);

% Condición de bloque
%constraint HORARIO



