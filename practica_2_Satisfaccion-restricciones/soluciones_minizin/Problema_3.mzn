% Problema P3 Problema lógico

% Autora: Blanca Cano Camarero 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
include "globals.mzn";
% Declaramos a las Personas
enum Personas = {vasco, catalan, gallego, navarro, andaluz};
% Resto de atributos 
enum Casas = {roja, blanca, azul, amarilla, verde};
enum Bebidas = {te, cafe, leche, zumo, agua}; 
enum Mascotas = {perro, caracoles, zorro, cebra, caballo };
enum Profesion = {pintor, violinista, medico, diplomatico , escultor};
% Relaciones 
array [Personas] of var Casas: persona_casa;
array [Personas] of var Mascotas: persona_mascota;
array [Personas] of var Profesion: persona_profesion;
array [Personas] of var Bebidas: persona_bebida;
% almacena el orden de las casa 1 la más a la izquierda 
array [1..5] of var Personas : numero_casa_persona; 

%%%% Restricciones 
% a. El vasco vive en la casa roja
constraint persona_casa[vasco]==roja;
% b. El catalán tienen un perro 
constraint persona_mascota[catalan] == perro;
% c. El gallego es pintor
constraint persona_profesion[gallego]==pintor;
% d. El navarro bebe te
constraint persona_bebida[navarro]==te;
% e. En andaluz vivie en la primera casa de la izquierda 
constraint numero_casa_persona[1]==andaluz; 
% f. El de la casa verde bebe café 
constraint forall( p in Personas)( persona_casa[p]== verde -> persona_bebida[p] == cafe);
% g. La casa verde está al lado de la blanca y a su derecha 
constraint forall( p in Personas, q in Personas, i in 1..4)(
    % sacamos quién vive para obtener la dirección de la casa
   (persona_casa[p]== verde /\ persona_casa[q]== blanca
   /\ numero_casa_persona[i] == p)
   ->
   % estar a la derecha es estár una dirección desplazada
   numero_casa_persona[i+1] == q
);
% h. Es escultor cria caracores
constraint forall(p in Personas)(
    persona_profesion[p] == escultor 
    -> 
    persona_mascota[p] == caracoles
);
% i. El diplomático vive en la casa amarilla.
constraint forall(p in Personas)(
    persona_profesion[p] == diplomatico 
    -> 
    persona_casa[p] == amarilla
);
% j. En la casa central se bebe leche.
constraint forall(p in Personas)(
    numero_casa_persona[3] == p
    -> 
    persona_bebida[p] == leche
);
% k. La casa del andaluz está al lado de la azul.
constraint forall(p in Personas, i in 1..5, j in 1..5)(
    abs(i-j) == 1  % si las clasas son vecinas 
    /\
    numero_casa_persona[i] == andaluz
    /\
    persona_casa[p]==azul
    -> 
    numero_casa_persona[j] == p 
);
% l. El violinista bebe zumo.
constraint forall(p in Personas)(
    persona_profesion[p] == violinista
    -> 
    persona_bebida[p] == zumo
);
% m. El zorro está en una casa al lado de la del médico.
%%%% caso el zorro esté en medio 
constraint forall(dueno_zorro in Personas, persona_medico in Personas, i in 2..4)(
    persona_mascota[dueno_zorro] == zorro
    /\
    numero_casa_persona[i] == dueno_zorro
    /\
    persona_profesion[persona_medico] == medico
    -> 
    numero_casa_persona[i-1] == persona_medico \/  numero_casa_persona[i+1] == persona_medico 
);
% caso vive en la casa de la derecha 
constraint forall(dueno_zorro in Personas, persona_medico in Personas)(
    persona_mascota[dueno_zorro] == zorro
    /\
    numero_casa_persona[5] == dueno_zorro
    /\
    persona_profesion[persona_medico] == medico
    -> 
    numero_casa_persona[4] == persona_medico
);
% caso vive en la casa de la izquierda
constraint forall(dueno_zorro in Personas, persona_medico in Personas)(
    persona_mascota[dueno_zorro] == zorro
    /\
    numero_casa_persona[1] == dueno_zorro
    /\
    persona_profesion[persona_medico] == medico
    -> 
    numero_casa_persona[2] == persona_medico
);

% n. El caballo está en una casa al lado de la del diplomático.
constraint forall(dueno_caballo in Personas, persona_diplomatico in Personas, i in 2..4)(
    persona_mascota[dueno_caballo] == caballo
    /\
    numero_casa_persona[i] == dueno_caballo
    /\
    persona_profesion[persona_diplomatico] == diplomatico
    -> 
    numero_casa_persona[i-1] == persona_diplomatico \/  numero_casa_persona[i+1] == persona_diplomatico 
);
% caso vive en la casa de la derecha 
constraint forall(dueno_caballo in Personas, persona_diplomatico in Personas)(
    persona_mascota[dueno_caballo] == caballo
    /\
    numero_casa_persona[5] == dueno_caballo
    /\
    persona_profesion[persona_diplomatico] == diplomatico
    -> 
    numero_casa_persona[4] == persona_diplomatico
);
% caso vive en la casa de la izquierda
constraint forall(dueno_caballo in Personas, persona_diplomatico in Personas)(
    persona_mascota[dueno_caballo] == caballo
    /\
    numero_casa_persona[1] == dueno_caballo
    /\
    persona_profesion[persona_diplomatico] == diplomatico
    -> 
    numero_casa_persona[2] == persona_diplomatico
);
%%% Preguntas que resolver 
% ¿Dónde está la cebra?
var Casas: casa_cebra; 
constraint forall(p in Personas, c in Casas)(
    persona_mascota[p] == cebra
    /\
    persona_casa[p] == c
    -> 
    casa_cebra == c
);
% ¿Quién bebea agua?
var Personas : bebe_agua; 
constraint forall(p in Personas)(
    persona_bebida[p] == agua
    ->
   bebe_agua == p
);

output
    ["La cebra está en la casa de color \(casa_cebra).\n"] ++
    ["El \(bebe_agua) bebe agua.\n"]
;

solve satisfy;

