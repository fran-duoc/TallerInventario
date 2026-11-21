package com.taller.inventario.backend.config;

import com.taller.inventario.backend.model.entity.Producto;
import com.taller.inventario.backend.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(ProductoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<Producto> productosIniciales = List.of(
                        new Producto(
                                "LUB-CAST-5W30",
                                "Aceite Sintético Castrol EDGE 5W-30",
                                "Castrol",
                                "Aceites y Lubricantes",
                                "Bidón 4L",
                                14,
                                5,
                                28990.0,
                                39990.0,
                                "Sector A - Lubricantes",
                                "Estante 01",
                                "Nivel 2",
                                "Castrol Chile Oficial",
                                "DISPONIBLE"
                        ),
                        new Producto(
                                "FIL-MANN-W712",
                                "Filtro de Aceite Blindado W712/22",
                                "Mann Filter",
                                "Filtros",
                                "Unidad",
                                3,
                                6,
                                4990.0,
                                8990.0,
                                "Sector B - Filtración",
                                "Estante 03",
                                "Nivel 1",
                                "Distribuidora Automotriz Sur",
                                "BAJO_STOCK"
                        ),
                        new Producto(
                                "FRE-BOSCH-DOT4",
                                "Líquido de Frenos DOT 4 Racing 500ml",
                                "Bosch",
                                "Frenos",
                                "Unidad",
                                8,
                                4,
                                6500.0,
                                10500.0,
                                "Sector C - Frenos",
                                "Estante 02",
                                "Nivel 3",
                                "Bosch Car Service Central",
                                "DISPONIBLE"
                        ),
                        new Producto(
                                "BUJ-DENSO-IK20",
                                "Bujías de Encendido Iridium Power IK20",
                                "Denso",
                                "Encendido",
                                "Juego 4 Unid",
                                0,
                                3,
                                18990.0,
                                27990.0,
                                "Sector D - Eléctrico",
                                "Estante 01",
                                "Nivel 4",
                                "Repuestos Puente Alto SpA",
                                "AGOTADO"
                        ),
                        new Producto(
                                "FRE-BREM-P060",
                                "Pastillas de Freno Cerámicas Delanteras Brembo",
                                "Brembo",
                                "Frenos",
                                "Kit",
                                5,
                                2,
                                34990.0,
                                49990.0,
                                "Sector C - Frenos",
                                "Estante 02",
                                "Nivel 1",
                                "Importadora Frenos Premium",
                                "DISPONIBLE"
                        )
                );
                repository.saveAll(productosIniciales);
            }
        };
    }
}
