include "globals.mzn";
set of int: valor = 0..9;
array[0..9] of var valor: numero;

% restricción para verificar que cada ocurrencia de i es reflejada en el numero de la posición i del array
constraint forall(i in 0..9)(count(numero,i)==numero[i]);

solve satisfy;