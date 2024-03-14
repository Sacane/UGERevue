import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {TagsComponent} from "./tags.component";
import {TagsRoutingModule} from "./tags-routing.module";
import {TagComponent} from "./tag/tag.component";

@NgModule({
    declarations: [
        TagsComponent,
        TagComponent
    ],
    imports: [
        TagsRoutingModule,
        SharedModule
    ],
    exports: [TagsComponent, TagComponent],
    bootstrap: [TagsComponent, TagComponent]
})
export class TagsModule { }
