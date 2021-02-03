(define (domain ej6)
	(:requirements :adl :typing :fluents)
	(:types
		unidad edificio recurso - espaciales
		localizacion
		investigacion
		tipo-unidad
		tipo-edificio
		tipo-recurso
	)
	(:constants vce marine segador - tipo-unidad
							mineral gas ambos - tipo-recurso
							barracones extractor centro-mando bahia-ingenieria deposito - tipo-edificio
							impulsor-segador - investigacion)

	(:predicates
		(en ?l - localizacion ?item - espaciales)
		(extrayendo ?u - unidad)
		(unidad-reclutada ?u - unidad)
		(construido ?b - edificio)
		(conectado ?z1 ?z2 - localizacion)
		(recurso-es ?r - recurso ?tr - tipo-recurso)
		(unidad-es ?u - unidad ?tu - tipo-unidad)
		(edificio-es ?e - edificio ?te - tipo-edificio)
		(edificio-creador ?e - tipo-edificio ?u - tipo-unidad)
		(investigacion-realizada ?i - investigacion)
	)

	(:functions
		(cantidad-recurso ?r - tipo-recurso)
		(cantidad-edificio ?e - tipo-edificio ?r - tipo-recurso)
		(cantidad-unidad ?u - tipo-unidad ?r - tipo-recurso)
		(cantidad-investigacion ?i - investigacion ?r - tipo-recurso)
		(cantidad-recolectada)
		(cantidad-maxima)
		(recolectores-recurso ?r - tipo-recurso)
	)

	(:action navegar
	:parameters (?u - unidad ?zu ?z - localizacion)
	:precondition (and (not (en ?z ?u))
	                    (en ?zu ?u)
											(conectado ?zu ?z)
											(not (extrayendo ?u)))

	:effect (and (en ?z ?u)
				(not (en ?zu ?u)))
	)

	(:action asignar
	:parameters (?u - unidad ?zr - localizacion ?r - recurso ?tr - tipo-recurso ?e - edificio)
	:precondition (and
	                    	(en ?zr ?u)
												(unidad-es ?u vce)
                        (en ?zr ?r)
                        (not (extrayendo ?u))
												(recurso-es ?r ?tr)
	                    (or  (recurso-es ?r mineral) (and (recurso-es ?r gas) (en ?zr ?e) (edificio-es ?e extractor)))
                    )

	:effect (and
							(extrayendo ?u)
							(increase (recolectores-recurso ?tr) 1)
	        )
	)

	(:action desasignar
	:parameters (?u - unidad ?zr - localizacion ?r - recurso ?tr - tipo-recurso)
	:precondition (and (en ?zr ?u) (en ?zr ?r) (recurso-es ?r ?tr) (extrayendo ?u) )

	:effect (and (not (extrayendo ?u)) (decrease (recolectores-recurso ?tr) 1) )
	)



	(:action extraer
	:parameters (?r - recurso ?tr - tipo-recurso)
	:precondition (and
											(recurso-es ?r ?tr)
											(> (recolectores-recurso ?tr) 0)
											(<= (+ (cantidad-recurso ?tr) (* (cantidad-recolectada) (recolectores-recurso ?tr))) (cantidad-maxima))
								)

	:effect (and (increase (cantidad-recurso ?tr) (* (cantidad-recolectada) (recolectores-recurso ?tr))))
	)

	(:action construir
	:parameters (?lc - localizacion ?u - unidad ?e - edificio ?te - tipo-edificio ?r - recurso)
	:precondition (and
											(en ?lc ?u)
											(edificio-es ?e ?te)
											(unidad-es ?u vce)
											(not (extrayendo ?u))
											(not (construido ?e))
											(not (exists
												(?ed - edificio)
														(en ?lc ?ed)
											))

								 			(>= (cantidad-recurso gas) (cantidad-edificio ?te gas))
											(>= (cantidad-recurso mineral) (cantidad-edificio ?te mineral))

						)

	:effect (and
								(when
									(and (edificio-es ?e extractor) (en ?lc ?r) (recurso-es ?r gas))
									(and
										(en ?lc ?e)
										(construido ?e)
										(decrease (cantidad-recurso gas) (cantidad-edificio ?te gas))
										(decrease (cantidad-recurso mineral) (cantidad-edificio ?te mineral))
									)
								)
								(when
									(edificio-es ?e deposito)
									(and
										(en ?lc ?e)
										(construido ?e)
										(decrease (cantidad-recurso gas) (cantidad-edificio ?te gas))
										(decrease (cantidad-recurso mineral) (cantidad-edificio ?te mineral))
										(increase (cantidad-maxima) (cantidad-maxima))
									)
								)
								(when
									(and (not (edificio-es ?e extractor)) (not (edificio-es ?e deposito)) )
									(and
										(en ?lc ?e)
										(construido ?e)
										(decrease (cantidad-recurso gas) (cantidad-edificio ?te gas))
										(decrease (cantidad-recurso mineral) (cantidad-edificio ?te mineral))
									)
								)

					)
)

	(:action reclutar
	:parameters (?l - localizacion ?e - edificio ?te - tipo-edificio ?u - unidad ?tu - tipo-unidad)
	:precondition (and
											(en ?l ?e)
											(edificio-es ?e ?te)
											(not (en ?l ?u))
											(not (unidad-reclutada ?u))
											(unidad-es ?u ?tu)
											(or
												(and
													(unidad-es ?u segador)
													(investigacion-realizada impulsor-segador)
												)
												(not (unidad-es ?u segador))
											)
											(edificio-creador ?te ?tu)
											(>= (cantidad-recurso gas) (cantidad-unidad ?tu gas))
											(>= (cantidad-recurso mineral) (cantidad-unidad ?tu mineral))
								)


	:effect (and
							(en ?l ?u)
							(unidad-reclutada ?u)
							(decrease (cantidad-recurso gas) (cantidad-unidad ?tu gas))
							(decrease (cantidad-recurso mineral) (cantidad-unidad ?tu mineral))
	            )
	)

	(:action investigar
	:parameters (?e - edificio ?te - tipo-edificio ?i - investigacion)
	:precondition (and
											(construido ?e)
											(edificio-es ?e bahia-ingenieria)
											(>= (cantidad-recurso gas) (cantidad-investigacion ?i gas))
											(>= (cantidad-recurso mineral) (cantidad-investigacion ?i mineral))
								)

	:effect (and
								(investigacion-realizada ?i)
								(decrease (cantidad-recurso gas) (cantidad-investigacion ?i gas))
								(decrease (cantidad-recurso mineral) (cantidad-investigacion ?i mineral))
					)
	)


)
