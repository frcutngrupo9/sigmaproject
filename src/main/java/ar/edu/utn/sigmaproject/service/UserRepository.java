package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.User;

@Repository
public interface UserRepository extends SearchableRepository<User, Long> {

	User findByAccount(String account);
	
}
