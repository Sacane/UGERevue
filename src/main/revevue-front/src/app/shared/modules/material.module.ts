import { NgModule } from "@angular/core";

import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';

@NgModule({
    imports: [
        MatButtonModule,
        MatInputModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule
    ],
    exports: [
        MatButtonModule,
        MatInputModule,
        MatToolbarModule,
        MatIconModule,
        MatSidenavModule
    ]
})
export class MaterialModule { }
