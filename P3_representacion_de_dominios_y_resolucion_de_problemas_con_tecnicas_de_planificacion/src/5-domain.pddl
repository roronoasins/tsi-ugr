(define (domain ej5)
	(:requirements :adl :typing)
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
							barracones extractor centro-mando bahia-ingenieria - tipo-edificio
							impulsor-segador - investigacion)

	(:predicates
		(en ?l - localizacion ?item - espaciales)
		(extrayendo ?u - unidad)
		(unidad-reclutada ?u - unidad)
		(edificio-necesita ?r - tipo-recurso ?b - tipo-edificio)
		(unidad-necesita ?r - tipo-recurso ?u - tipo-unidad)
		(investigacion-necesita ?r - tipo-recurso ?i - investigacion)
		(got ?r - tipo-recurso)
		(construido ?b - edificio)
		(conectado ?z1 ?z2 - localizacion)
		(recurso-es ?r - recurso ?tr - tipo-recurso)
		(unidad-es ?u - unidad ?tu - tipo-unidad)
		(edificio-es ?e - edificio ?te - tipo-edificio)
		(edificio-creador ?e - tipo-edificio ?u - tipo-unidad)
		(investigacion-realizada ?i - investigacion)
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

	(:action reclutar
	:parameters (?l ?lu - localizacion ?e - edificio ?te - tipo-edificio ?u - unidad ?tu - tipo-unidad)
	:precondition (and
											(en ?l ?e)
											(edificio-es ?e ?te)
											(not (en ?l ?u))
											(not (en ?lu ?u))
											(unidad-es ?u ?tu)
											(edificio-creador ?te ?tu)
											(or (and (unidad-necesita gas ?tu)
						            				(got gas))
		    					        (and (unidad-necesita mineral ?tu)
		    					            (got mineral))
		    					        (and (unidad-necesita ambos ?tu)
															(unidad-es ?u segador)
															(investigacion-realizada impulsor-segador)
		    					            (got mineral)
		    					            (got gas))
												)
								)


	:effect (and
							(en ?l ?u)
	            )
	)

	(:action investigar
	:parameters (?e - edificio ?te - tipo-edificio ?i - investigacion)
	:precondition (and
											(construido ?e)
											(edificio-es ?e bahia-ingenieria)
											(investigacion-necesita ambos ?i)
											(got mineral)
											(got gas)
								)

	:effect (and
								(investigacion-realizada ?i)
					)
	)


)
