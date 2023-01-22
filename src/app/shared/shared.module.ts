import { NgModule } from "@angular/core";
import { AgePipe } from "./pipes/age.pipe";
import { DescriptionPipe } from "./pipes/description.pipe";

const declarations = [AgePipe, DescriptionPipe]

@NgModule({
    declarations,
    imports: [],
    exports: declarations
})
export class SharedModule {}