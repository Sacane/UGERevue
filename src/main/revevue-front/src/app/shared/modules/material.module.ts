import { NgModule } from "@angular/core";

import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSnackBarModule } from '@angular/material/snack-bar';

@NgModule({
    imports: [
        MatButtonModule,
        MatInputModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule,
        MatCardModule,
        MatPaginatorModule,
        MatSnackBarModule
    ],
    exports: [
        MatButtonModule,
        MatInputModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule,
        MatCardModule,
        MatPaginatorModule,
        MatSnackBarModule
    ]
})
export class MaterialModule { }
