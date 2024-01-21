import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        children: [
            {
                path: 'login',
                loadChildren: () => import('./modules').then(m => m.LoginModule)
            },
            {
                path: 'signup',
                loadChildren: () => import('./modules').then(m => m.SignupModule)
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes, {})],
    exports: [RouterModule]
})
export class AppRoutingModule { }
