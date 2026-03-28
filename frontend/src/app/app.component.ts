import { Component, signal } from '@angular/core';
import { MapaComponent } from './components/mapa/mapa.component';
import { MenuModalComponent } from './components/menu-modal/menu-modal.component';
import { Restaurante } from './models/restaurante.model';

@Component({
  selector: 'app-root',
  imports: [MapaComponent, MenuModalComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  // Signals para manejar el estado de la aplicación
  selectedRestaurante = signal<Restaurante | null>(null);
  modalVisible = signal<boolean>(false);

  /**
   * Maneja la selección de un restaurante desde el mapa
   * @param restaurante Restaurante seleccionado
   */
  onRestauranteSelected(restaurante: Restaurante): void {
    this.selectedRestaurante.set(restaurante);
    this.modalVisible.set(true);
    console.log('Restaurante seleccionado:', restaurante);
  }

  /**
   * Cierra el modal de menú
   */
  onCloseModal(): void {
    this.modalVisible.set(false);
    // Opcional: limpiar el restaurante seleccionado después de cerrar
    // this.selectedRestaurante.set(null);
  }
}
