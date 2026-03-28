import { Component } from '@angular/core';
import { MapaPageComponent } from './components/mapa-page/mapa-page.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [MapaPageComponent],
  template: '<app-mapa-page />'
})
export class AppComponent {}
