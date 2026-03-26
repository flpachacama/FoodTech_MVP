package com.foodtech.order.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.order.domain.model.EstadoPedido;
import com.foodtech.order.domain.model.Pedido;
import com.foodtech.order.domain.model.ProductoPedido;
import com.foodtech.order.infrastructure.persistence.PedidoJpaRepository;
import com.foodtech.order.infrastructure.persistence.entity.PedidoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoDataAdapterTest {

    @Mock
    private PedidoJpaRepository pedidoJpaRepository;

    @InjectMocks
    private PedidoDataAdapter adapter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void debeGuardarYPasarARepository_cuandoPedidoValido() {
        ProductoPedido prod = new ProductoPedido(1L, "Pizza", null);
        Pedido pedido = new Pedido(100L, EstadoPedido.PENDIENTE, 10L, List.of(prod), 20L, "Cliente", 1, 2, 15);

        PedidoEntity returned = new PedidoEntity(100L, EstadoPedido.PENDIENTE, 10L, 20L, "Cliente", 1, 2, 15, "[{\"id\":1,\"nombre\":\"Pizza\",\"precio\":null}]");
        when(pedidoJpaRepository.save(any())).thenReturn(returned);

        Pedido saved = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).save(pedido);

        assertNotNull(saved);
        assertEquals(100L, saved.getId());
        assertEquals(1, saved.getProductos().size());
        verify(pedidoJpaRepository, times(1)).save(any());
    }

    @Test
    void debeSerializarProductosComoListaVacia_cuandoProductosNull() {
        Pedido pedido = new Pedido(101L, EstadoPedido.PENDIENTE, 11L, null, 21L, "Anon", null, null, 0);

        ArgumentCaptor<PedidoEntity> captor = ArgumentCaptor.forClass(PedidoEntity.class);
        when(pedidoJpaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new PedidoDataAdapter(pedidoJpaRepository, objectMapper).save(pedido);

        verify(pedidoJpaRepository).save(captor.capture());
        PedidoEntity sent = captor.getValue();
        assertEquals("[]", sent.getProductos());
    }

    @Test
    void debeMapearDesdeRepository_cuandoExiste() {
        PedidoEntity entity = new PedidoEntity(200L, EstadoPedido.ASIGNADO, 30L, 40L, "C", 1, 2, 20, "[]");
        when(pedidoJpaRepository.findById(200L)).thenReturn(Optional.of(entity));

        Optional<Pedido> found = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findById(200L);

        assertTrue(found.isPresent());
        assertEquals(200L, found.get().getId());
    }

    @Test
    void debeMapearListaDesdeRepository_cuandoHayRegistros() {
        PedidoEntity e1 = new PedidoEntity(201L, EstadoPedido.PENDIENTE, 31L, 41L, "C2", null, null, 5, "[]");
        when(pedidoJpaRepository.findAll()).thenReturn(List.of(e1));

        List<Pedido> lista = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findAll();

        assertEquals(1, lista.size());
        assertEquals(201L, lista.get(0).getId());
    }

    @Test
    void debeLanzarIllegalStateException_cuandoSerializacionFalla_enSave() throws Exception {
        ProductoPedido prod = new ProductoPedido(2L, "Taco", null);
        Pedido pedido = new Pedido(102L, EstadoPedido.PENDIENTE, 12L, List.of(prod), 22L, "Cliente2", null, null, 10);

        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        when(mockMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom"){});

        PedidoDataAdapter adapterWithBadMapper = new PedidoDataAdapter(pedidoJpaRepository, mockMapper);

        assertThrows(IllegalStateException.class, () -> adapterWithBadMapper.save(pedido));
        verify(pedidoJpaRepository, never()).save(any());
    }

    @Test
    void debeLanzarIllegalStateException_cuandoDeserializacionFalla_enFindAll() throws Exception {
        PedidoEntity e1 = new PedidoEntity(300L, EstadoPedido.PENDIENTE, 31L, 41L, "C2", null, null, 5, "[{\"id\":1}]");
        when(pedidoJpaRepository.findAll()).thenReturn(List.of(e1));

        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        when(mockMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(new JsonProcessingException("boom"){});

        PedidoDataAdapter adapterWithBadMapper = new PedidoDataAdapter(pedidoJpaRepository, mockMapper);

        assertThrows(IllegalStateException.class, adapterWithBadMapper::findAll);
    }

    @Test
    void findById_debeRetornarVacio_cuandoNoExiste() {
        when(pedidoJpaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pedido> r = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findById(999L);
        assertTrue(r.isEmpty());
    }

    @Test
    void deserialize_debeRetornarListaVacia_cuandoProductosBlank() {
        PedidoEntity e = new PedidoEntity(400L, EstadoPedido.PENDIENTE, 31L, 41L, "C2", null, null, 5, "   ");
        when(pedidoJpaRepository.findAll()).thenReturn(List.of(e));

        List<Pedido> lista = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findAll();
        assertEquals(1, lista.size());
        assertTrue(lista.get(0).getProductos().isEmpty());
    }

}
