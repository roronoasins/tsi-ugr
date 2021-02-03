include "all_different.mzn";
set of int: profesores = 1..6;

set of int: profesor1 = 11..14;
set of int: profesor2 = 11..12;
set of int: profesor3 = 10..13;
set of int: profesor4 = 10..12;
set of int: profesor5 = 11..12;
set of int: profesor6 = 9..14;

var profesor1: hora_profesor1;
var profesor2: hora_profesor2;
var profesor3: hora_profesor3;
var profesor4: hora_profesor4;
var profesor5: hora_profesor5;
var profesor6: hora_profesor6;
% array del horario con las seis horas necesarias
array[profesores] of var int: horario = [hora_profesor1,hora_profesor2,hora_profesor3,hora_profesor4,hora_profesor5,hora_profesor6];

% restricción para seleccionar horas distintas para cada profesor
constraint all_different(horario);
solve satisfy;
