set of int: preferencia = 0..200;
var int: peso_max = 275;

% array para determinar si un objeto es seleccionado
array[1..12] of var 0..1: x;

% array de datos del enunciado
array[1..12] of int: peso = [9,13,153,50,15,68,27,39,23,52,11,32];
array[1..12] of int: pref = [150,35,200,160,60,45,60,40,30,10,70,30];

% restricción para que la solución verifique que la suma de los pesos de los objetos seleccionados es menor que la carga máxima
constraint sum(i in 1..12) (peso[i]*x[i]) <= peso_max;
% maximizamos la suma de preferencias de los objetos seleccionados
solve maximize sum (i in 1..12) (pref[i]*x[i]);

output ["x = ", show(x), "\n"];
% solucion = mapa, compas, agua, sandwich, azucar, queso, protector solar
% 705 preferencia