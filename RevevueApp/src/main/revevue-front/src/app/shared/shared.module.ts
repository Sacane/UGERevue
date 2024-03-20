import {CommonModule} from "@angular/common";
import {NgModule} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {MaterialModule} from "./modules/material.module";
import {NavBarComponent, QuestionsDisplayerComponent, ReviewDialogComponent, SideNavBarComponent} from "./components";
import {MarkdownComponent} from "ngx-markdown";
import {MatButtonToggle} from "@angular/material/button-toggle";
import {MatChip, MatChipsModule} from "@angular/material/chips";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
import {UpdateQuestionDialogComponent} from "./components/update-question-dialog/update-question-dialog.component";
import {PopoverModule} from "ngx-bootstrap/popover";

const COMPONENTS: any[] = [
    NavBarComponent,
    SideNavBarComponent,
    ReviewDialogComponent,
    QuestionsDisplayerComponent,
    UpdateQuestionDialogComponent
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
        MatOption,
        PopoverModule
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
