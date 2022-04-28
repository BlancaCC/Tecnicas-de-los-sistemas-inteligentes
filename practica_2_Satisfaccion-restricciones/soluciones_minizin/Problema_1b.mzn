% Problema P1 Problema de las monedas 

% Autora: Blanca Cano Camarero 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 

include "globals.mzn";
 
%%%%%%%%%%%% Declaración de variables  %%%%%%%%%%%%
%% Estructura de datos que resuelve el problema 

set of int: NUMERO_MONEDAS = 1..8; % número de monedas distintas (hay 8 en total)
set of int: INDICES_MONEDAS_CENTIMO = 1..6; % Índices de las monedas de céntimo
set of int: INDICES_MONEDAS_EURO = 7..8; % Índices de las monedas de euro


array [NUMERO_MONEDAS] of int : VALOR_MONEDAS = [1, 2, 5, 10, 20, 50, 100, 200];
array [NUMERO_MONEDAS] of var int : CANTIDAD_MONEDAS;
float : IMPORTE; %% cantidad a buscar 

%% Calculamos la parte entera (que se corresponde con las monedas de euro) y la parte decimal (que son las de céntimo)
float : EUROS = floor(IMPORTE / 100); % parte entera
float : CENTIMOS = IMPORTE - EUROS; %parte decimal

%%%%%%%%%%%%% Restricciones  %%%%%%%%%%%%

% 1. Cantidades de monedas debe de ser un número natural (mayor o igual que cero)
constraint forall( i in NUMERO_MONEDAS)(CANTIDAD_MONEDAS[i] >= 0);
% 2. La suma de cantidad más valor debe ser el importe
%   2.1 Calculamos la parte entera que se corresponde a los euros
constraint sum( i in INDICES_MONEDAS_EURO )(VALOR_MONEDAS[i]*CANTIDAD_MONEDAS[i]) == EUROS;
%   2.2 Calculamos la parte decimal que se corresponde a los céntimos necesarios
constraint sum( i in INDICES_MONEDAS_CENTIMO )(VALOR_MONEDAS[i]*CANTIDAD_MONEDAS[i]) == CENTIMOS;

%%%%%%%%%%%% Imprimimos solución en pantalla %%%%%%%%%%%%
output
  ["El importe \(IMPORTE) se puede obtener como:\n"] ++
  ["\(CANTIDAD_MONEDAS[i]) monedas de \(VALOR_MONEDAS[i]) euros \n" | i in NUMERO_MONEDAS];
solve satisfy;