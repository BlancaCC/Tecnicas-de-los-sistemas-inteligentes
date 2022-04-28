% Problema P1 Problema de las monedas 

% Autora: Blanca Cano Camarero 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 

include "globals.mzn";
 
%%%%%%%%%%%% Declaración de variables  %%%%%%%%%%%%
%% Estructura de datos que resuelve el problema 

set of int: NUMERO_MONEDAS = 1..8; % número de monedas distintas (hay 8 en total) 
array [NUMERO_MONEDAS] of int : VALOR_MONEDAS = [1, 2, 5, 10, 20, 50, 100, 200];
array [NUMERO_MONEDAS] of var int : CANTIDAD_MONEDAS;
int : IMPORTE; %% cantidad a buscar 

%%%%%%%%%%%%% Restricciones  %%%%%%%%%%%%

% 1. Cantidades de monedas debe de ser un número natural (mayor o igual que cero)
constraint forall( i in NUMERO_MONEDAS)(CANTIDAD_MONEDAS[i] >= 0);
% 2. La suma de cantidad más valor debe ser el importe
constraint sum( i in NUMERO_MONEDAS )(VALOR_MONEDAS[i]*CANTIDAD_MONEDAS[i]) == IMPORTE;

%%%%%%%%%%%% Imprimimos solución en pantalla %%%%%%%%%%%%
output
  ["El importe \(IMPORTE) se puede obtener como:\n"] ++
  ["\(CANTIDAD_MONEDAS[i]) monedas de \(VALOR_MONEDAS[i]) euros \n" | i in NUMERO_MONEDAS];
solve satisfy;