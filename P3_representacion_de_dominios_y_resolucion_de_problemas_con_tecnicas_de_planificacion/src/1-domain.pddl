(define (domain ej1)
	(:requirements :adl :typing)
	(:types unidad edificio localizacion recurso - espaciales
	tipo-recurso
	tipo-edificio
	tipo-unidad
	)
	(:constants vce - tipo-unidad
							mineral gas - tipo-recurso
							barracones centro-mando - tipo-edificio)

	(:predicates
		(en ?l - localizacion ?u - espaciales)
		(extrayendo ?id - unidad)
		(edificio-necesita ?r - tipo-recurso ?b - tipo-edificio)
		(got ?r - tipo-recurso)
		(construido ?b - edificio)
		(conectado ?l1 ?l2 - localizacion)
		(recurso-es ?r - recurso ?tr - tipo-recurso)
		(unidad-es ?u - unidad ?tu - tipo-unidad)
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
	:parameters (?u - unidad ?zr - localizacion ?r - recurso ?tr - tipo-recurso)
	:precondition (and (en ?zr ?u)
											(unidad-es ?u vce)
											(en ?zr ?r)
											(not (extrayendo ?u)))

	:effect (and (extrayendo ?u)
				(got ?tr))
	)

	(:action construir
	:parameters (?lc - localizacion ?u - unidad ?e - edificio ?te - tipo-edificio)
	:precondition (and
	                    (en ?lc ?u)
											(unidad-es ?u vce)
											(not (extrayendo ?u))
						    			(not (construido ?e))
											(not (exists
										    (?ed - edificio)
										        (en ?lc ?ed)
											))
											(edificio-necesita mineral ?te)
											(got mineral)
				    )

	:effect (and
							(en ?lc ?e)
	            (construido ?e)
	            )
	)

)
