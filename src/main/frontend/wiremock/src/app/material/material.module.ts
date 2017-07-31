import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {
  MdButtonModule,
  MdCardModule,
  MdChipsModule,
  MdIconModule,
  MdInputModule,
  MdListModule,
  MdTabsModule,
  MdTableModule, MdToolbarModule, MdMenuModule, MdSlideToggleModule, MdCheckboxModule
} from '@angular/material';
import {CdkTableModule} from '@angular/cdk'
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
  imports: [
    CommonModule
  ],
  exports: [
    MdInputModule,
    MdButtonModule,
    MdTabsModule,
    MdCardModule,
    MdListModule,
    MdChipsModule,
    MdIconModule,
    CdkTableModule,
    MdTableModule,
    MdToolbarModule,
    MdSlideToggleModule,
    MdMenuModule,
    MdCheckboxModule,
    BrowserAnimationsModule
  ],
  declarations: []
})
export class MaterialModule {
}