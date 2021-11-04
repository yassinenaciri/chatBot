import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UtilisateurComponent } from './list/utilisateur.component';
import { UtilisateurDetailComponent } from './detail/utilisateur-detail.component';
import { UtilisateurUpdateComponent } from './update/utilisateur-update.component';
import { UtilisateurDeleteDialogComponent } from './delete/utilisateur-delete-dialog.component';
import { UtilisateurRoutingModule } from './route/utilisateur-routing.module';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ChatModule } from '@progress/kendo-angular-conversational-ui';

@NgModule({
  imports: [SharedModule, UtilisateurRoutingModule, BrowserModule, BrowserAnimationsModule, ChatModule],
  declarations: [UtilisateurComponent, UtilisateurDetailComponent, UtilisateurUpdateComponent, UtilisateurDeleteDialogComponent],
  entryComponents: [UtilisateurDeleteDialogComponent],
})
export class UtilisateurModule {}
