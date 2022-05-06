% Problema P2 Problema de los horarios 

% Autora: Blanca Cano Camarero 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 

include "globals.mzn";


% a. Definción del horario
set of int: HORAS = 1..6;
set of int: ASIGNATURAS = 1..9; 
set of int: DIAS = 1..5; % días de las semana
array[DIAS,HORAS] of var 1..10 : horario; % asignatura 10 representa el recreo


% b. número de horas por asignatura
array[ASIGNATURAS] of int : horas = [4,2,4,4,4,2,2,2,1];
constraint forall(a in ASIGNATURAS)(count([horario[d,h] | d in DIAS, h in HORAS],a) == horas[a]);


% c. y d. Bloques de asignaturas
% Asignaturas en bloques de 1 hora
set of ASIGNATURAS : bloque1hora = {2,6,7,9};
% Como mucho 1 hora al día
constraint forall(a in bloque1hora, d in DIAS)(count([horario[d,h] | h in HORAS],a) <= 1); 

% Asignaturas en bloques de 2 horas
set of ASIGNATURAS : bloque2hora = {1,3,4,5,8};
% Que para cada hora de la asignatura haya otra hora antes o después
constraint forall(a in bloque2hora, d in DIAS)(horario[d,1]==a-> horario[d,2]==a);
constraint forall(a in bloque2hora, d in DIAS)(horario[d,6]==a-> horario[d,5]==a);
constraint forall(a in bloque2hora, d in DIAS, h in HORAS diff{1,4,6})(horario[d,h]==a-> (horario[d,h-1]==a \/ horario[d,h+1]==a));
% Como mucho 2 horas al día (por las restricciones anteriores, 0 ó 2 horas).
constraint forall(a in bloque2hora, d in DIAS)(count([horario[d,h] | h in HORAS],a) <= 2);


% e. Profesores
set of int : PROFESORES = 1..4; 
array[PROFESORES] of set of ASIGNATURAS : asigProfe = [{1,3},{4,5},{6,9},{2,7,8}];

% f. Impedir que un profesor de dos bloques de asignaturas en un mismo día
constraint forall(p in PROFESORES diff {4}, a1 in asigProfe[p], a2 in asigProfe[p], d1 in DIAS, d2 in DIAS, h1 in HORAS, h2 in HORAS)((horario[d1,h1]==a1 /\ horario[d2,h2]==a2) -> (a1 == a2 \/ d1!=d2));

% g. Recreo
constraint forall(i in DIAS)(horario[i,4] == 10); % representamos con 10 el recreo


% h. Restricciones horarias de profesores

% El Profesor 1 solo puede dar clase en las dos últimas horas de la mañana.
constraint forall(a in asigProfe[1], d in DIAS, h in HORAS)(horario[d,h]==a -> (h==5 \/ h==6));
% El Profesor 2 solo puede dar clase en las dos primeras horas de la mañana
constraint forall(a in asigProfe[2], d in DIAS, h in HORAS)(horario[d,h]==a -> (h==1 \/ h==2));
% El Profesor 3 solo puede dar clase en la hora justo antes del recreo.
constraint forall(a in asigProfe[3], d in DIAS, h in HORAS)(horario[d,h]==a -> h==3);


% i. Restricciones de días para asignaturas
array[ASIGNATURAS] of set of DIAS : diasAsignaturas = [
  {1,2},          % Asignatura 1: lunes o martes
  {1,2,3,4,5},    % Asignatura 2: sin restricciones
  {3,4},          % Asignatura 3: miércoles o jueves
  {1,2},          % Asignatura 4: lunes o martes
  {4,5},          % Asignatura 5: jueves o viernes
  {3,5},          % Asignatura 6: miércoles o viernes
  {2,5},          % Asignatura 7: martes o viernes
  {3},            % Asignatura 8: miércoles
  {1}             % Asignatura 9: lunes
];
constraint forall(a in ASIGNATURAS, d in DIAS, h in HORAS)(horario[d,h]==a -> d in diasAsignaturas[a]);

% definición del problema
solve satisfy;





