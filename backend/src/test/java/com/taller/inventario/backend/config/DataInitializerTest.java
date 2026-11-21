package com.taller.inventario.backend.config;

import com.taller.inventario.backend.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    @Test
    void testInitDatabase_WhenEmpty_ShouldSaveDefaults() throws Exception {
        ProductoRepository repository = mock(ProductoRepository.class);
        when(repository.count()).thenReturn(0L);

        DataInitializer initializer = new DataInitializer();
        CommandLineRunner runner = initializer.initDatabase(repository);
        runner.run();

        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void testInitDatabase_WhenNotEmpty_ShouldNotSave() throws Exception {
        ProductoRepository repository = mock(ProductoRepository.class);
        when(repository.count()).thenReturn(5L);

        DataInitializer initializer = new DataInitializer();
        CommandLineRunner runner = initializer.initDatabase(repository);
        runner.run();

        verify(repository, never()).saveAll(anyList());
    }
}
