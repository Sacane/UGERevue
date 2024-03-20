import {Component, Input, signal} from '@angular/core';

@Component({
  selector: 'app-popever',
  standalone: true,
  imports: [],
  templateUrl: './popever.component.html',
  styleUrl: './popever.component.scss'
})
export class PopeverComponent {
    isTextShowingUp = signal(false);
    @Input("text") displayText: string;


    showText() {
        this.isTextShowingUp.set(!this.isTextShowingUp())
    }
}
