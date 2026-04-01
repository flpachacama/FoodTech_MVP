import { Component, OnInit, inject, signal, output, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RestauranteService } from '../../services/restaurante.service';
import { Restaurante } from '../../models/restaurante.model';

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mapa.component.html',
  styleUrls: ['./mapa.component.css']
})
export class MapaComponent implements OnInit, OnDestroy {
  private readonly restauranteService = inject(RestauranteService);
  private readonly destroy$ = new Subject<void>();

  restaurantes = signal<Restaurante[]>([]);
  restauranteSelected = output<Restaurante>();

  ngOnInit(): void {
    this.restauranteService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (restaurantes) => this.restaurantes.set(restaurantes),
        error: () => {}
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
