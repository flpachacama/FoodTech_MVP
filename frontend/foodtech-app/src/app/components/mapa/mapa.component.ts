import { Component, ElementRef, OnInit, ViewChild, inject, signal, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestauranteService, DeliverService } from '../../services';
import { Restaurante, Deliver } from '../../models';

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mapa.component.html',
  styleUrls: ['./mapa.component.css']
})
export class MapaComponent implements OnInit {
  @ViewChild('canvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
  
  private readonly restauranteService = inject(RestauranteService);
  private readonly deliverService = inject(DeliverService);
  
  private ctx!: CanvasRenderingContext2D;
  private readonly CANVAS_SIZE = 800;
  private readonly GRID_SIZE = 100;
  
  // Signals
  restaurantes = signal<Restaurante[]>([]);
  delivers = signal<Deliver[]>([]);
  hoveredElement = signal<string | null>(null);
  
  // Output event
  restauranteSelected = output<Restaurante>();
  
  ngOnInit(): void {
    this.ctx = this.canvasRef.nativeElement.getContext('2d')!;
    this.loadData();
  }
  
  private loadData(): void {
    this.restauranteService.getAll().subscribe({
      next: (restaurantes) => {
        this.restaurantes.set(restaurantes);
        this.render();
      },
      error: (err) => console.error('Error cargando restaurantes:', err)
    });
    
    this.deliverService.getAll().subscribe({
      next: (delivers) => {
        this.delivers.set(delivers);
        this.render();
      },
      error: (err) => console.error('Error cargando delivers:', err)
    });
  }
  
  private render(): void {
    // Limpiar canvas
    this.ctx.clearRect(0, 0, this.CANVAS_SIZE, this.CANVAS_SIZE);
    
    // Dibujar grid de fondo
    this.drawGrid();
    
    // Dibujar restaurantes
    this.restaurantes().forEach(r => this.drawRestaurante(r));
    
    // Dibujar repartidores
    this.delivers().forEach(d => this.drawDeliver(d));
  }
  
  private drawGrid(): void {
    this.ctx.strokeStyle = '#e0e0e0';
    this.ctx.lineWidth = 1;
    
    // Líneas verticales
    for (let i = 0; i <= 10; i++) {
      const x = (i * this.CANVAS_SIZE) / 10;
      this.ctx.beginPath();
      this.ctx.moveTo(x, 0);
      this.ctx.lineTo(x, this.CANVAS_SIZE);
      this.ctx.stroke();
    }
    
    // Líneas horizontales
    for (let i = 0; i <= 10; i++) {
      const y = (i * this.CANVAS_SIZE) / 10;
      this.ctx.beginPath();
      this.ctx.moveTo(0, y);
      this.ctx.lineTo(this.CANVAS_SIZE, y);
      this.ctx.stroke();
    }
  }
  
  private drawRestaurante(r: Restaurante): void {
    const x = this.coordToPixel(r.coordenadaX);
    const y = this.coordToPixel(r.coordenadaY);
    
    // Círculo rojo
    this.ctx.fillStyle = '#e74c3c';
    this.ctx.beginPath();
    this.ctx.arc(x, y, 15, 0, Math.PI * 2);
    this.ctx.fill();
    
    // Borde blanco
    this.ctx.strokeStyle = '#fff';
    this.ctx.lineWidth = 2;
    this.ctx.stroke();
    
    // Nombre debajo
    this.ctx.fillStyle = '#000';
    this.ctx.font = '12px Arial';
    this.ctx.textAlign = 'center';
    this.ctx.fillText(r.nombre, x, y + 30);
  }
  
  private drawDeliver(d: Deliver): void {
    const x = this.coordToPixel(d.ubicacionX);
    const y = this.coordToPixel(d.ubicacionY);
    
    // Color según estado
    let color = '#95a5a6'; // INACTIVO (gris)
    if (d.estado === 'ACTIVO') color = '#27ae60'; // verde
    if (d.estado === 'EN_ENTREGA') color = '#f39c12'; // amarillo
    
    // Triángulo
    this.ctx.fillStyle = color;
    this.ctx.beginPath();
    this.ctx.moveTo(x, y - 12);
    this.ctx.lineTo(x - 10, y + 8);
    this.ctx.lineTo(x + 10, y + 8);
    this.ctx.closePath();
    this.ctx.fill();
    
    // Borde
    this.ctx.strokeStyle = '#fff';
    this.ctx.lineWidth = 2;
    this.ctx.stroke();
    
    // Icono del vehículo
    this.ctx.fillStyle = '#fff';
    this.ctx.font = 'bold 10px Arial';
    this.ctx.textAlign = 'center';
    this.ctx.textBaseline = 'middle';
    
    let vehiculoIcon = 'B';
    if (d.vehiculo === 'MOTO') vehiculoIcon = 'M';
    if (d.vehiculo === 'AUTO') vehiculoIcon = 'A';
    
    this.ctx.fillText(vehiculoIcon, x, y);
  }
  
  private coordToPixel(coord: number): number {
    return (coord * this.CANVAS_SIZE) / this.GRID_SIZE;
  }
  
  onCanvasClick(event: MouseEvent): void {
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    
    // Verificar click en restaurantes
    for (const r of this.restaurantes()) {
      const rx = this.coordToPixel(r.coordenadaX);
      const ry = this.coordToPixel(r.coordenadaY);
      const distance = Math.sqrt((x - rx) ** 2 + (y - ry) ** 2);
      
      if (distance <= 15) {
        this.restauranteSelected.emit(r);
        return;
      }
    }
  }
  
  onCanvasMouseMove(event: MouseEvent): void {
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    
    let found = false;
    
    // Verificar hover en restaurantes
    for (const r of this.restaurantes()) {
      const rx = this.coordToPixel(r.coordenadaX);
      const ry = this.coordToPixel(r.coordenadaY);
      const distance = Math.sqrt((x - rx) ** 2 + (y - ry) ** 2);
      
      if (distance <= 15) {
        this.hoveredElement.set(`restaurante-${r.id}`);
        this.canvasRef.nativeElement.style.cursor = 'pointer';
        found = true;
        break;
      }
    }
    
    if (!found) {
      this.hoveredElement.set(null);
      this.canvasRef.nativeElement.style.cursor = 'default';
    }
  }
}
