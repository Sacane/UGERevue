import {Component} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent{
  title = 'revevue-front';
  navs = [
    {
      name: 'Home',
      url: '/home',
      icon: 'home'
    },
    {
      name: 'Questions',
      url: '/questions',
      icon: 'chat'
    },
    {
      name: 'Tags',
      url: '/tags',
      icon: 'tags'
    },
    {
      name: 'Socials',
      url: '/users',
      icon: 'account_circle'
    }
  ];
}
