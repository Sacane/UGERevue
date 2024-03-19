import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ProfilComponent} from "./profil.component";
import {ProfilRoutingModule} from "./profil-routing.module";
import {
    ProfilInfosComponent,
    ProfilInfosContentComponent,
    ProfilQuestionsComponent,
    ProfilUsersComponent,
    ProfilUsersContentComponent
} from "./components";
import {RolePipe} from "./pipes/role.pipe";

@NgModule({
    declarations: [
        ProfilComponent,
        ProfilInfosComponent,
        ProfilInfosContentComponent,
        ProfilQuestionsComponent,
        ProfilUsersComponent,
        ProfilUsersContentComponent
    ],
    imports: [
        ReactiveFormsModule,
        ProfilRoutingModule,
        SharedModule,
        RolePipe,
        ReactiveFormsModule,
        FormsModule
    ],
    exports: [ProfilComponent, ProfilInfosComponent, ProfilInfosContentComponent, ProfilQuestionsComponent, ProfilUsersComponent, ProfilUsersContentComponent],
    bootstrap: [ProfilComponent, ProfilInfosComponent, ProfilInfosContentComponent, ProfilQuestionsComponent, ProfilUsersComponent, ProfilUsersContentComponent]
})
export class ProfilModule { }
