import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { MaterialModule } from "./modules/material.module";
import { NavBarComponent } from "./components";

const COMPONENTS: any[] = [
    NavBarComponent
];

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule
    ],
    declarations: [
        COMPONENTS
    ],
    exports: [
        MaterialModule,
        COMPONENTS
    ]
})
export class SharedModule { }
