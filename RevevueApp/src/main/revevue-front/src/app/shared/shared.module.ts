import {CommonModule} from "@angular/common";
import {NgModule} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {MaterialModule} from "./modules/material.module";
import {NavBarComponent, ReviewDialogComponent, SideNavBarComponent} from "./components";
import {MarkdownComponent} from "ngx-markdown";
import {MatButtonToggle} from "@angular/material/button-toggle";

const COMPONENTS: any[] = [
    NavBarComponent,
    SideNavBarComponent,
    ReviewDialogComponent
];

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule,
        MarkdownComponent,
        MatButtonToggle
    ],
    declarations: [
        COMPONENTS
    ],
    exports: [
        CommonModule,
        MaterialModule,
        COMPONENTS
    ]
})
export class SharedModule { }
