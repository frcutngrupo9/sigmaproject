package ar.edu.utn.sigmaproject.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.sigmaproject.domain.OrderDetail;
import ar.edu.utn.sigmaproject.domain.Product;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	OrderDetail findFirstByProduct(Product product);

}
