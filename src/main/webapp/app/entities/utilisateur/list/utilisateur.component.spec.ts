import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { UtilisateurService } from '../service/utilisateur.service';

import { UtilisateurComponent } from './utilisateur.component';

describe('Utilisateur Management Component', () => {
  let comp: UtilisateurComponent;
  let fixture: ComponentFixture<UtilisateurComponent>;
  let service: UtilisateurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [UtilisateurComponent],
    })
      .overrideTemplate(UtilisateurComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UtilisateurComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UtilisateurService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.utilisateurs?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
