(define (domain ej3)
	(:requirements :adl :typing)
	(:types
		unidad edificio recurso - espaciales
		localizacion
		tipo-unidad
		tipo-edificio
		tipo-recurso
	)
	(:constants vce - tipo-unidad
							mineral gas ambos - tipo-recurso
							barracones extractor centro-mando - tipo-edificio)

	(:predicates
		(en ?l - localizacion ?item - espaciales)
		(extrayendo ?u - unidad)
		(edificio-necesita ?r - tipo-recurso ?b - tipo-edificio)
		(got ?r - tipo-recurso)
		(construido ?b - edificio)
		(conectado ?z1 ?z2 - localizacion)
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
												(edificio-es ?e ?te)
												(unidad-es ?u vce)
												(not (extrayendo ?u))
    					    			(not (construido ?e))
												(not (exists
													(?ed - edificio)
															(en ?lc ?ed)
												))
		                    (or (and (edificio-necesita gas ?te)
    					            				(got gas))
			    					        (and (edificio-necesita mineral ?te)
			    					            (got mineral))
			    					        (and (edificio-necesita ambos ?te)
			    					            (got mineral)
			    					            (got gas))
													)
					    )

		:effect (and
                (when (and (edificio-es ?e extractor) (en ?lc ?r) (recurso-es ?r gas))
                  (and (en ?lc ?e) (construido ?e) )
								)

								(when (edificio-necesita mineral ?te)
                  (and (en ?lc ?e) (construido ?e) )
								)

                (when (edificio-necesita ambos ?te)
                    (and (en ?lc ?e) (construido ?e) )
								)
            )
	)

)
