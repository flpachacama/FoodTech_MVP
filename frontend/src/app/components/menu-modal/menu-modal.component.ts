import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Restaurante } from '../../models/restaurante.model';
import { ProductoMenu } from '../../models/producto-menu.model';

@Component({
  selector: 'app-menu-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu-modal.component.html',
  styleUrls: ['./menu-modal.component.css']
})
export class MenuModalComponent {
  @Input() restaurante: Restaurante | null = null;
  @Input() visible: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() agregarProducto = new EventEmitter<ProductoMenu>();

  /**
   * Cierra el modal
   */
  onClose(): void {
    this.close.emit();
  }

  /**
   * Cierra el modal al hacer click en el overlay
   * @param event MouseEvent
   */
  onOverlayClick(event: MouseEvent): void {
    // Solo cierra si el click es directamente en el overlay
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.onClose();
    }
  }

  /**
   * Emite el evento de agregar producto
   * @param producto ProductoMenu seleccionado
   */
  onAgregarProducto(producto: ProductoMenu): void {
    this.agregarProducto.emit(producto);
    console.log('Producto agregado:', producto);
  }

  /**
   * Formatea el precio en formato colombiano
   * @param precio Precio del producto
   * @returns String formateado como $18.000
   */
  formatPrice(precio: number): string {
    return '$' + precio.toLocaleString('es-CO', { 
      minimumFractionDigits: 0,
      maximumFractionDigits: 0 
    });
  }
}
