package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestauranteResponseDtoTest {

    @Test
    void builder_creaObjetoConCamposCorrectos() {
        ProductoMenuDto producto = ProductoMenuDto.builder().id(1L).nombre("Pizza").precio(15.0).build();
        RestauranteResponseDto dto = RestauranteResponseDto.builder()
                .id(1L)
                .nombre("La Parrilla")
                .coordenadaX(4.6)
                .coordenadaY(-74.0)
                .menu(List.of(producto))
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("La Parrilla", dto.getNombre());
        assertEquals(4.6, dto.getCoordenadaX());
        assertEquals(-74.0, dto.getCoordenadaY());
        assertEquals(1, dto.getMenu().size());
    }

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        RestauranteResponseDto dto = new RestauranteResponseDto(2L, "Sushi House", 5.0, -73.0, List.of());

        assertEquals(2L, dto.getId());
        assertEquals("Sushi House", dto.getNombre());
        assertTrue(dto.getMenu().isEmpty());
    }

    @Test
    void noArgsConstructorYSetters_asignanValoresCorrectamente() {
        RestauranteResponseDto dto = new RestauranteResponseDto();
        dto.setId(3L);
        dto.setNombre("Burger");
        dto.setCoordenadaX(1.0);
        dto.setCoordenadaY(2.0);
        dto.setMenu(List.of());

        assertEquals(3L, dto.getId());
        assertEquals("Burger", dto.getNombre());
        assertEquals(1.0, dto.getCoordenadaX());
        assertEquals(2.0, dto.getCoordenadaY());
    }

    @Test
    void equals_dosObjetosIguales_retornaTrue() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_objetosDiferentes_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest1", 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(2L, "Rest2", 3.0, 4.0, null);

        assertNotEquals(a, b);
    }

    @Test
    void toString_contieneValoresClave() {
        RestauranteResponseDto dto = new RestauranteResponseDto(10L, "Italiano", 5.5, -74.5, List.of());

        String str = dto.toString();

        assertTrue(str.contains("10"));
        assertTrue(str.contains("Italiano"));
    }
}
