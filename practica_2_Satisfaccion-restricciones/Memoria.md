# Práctica 2 

Autor: Blanca Cano Camarero 


## Problema de las monedas    

### Apartado a
Se desea calcular un conjunto de monedas cuyo importe sea exactamente una cantidad dada. 

Se usarán monedas de $1,2,5, 10, 20, 50$ céntimos y $1,2$ euros. 

#### Solución aportada 

Traducimos todas las cantidades a euros, es decir, que las monedas posibles tienen un valor 
$$C= \{0.01;  0.02; 0.05; 0.1; 0.2; 0.5; 1; 2\}.$$

Si denotamos $c_{a}$ en número de monedas con valor 
$a \in C$ se tiene que para un importe $i$ el problema debe de satisfacer que: 

$$\sum_{a \in C} c_a a = i.$$