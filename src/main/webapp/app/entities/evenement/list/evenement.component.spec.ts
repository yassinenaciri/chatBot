import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { EvenementService } from '../service/evenement.service';

import { EvenementComponent } from './evenement.component';

describe('Evenement Management Component', () => {
  let comp: EvenementComponent;
  let fixture: ComponentFixture<EvenementComponent>;
  let service: EvenementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [EvenementComponent],
    })
      .overrideTemplate(EvenementComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EvenementComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EvenementService);

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
    expect(comp.evenements?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
