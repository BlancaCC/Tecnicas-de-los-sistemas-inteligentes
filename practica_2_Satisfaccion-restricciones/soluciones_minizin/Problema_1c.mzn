% Problema P1 Problema de las monedas 

% Autora: Blanca Cano Camarero 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 

include "globals.mzn";
 
%%%%%%%%%%%% Declaración de variables  %%%%%%%%%%%%
%% Estructura de datos que resuelve el problema 

set of int: NUMERO_MONEDAS = 1..8; % número de monedas distintas (hay 8 en total)
set of int: INDICES_MONEDAS_CENTIMO = 1..6; % Índices de las monedas de céntimo
set of int: INDICES_MONEDAS_EURO = 1..2; % Índices de las monedas de euro

% Estructuras que almacena valores y cantidad de monedas
array [INDICES_MONEDAS_CENTIMO] of int : VALOR_MONEDAS_CENTIMOS  = [1, 2, 5, 10, 20, 50]; 
array [INDICES_MONEDAS_CENTIMO] of var int : CANTIDAD_MONEDAS_CENTIMOS;
array [INDICES_MONEDAS_EURO] of int : VALOR_MONEDAS_EURO  = [1, 2]; 
array [INDICES_MONEDAS_EURO] of var int : CANTIDAD_MONEDAS_EURO;

int : IMPORTE; %% cantidad a buscar
var int :  CANTIDAD_MONEDAS_ACUMULADAS;  % variable que trataremos de minimizar 

%% Calculamos la parte entera (que se corresponde con las monedas de euro) y la parte decimal (que son las de céntimo)
int : EUROS = floor(IMPORTE / 100); % parte entera
int : CENTIMOS = IMPORTE - 100*EUROS; %parte decimal

%%%%%%%%%%%%% Restricciones  %%%%%%%%%%%%

% 1. Cantidades de monedas debe de ser un número natural (mayor o igual que cero)
constraint forall( i in INDICES_MONEDAS_EURO)(CANTIDAD_MONEDAS_EURO[i] >= 0) /\  forall( i in INDICES_MONEDAS_CENTIMO)(CANTIDAD_MONEDAS_CENTIMOS[i] >= 0);
% 2. La suma de cantidad más valor debe ser el importe
%   2.1 Calculamos la parte entera que se corresponde a los euros
constraint sum( i in INDICES_MONEDAS_EURO )(VALOR_MONEDAS_EURO[i]*CANTIDAD_MONEDAS_EURO[i]) == EUROS;
%   2.2 Calculamos la parte decimal que se corresponde a los céntimos necesarios
constraint sum( i in INDICES_MONEDAS_CENTIMO )(VALOR_MONEDAS_CENTIMOS[i]*CANTIDAD_MONEDAS_CENTIMOS[i]) == CENTIMOS;
% 3. Contabilizamos las monedas actuales 
constraint CANTIDAD_MONEDAS_ACUMULADAS = sum( i in INDICES_MONEDAS_EURO )(CANTIDAD_MONEDAS_EURO[i]) + sum( i in INDICES_MONEDAS_CENTIMO )(CANTIDAD_MONEDAS_CENTIMOS[i]);


%%%%%%%%%%%% Imprimimos solución en pantalla %%%%%%%%%%%%
output
    ["El importe \(IMPORTE) se puede obtener como \(CANTIDAD_MONEDAS_ACUMULADAS) monedas con la siguiente distribución:\n"] ++
    ["\(CANTIDAD_MONEDAS_CENTIMOS[i]) monedas de \(VALOR_MONEDAS_CENTIMOS[i]) céntimos \n" | i in INDICES_MONEDAS_CENTIMO]
   ++
    ["\(CANTIDAD_MONEDAS_EURO[i]) monedas de \(VALOR_MONEDAS_EURO[i]) euros\n" | i in INDICES_MONEDAS_EURO] 
;
solve minimize CANTIDAD_MONEDAS_ACUMULADAS;

