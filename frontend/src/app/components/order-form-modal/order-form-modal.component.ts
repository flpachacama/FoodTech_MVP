import { Component, Input, Output, EventEmitter, inject, signal, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { OrderRequest } from '../../models/order-request.model';

@Component({
  selector: 'app-order-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './order-form-modal.component.html',
  styleUrls: ['./order-form-modal.component.css']
})
export class OrderFormModalComponent implements OnChanges {
  @Input() visible = false;
  @Output() close = new EventEmitter<void>();
  @Output() pedidoConfirmado = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  public readonly cartService = inject(CartService);
  private readonly orderService = inject(OrderService);

  enviando = signal(false);
  error = signal<string | null>(null);

  form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    telefono: ['', [Validators.required, Validators.pattern(/^[0-9]+$/), Validators.minLength(7)]],
    ubicacionX: [null, [Validators.required, Validators.min(0), Validators.max(100)]],
    ubicacionY: [null, [Validators.required, Validators.min(0), Validators.max(100)]]
  });

  ngOnChanges(): void {
    if (this.visible) {
      this.form.reset();
      this.error.set(null);
    }
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.onClose();
    }
  }

  onClose(): void {
    this.close.emit();
  }

  getControl(name: string): AbstractControl {
    return this.form.get(name)!;
  }

  isInvalid(name: string): boolean {
    const ctrl = this.getControl(name);
    return ctrl.invalid && ctrl.touched;
  }

  onConfirmar(): void {
    console.log("confirmar pedido")
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const restaurante = this.cartService.restaurante();
    console.log("confirmar pedido 2", restaurante)
    if (!restaurante) return;
        

    const productos = this.cartService.items().flatMap(item =>
      Array.from({ length: item.cantidad }, () => item.producto)
    );

    const { nombre, telefono, ubicacionX, ubicacionY } = this.form.value;

    const order: OrderRequest = {
      restauranteId: restaurante.id,
      restauranteX: restaurante.coordenadaX,
      restauranteY: restaurante.coordenadaY,
      clima: this.orderService.getRandomClima(),
      productos,
      clienteId: Math.floor(Math.random() * 9000) + 1000,
      clienteNombre: nombre,
      clienteCoordenadasX: Number(ubicacionX),
      clienteCoordenadasY: Number(ubicacionY),
      clienteTelefono: telefono
    };
    console.log("orden", order)
    this.enviando.set(true);
    this.error.set(null);

    this.orderService.crearPedido(order).subscribe({
      next: () => {
        this.enviando.set(false);
        this.cartService.clear();
        this.pedidoConfirmado.emit();
      },
      error: (err: Error) => {
        this.enviando.set(false);
        this.error.set('No se pudo crear el pedido. Intenta de nuevo.');
        console.error(err);
      }
    });
  }
}
