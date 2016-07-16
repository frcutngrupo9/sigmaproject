package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.Client;

/**
*
* @author gfzabarino
*/
@Repository
public interface ClientRepository extends SearchableRepository<Client, Long> {

	Client findFirstByName(String name);

}
