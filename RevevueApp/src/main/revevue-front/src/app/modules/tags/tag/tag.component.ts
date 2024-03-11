import {Component} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';

@Component({
    selector: 'app-tag',
    templateUrl: './tag.component.html',
    styleUrl: './tag.component.scss'
})
export class TagComponent {
    constructor(protected dialog: MatDialog) { }
}
