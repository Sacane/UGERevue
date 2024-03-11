import {NgModule} from "@angular/core";

import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatCardModule} from "@angular/material/card";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatDialogModule} from '@angular/material/dialog';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatBadgeModule} from '@angular/material/badge';
import {MatDividerModule} from '@angular/material/divider';


@NgModule({
    imports: [
        MatButtonModule,
        MatInputModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule,
        MatCardModule,
        MatPaginatorModule,
        MatSnackBarModule,
        MatDialogModule,
        MatProgressSpinnerModule,
        MatBadgeModule,
        MatDividerModule
    ],
    exports: [
        MatButtonModule,
        MatInputModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule,
        MatCardModule,
        MatPaginatorModule,
        MatSnackBarModule,
        MatDialogModule,
        MatProgressSpinnerModule,
        MatBadgeModule,
        MatDividerModule
    ]
})
export class MaterialModule { }
