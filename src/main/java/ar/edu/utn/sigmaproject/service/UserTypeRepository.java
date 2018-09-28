package ar.edu.utn.sigmaproject.service;

import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.UserType;

@Repository
public interface UserTypeRepository extends SearchableRepository<UserType, Long> {

	public UserType findFirstByName(String string);

}