include "all_different.mzn";

% Rango de las letras según su posición
set of int: DIGITO = 0..9;
set of int: PRIMER_DIGITO = 1..9;

% Cada letra tiene asociada una variable(dependiendo del tipo de dígito)
var PRIMER_DIGITO: T;
var DIGITO: E;
var DIGITO: S;
var PRIMER_DIGITO: F;
var PRIMER_DIGITO: D;
var DIGITO: I;
var DIGITO: N;
var PRIMER_DIGITO: K;
var DIGITO: A;
var DIGITO: R;

% Array de letras para poder ver que todas son distintas
array[1..10] of var int: letras = [T,E,S,F,D,I,N,K,A,R];

% restricción para verificar la suma del enunciado de acuerdo a la posición de cada letra(variable)
constraint   10000*T + 1000*E + 100*S + 10*T + E
           + 10000*F + 1000*E + 100*S + 10*T + E
           + 10000*D + 1000*E + 100*I + 10*N + E
= 100000*K + 10000*R + 1000*A + 100*F + 10*T + E;

% Restricción para verificar que todas son distintas
constraint all_different(letras);

solve satisfy;

output [" \(T)\(E)\(S)\(T)\(E)\n","+ \(F)\(E)\(S)\(T)\(E)\n","+ \(D)\(E)\(I)\(N)\(E)\n","= \(K)\(R)\(A)\(F)\(T)\(E)\n"];