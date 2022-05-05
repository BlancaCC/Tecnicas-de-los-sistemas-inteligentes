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
array [Personas] of var 1..5 : numero_casa_persona; 

%%%% Restricciones 
% a. El vasco vive en la casa roja
constraint persona_casa[vasco]==roja;
% b. El catalán tienen un perro 
constraint persona_mascota[catalan] == perro;
% c. El gallego es pintor
constraint persona_profesion[gallego]==pintor;
% d. El navarro bebe te
constraint persona_bebida[navarro]==te;
% e. En andaluz vive en la primera casa de la izquierda 
constraint numero_casa_persona[andaluz]==1; 
% f. El de la casa verde bebe café 
constraint forall( p in Personas)( persona_casa[p]== verde <-> persona_bebida[p] == cafe);

% g. La casa verde está al lado de la blanca y a su derecha 
constraint forall( p,q in Personas)(
    % identificamos casa verde con su persona y su número
   persona_casa[p]== verde  
   <->
   % identificamos a la persona que vive en la blanca y su número a la izquierda de la verde
   (numero_casa_persona[q] == numero_casa_persona[p]+1/\ persona_casa[q]== blanca)
);


% h. El escultor cria caracores
constraint forall(p in Personas)(
    persona_profesion[p] == escultor 
    <-> 
    persona_mascota[p] == caracoles
);
% i. El diplomático vive en la casa amarilla.
constraint forall(p in Personas)(
    persona_profesion[p] == diplomatico 
    <-> 
    persona_casa[p] == amarilla
);
% j. En la casa central se bebe leche.
constraint forall(p in Personas)(
    numero_casa_persona[p] == 3 % 3 es la casa central 
    <-> 
    persona_bebida[p] == leche
);
% k. La casa del andaluz está al lado de la azul.
% ya sabemos por e que el andaluz vive en la primera casa, luego
%%%% caso en que la casa del andaluz tenga al lado y abajo
%%%%%%% Caso andaluz en la primera casa 
constraint forall(p in Personas)(
    persona_casa[p]==azul
    <-> 
    numero_casa_persona[p] == 2
);


% l. El violinista bebe zumo.
constraint forall(p in Personas)(
    persona_profesion[p] == violinista
    <-> 
    persona_bebida[p] == zumo
);
% m. El zorro está en una casa al lado de la del médico.
%%%% caso el zorro esté en medio 
constraint forall(dueno_zorro, persona_medico  in Personas)(
    persona_mascota[dueno_zorro] == zorro /\ persona_profesion[persona_medico] == medico  
    ->
    abs(numero_casa_persona[persona_medico] - numero_casa_persona[dueno_zorro]) == 1
);

% n. El caballo está en una casa al lado de la del diplomático.
constraint forall(dueno_caballo, persona_diplomatico  in Personas)(
    persona_mascota[dueno_caballo] == caballo /\ persona_profesion[persona_diplomatico] == diplomatico  
    ->
    abs(numero_casa_persona[persona_diplomatico] - numero_casa_persona[dueno_caballo]) == 1
);

%%% Preguntas que resolver 
% ¿Dónde está la cebra?
var Casas: casa_cebra; 
var Personas: dueno_cebra; 
constraint forall(p in Personas, c in Casas)(
    persona_mascota[p] == cebra
    /\
    persona_casa[p] == c
    -> 
    casa_cebra == c
    /\
    dueno_cebra = p
);
% ¿Quién bebea agua?
var Personas : bebe_agua; 
constraint forall(p in Personas)(
    persona_bebida[p] == agua
    ->
   bebe_agua == p
);

output
    ["La cebra está en la casa de color \(casa_cebra) que pertenece al \(dueno_cebra).\n"] ++
    ["El \(bebe_agua) bebe agua.\n"]
;

solve satisfy;

