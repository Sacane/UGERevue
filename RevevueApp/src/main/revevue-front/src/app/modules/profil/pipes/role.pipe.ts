import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'role',
    standalone: true
})
export class RolePipe implements PipeTransform {

    transform(value: String): String {
        switch (value) {
            case "ADMIN":
                return "Administrateur";
            case "USER":
                return "Utilisateur";
        }
        return "Inconnu";
    }

}
