import { Component, signal, inject } from '@angular/core';
import { MapaComponent } from '../mapa/mapa.component';
import { MenuModalComponent } from '../menu-modal/menu-modal.component';
import { CartService } from '../../services/cart.service';
import { Restaurante } from '../../models/restaurante.model';

@Component({
  selector: 'app-mapa-page',
  standalone: true,
  imports: [MapaComponent, MenuModalComponent],
  templateUrl: './mapa-page.component.html',
  styleUrls: ['./mapa-page.component.css']
})
export class MapaPageComponent {
  private readonly cartService = inject(CartService);

  selectedRestaurante = signal<Restaurante | null>(null);
  modalVisible = signal(false);

  onRestauranteSelected(restaurante: Restaurante): void {
    this.cartService.clear();
    this.selectedRestaurante.set(restaurante);
    this.modalVisible.set(true);
  }

  onCloseModal(): void {
    this.modalVisible.set(false);
  }

  onPedidoRealizado(): void {
    this.modalVisible.set(false);
  }
}
