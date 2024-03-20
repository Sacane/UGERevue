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
import {ProfileOtherComponent} from "./components/profile-other/profile-other.component";

@NgModule({
    declarations: [
        ProfilComponent,
        ProfilInfosComponent,
        ProfilInfosContentComponent,
        ProfilQuestionsComponent,
        ProfilUsersComponent,
        ProfilUsersContentComponent,
        ProfileOtherComponent
    ],
    imports: [
        ReactiveFormsModule,
        ProfilRoutingModule,
        SharedModule,
        RolePipe,
        ReactiveFormsModule,
        FormsModule,
    ],
    exports: [ProfilComponent, ProfilInfosComponent, ProfilInfosContentComponent, ProfilQuestionsComponent, ProfilUsersComponent, ProfilUsersContentComponent, ProfileOtherComponent],
    bootstrap: [ProfilComponent, ProfilInfosComponent, ProfilInfosContentComponent, ProfilQuestionsComponent, ProfilUsersComponent, ProfilUsersContentComponent]
})
export class ProfilModule { }
