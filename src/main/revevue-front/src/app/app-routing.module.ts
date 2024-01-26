import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

export const routes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: '/home' },
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
            },
            {
                path: 'home',
                loadChildren: () => import('./modules').then(m => m.HomeModule)
            },
            {
                path: 'questions',
                loadChildren: () => import('./modules').then(m => m.QuestionsModule)
            },
            {
                path: 'tags',
                loadChildren: () => import('./modules').then(m => m.TagsModule)
            },
            {
                path: 'users',
                loadChildren: () => import('./modules').then(m => m.UsersModule)
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes, {})],
    exports: [RouterModule]
})
export class AppRoutingModule { }
