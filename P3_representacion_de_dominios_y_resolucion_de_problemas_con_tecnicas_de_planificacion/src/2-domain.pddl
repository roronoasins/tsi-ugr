(define (domain ej2)
	(:requirements :adl :typing)
	(:types unidad edificio localizacion recurso - espaciales
	tipo-recurso
	tipo-edificio
	tipo-unidad
	)
	(:constants vce - tipo-unidad
							mineral gas - tipo-recurso
							barracones centro-mando extractor - tipo-edificio)

	(:predicates
		(en ?l - localizacion ?u - espaciales)
		(extrayendo ?id - unidad)
		(edificio-necesita ?r - tipo-recurso ?b - tipo-edificio)
		(got ?r - tipo-recurso)
		(construido ?b - edificio)
		(conectado ?l1 ?l2 - localizacion)
		(recurso-es ?r - recurso ?tr - tipo-recurso)
		(unidad-es ?u - unidad ?tu - tipo-unidad)
		(edificio-es ?e - edificio ?te - tipo-edificio)
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
	:parameters (?u - unidad ?zr - localizacion ?r - recurso ?e - edificio)
	:precondition (and
	                    	(en ?zr ?u)
												(unidad-es ?u vce)
                        (en ?zr ?r)
                        (not (extrayendo ?u))
	                    (or  (recurso-es ?r mineral) (and (recurso-es ?r gas) (en ?zr ?e) (edificio-es ?e extractor)))
                    )

	:effect (and
	            (when (recurso-es ?r mineral)
                    (and (extrayendo ?u)
                        (got mineral)
                    )
                )
                (when (recurso-es ?r gas)
                    (and (extrayendo ?u)
                        (got gas)
                    )
                )
	        )
	)

	(:action construir
	:parameters (?lc - localizacion ?u - unidad ?e - edificio ?te - tipo-edificio ?r - recurso)
	:precondition (and
											(en ?lc ?u)
											(unidad-es ?u vce)
											(not (extrayendo ?u))
											(not (construido ?e))
											(not (exists
												(?ed - edificio)
														(en ?lc ?ed)
											))
											(edificio-es ?e ?te)
											(edificio-necesita mineral ?te)
											(got mineral)
						)

	:effect (and
							(when (and (edificio-es ?e extractor) (en ?lc ?r) (recurso-es ?r gas))
										(and (en ?lc ?e) (construido ?e) )
							)
							(when (or (edificio-es ?e centro-mando) (edificio-es ?e barracones))
										(and (en ?lc ?e) (construido ?e) )
							)
					)
	)

)
