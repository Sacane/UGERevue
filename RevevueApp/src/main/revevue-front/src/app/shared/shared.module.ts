import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { MaterialModule } from "./modules/material.module";
import { NavBarComponent, QuestionsDisplayerComponent, ReviewDialogComponent, SideNavBarComponent } from "./components";
import { MarkdownComponent } from "ngx-markdown";
import { MatButtonToggle } from "@angular/material/button-toggle";
import {MatChip, MatChipsModule} from "@angular/material/chips";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";

const COMPONENTS: any[] = [
    NavBarComponent,
    SideNavBarComponent,
    ReviewDialogComponent,
    QuestionsDisplayerComponent
];

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule,
        MarkdownComponent,
        MatButtonToggle,
        MatChip,
        MatChipsModule,
        MatAutocompleteTrigger,
        MatAutocomplete,
        MatOption
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
