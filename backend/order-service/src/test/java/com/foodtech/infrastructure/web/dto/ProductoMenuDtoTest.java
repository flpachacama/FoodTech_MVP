package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoMenuDtoTest {

    @Test
    void builder_creaObjetoConCamposCorrectos() {
        ProductoMenuDto dto = ProductoMenuDto.builder()
                .id(1L)
                .nombre("Pizza Margherita")
                .precio(15.99)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Pizza Margherita", dto.getNombre());
        assertEquals(15.99, dto.getPrecio());
    }

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        ProductoMenuDto dto = new ProductoMenuDto(2L, "Burger", 12.50);

        assertEquals(2L, dto.getId());
        assertEquals("Burger", dto.getNombre());
        assertEquals(12.50, dto.getPrecio());
    }

    @Test
    void noArgsConstructorYSetters_asignanValoresCorrectamente() {
        ProductoMenuDto dto = new ProductoMenuDto();
        dto.setId(3L);
        dto.setNombre("Sushi");
        dto.setPrecio(20.0);

        assertEquals(3L, dto.getId());
        assertEquals("Sushi", dto.getNombre());
        assertEquals(20.0, dto.getPrecio());
    }

    @Test
    void equals_dosObjetosIguales_retornaTrue() {
        ProductoMenuDto a = new ProductoMenuDto(1L, "Taco", 8.0);
        ProductoMenuDto b = new ProductoMenuDto(1L, "Taco", 8.0);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_objetosDiferentes_retornaFalse() {
        ProductoMenuDto a = new ProductoMenuDto(1L, "Taco", 8.0);
        ProductoMenuDto b = new ProductoMenuDto(2L, "Burrito", 10.0);

        assertNotEquals(a, b);
    }

    @Test
    void toString_contieneValoresClave() {
        ProductoMenuDto dto = new ProductoMenuDto(5L, "Empanada", 3.5);

        String str = dto.toString();

        assertTrue(str.contains("5"));
        assertTrue(str.contains("Empanada"));
        assertTrue(str.contains("3.5"));
    }
}
