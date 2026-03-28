import { Component, signal, inject } from '@angular/core';
import { MapaComponent } from '../mapa/mapa.component';
import { MenuModalComponent } from '../menu-modal/menu-modal.component';
import { OrderFormModalComponent } from '../order-form-modal/order-form-modal.component';
import { CartService } from '../../services/cart.service';
import { Restaurante } from '../../models/restaurante.model';

@Component({
  selector: 'app-mapa-page',
  standalone: true,
  imports: [MapaComponent, MenuModalComponent, OrderFormModalComponent],
  templateUrl: './mapa-page.component.html',
  styleUrls: ['./mapa-page.component.css']
})
export class MapaPageComponent {
  private readonly cartService = inject(CartService);

  selectedRestaurante = signal<Restaurante | null>(null);
  menuVisible = signal(false);
  orderFormVisible = signal(false);

  onRestauranteSelected(restaurante: Restaurante): void {
    this.cartService.clear();
    this.cartService.setRestaurante(restaurante);
    this.selectedRestaurante.set(restaurante);
    this.menuVisible.set(true);
  }

  onCloseMenu(): void {
    this.menuVisible.set(false);
  }

  onPedidoRealizado(): void {
    this.menuVisible.set(false);
    this.orderFormVisible.set(true);
  }

  onCloseOrderForm(): void {
    this.orderFormVisible.set(false);
    this.cartService.clear();
  }

  onPedidoConfirmado(): void {
    this.orderFormVisible.set(false);
  }
}
