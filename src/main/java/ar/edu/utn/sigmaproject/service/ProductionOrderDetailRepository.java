package ar.edu.utn.sigmaproject.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;

@Repository
public interface ProductionOrderDetailRepository extends JpaRepository<ProductionOrderDetail, Long> {
	
	public List<ProductionOrderDetail> findByDateFinishAfterAndDateStartBefore(Date dateStart, Date dateFinish);
	
	/*
	 * deberia crearse un metodo que busque los detalles que se superponen pero que solo busque en los planes u ordenes
	 * que no estan finalizados o cancelados o que no tienen asignado un recurso
	@Query("select u from ProductionOrderDetail u where u.dateFinish after %?1 and u.dateStart before %?2")
	public List<ProductionOrderDetail> findOverlappingDetailDates(Date dateStart, Date dateFinish);
	
	@Query("SELECT p FROM ProductionOrderDetail p WHERE LOWER(p.lastName) = LOWER(:lastName)")
    public List<ProductionOrderDetail> find(@Param("lastName") String lastName);
    */
	
}