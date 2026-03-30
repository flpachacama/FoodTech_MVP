import { Component, ElementRef, OnInit, ViewChild, inject, signal, output, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RestauranteService, DeliverService } from '../../services';
import { Restaurante, Deliver } from '../../models';

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mapa.component.html',
  styleUrls: ['./mapa.component.css']
})
export class MapaComponent implements OnInit, OnDestroy {
  @ViewChild('canvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
  
  private readonly restauranteService = inject(RestauranteService);
  private readonly deliverService = inject(DeliverService);
  private readonly destroy$ = new Subject<void>();
  
  private ctx!: CanvasRenderingContext2D;
  private readonly CANVAS_SIZE = 800;
  private readonly GRID_SIZE = 100;
  private readonly PADDING = 20;
  private readonly ICON_SIZE = 40;
  private readonly RESTAURANTE_ICON_SIZE = 50;
  
  private images = new Map<string, HTMLImageElement>();
  private imagesLoaded = false;
  
  restaurantes = signal<Restaurante[]>([]);
  delivers = signal<Deliver[]>([]);
  hoveredElement = signal<string | null>(null);
  
  restauranteSelected = output<Restaurante>();
  
  ngOnInit(): void {
    this.ctx = this.canvasRef.nativeElement.getContext('2d')!;
    this.loadImages();
    this.loadData();
  }
  
  private loadImages(): void {
    const imagesToLoad = [
      { key: 'restaurante', path: '/assets/restaurantes/restaurante.svg' },
      { key: 'bici-inactivo', path: '/assets/delivers/bici-inactivo.svg' },
      { key: 'bici-activo', path: '/assets/delivers/bici-activo.svg' },
      { key: 'bici-en-entrega', path: '/assets/delivers/bici-en-entrega.svg' },
      { key: 'moto-inactivo', path: '/assets/delivers/moto-inactivo.svg' },
      { key: 'moto-activo', path: '/assets/delivers/moto-activo.svg' },
      { key: 'moto-en-entrega', path: '/assets/delivers/moto-en-entrega.svg' },
      { key: 'auto-inactivo', path: '/assets/delivers/auto-inactivo.svg' },
      { key: 'auto-activo', path: '/assets/delivers/auto-activo.svg' },
      { key: 'auto-en-entrega', path: '/assets/delivers/auto-en-entrega.svg' }
    ];

    let loadedCount = 0;
    imagesToLoad.forEach(({ key, path }) => {
      const img = new Image();
      img.onload = () => {
        loadedCount++;
        if (loadedCount === imagesToLoad.length) {
          this.imagesLoaded = true;
          this.render();
        }
      };
      img.onerror = () => {
        loadedCount++;
        if (loadedCount === imagesToLoad.length) {
          this.imagesLoaded = true;
          this.render();
        }
      };
      img.src = path;
      this.images.set(key, img);
    });
  }
  
  private loadData(): void {
    this.restauranteService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (restaurantes) => {
          this.restaurantes.set(restaurantes);
          if (this.imagesLoaded) this.render();
        },
        error: () => {}
      });
    
    this.deliverService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (delivers) => {
          this.delivers.set(delivers);
          if (this.imagesLoaded) this.render();
        },
        error: () => {}
      });
  }
  
  private render(): void {
    this.ctx.clearRect(0, 0, this.CANVAS_SIZE, this.CANVAS_SIZE);
    this.drawGrid();
    this.restaurantes().forEach(r => this.drawRestaurante(r));
    this.delivers().forEach(d => this.drawDeliver(d));
  }
  
  private drawGrid(): void {
    this.ctx.strokeStyle = '#e0e0e0';
    this.ctx.lineWidth = 1;
        for (let i = 0; i <= 10; i++) {
      const x = (i * this.CANVAS_SIZE) / 10;
      this.ctx.beginPath();
      this.ctx.moveTo(x, 0);
      this.ctx.lineTo(x, this.CANVAS_SIZE);
      this.ctx.stroke();
    }

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

    const img = this.images.get('restaurante');
    const size = this.RESTAURANTE_ICON_SIZE;
    if (img && img.complete) {
      this.ctx.drawImage(img, x - size / 2, y - size / 2, size, size);
    } else {
      this.ctx.fillStyle = '#e74c3c';
      this.ctx.beginPath();
      this.ctx.arc(x, y, size / 2, 0, Math.PI * 2);
      this.ctx.fill();
      this.ctx.strokeStyle = '#fff';
      this.ctx.lineWidth = 2;
      this.ctx.stroke();
    }

    this.ctx.fillStyle = '#000';
    this.ctx.font = '25px Arial Bold';
    this.ctx.textAlign = 'center';
    this.ctx.fillText(r.nombre, x, y + 30);
  }
  
  private drawDeliver(d: Deliver): void {
    const x = this.coordToPixel(d.ubicacionX);
    const y = this.coordToPixel(d.ubicacionY);

    const vehiculo = ((): string => {
      switch (d.vehiculo) {
        case 'BICICLETA': return 'bici';
        case 'MOTO': return 'moto';
        case 'AUTO': return 'auto';
        default: return String(d.vehiculo).toLowerCase();
      }
    })();

    let estado = 'inactivo';
    if (d.estado === 'ACTIVO') estado = 'activo';
    if (d.estado === 'EN_ENTREGA') estado = 'en-entrega';
    
    const imageKey = `${vehiculo}-${estado}`;
    const img = this.images.get(imageKey);
    
    if (img && img.complete) {
      this.ctx.drawImage(img, x - this.ICON_SIZE / 2, y - this.ICON_SIZE / 2, this.ICON_SIZE, this.ICON_SIZE);
    } else {
      let color = '#95a5a6';
      if (d.estado === 'ACTIVO') color = '#27ae60';
      if (d.estado === 'EN_ENTREGA') color = '#f39c12';

      this.ctx.fillStyle = color;
      this.ctx.beginPath();
      this.ctx.moveTo(x, y - 12);
      this.ctx.lineTo(x - 10, y + 8);
      this.ctx.lineTo(x + 10, y + 8);
      this.ctx.closePath();
      this.ctx.fill();

      this.ctx.strokeStyle = '#fff';
      this.ctx.lineWidth = 2;
      this.ctx.stroke();

      this.ctx.fillStyle = '#fff';
      this.ctx.font = 'bold 10px Arial';
      this.ctx.textAlign = 'center';
      this.ctx.textBaseline = 'middle';
      
      let vehiculoIcon = 'B';
      if (d.vehiculo === 'MOTO') vehiculoIcon = 'M';
      if (d.vehiculo === 'AUTO') vehiculoIcon = 'A';
      
      this.ctx.fillText(vehiculoIcon, x, y);
    }
  }
  
  private coordToPixel(coord: number): number {
    return this.PADDING + (coord * (this.CANVAS_SIZE - 2 * this.PADDING)) / this.GRID_SIZE;
  }
  
  onCanvasClick(event: MouseEvent): void {
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
