import { NgModule } from "@angular/core";
import { SharedModule } from "../../shared/shared.module";
import { TagsComponent } from "./tags.component";
import { TagsRoutingModule } from "./tags-routing.module";

@NgModule({
    declarations: [
        TagsComponent
    ],
    imports: [
        TagsRoutingModule,
        SharedModule
    ],
    exports: [TagsComponent],
    bootstrap: [TagsComponent]
})
export class TagsModule { }
