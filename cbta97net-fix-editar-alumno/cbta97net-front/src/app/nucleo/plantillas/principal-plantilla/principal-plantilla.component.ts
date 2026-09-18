import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { SidebarPlantillaComponente } from "../../widgets/sidebar/sidebar.component";

@Component({
  selector: 'layout-main',
  templateUrl: './principal-plantilla.component.html',
  standalone: true,
  imports: [
    SidebarPlantillaComponente,
    RouterOutlet
  ]
})
export class PrincipalPlantillaComponente {

}