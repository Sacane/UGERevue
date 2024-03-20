import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {ProfilComponent} from "./profil.component";
import {ProfileOtherComponent} from "./components/profile-other/profile-other.component";

const routes: Routes = [
    {
        path: '',
        component: ProfilComponent
    },
    {
        path: ':id',
        component: ProfileOtherComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ProfilRoutingModule { }
