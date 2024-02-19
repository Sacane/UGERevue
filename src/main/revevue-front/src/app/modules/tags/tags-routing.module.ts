import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { TagsComponent } from "./tags.component";
import { TagComponent } from "./tag/tag.component";

const routes: Routes = [
    {
        path: '',
        component: TagsComponent,
        children: []
    },
    {
        path: 'detail',
        component: TagComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TagsRoutingModule { }
