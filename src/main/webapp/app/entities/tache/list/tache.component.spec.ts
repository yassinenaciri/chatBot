import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TacheService } from '../service/tache.service';

import { TacheComponent } from './tache.component';

describe('Tache Management Component', () => {
  let comp: TacheComponent;
  let fixture: ComponentFixture<TacheComponent>;
  let service: TacheService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TacheComponent],
    })
      .overrideTemplate(TacheComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TacheComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TacheService);

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
    expect(comp.taches?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
