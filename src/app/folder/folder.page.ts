import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { Plugins } from "@capacitor/core";
const { MyPlugin } = Plugins;

@Component({
  selector: "app-folder",
  templateUrl: "./folder.page.html",
  styleUrls: ["./folder.page.scss"],
})
export class FolderPage implements OnInit {
  public folder: string;

  constructor(private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.folder = this.activatedRoute.snapshot.paramMap.get("id");
  }
}
