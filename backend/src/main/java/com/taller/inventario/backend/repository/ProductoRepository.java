package com.taller.inventario.backend.repository;

import com.taller.inventario.backend.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.codigo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.categoria) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY p.nombre ASC")
    List<Producto> searchProductos(@Param("query") String query);

    @Query("SELECT p FROM Producto p WHERE p.cantidad <= p.stockMinimo ORDER BY p.cantidad ASC")
    List<Producto> findProductosBajoStock();

    List<Producto> findByCategoriaIgnoreCaseOrderByNombreAsc(String categoria);
}
