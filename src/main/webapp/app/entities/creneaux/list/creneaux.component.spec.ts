import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CreneauxService } from '../service/creneaux.service';

import { CreneauxComponent } from './creneaux.component';

describe('Component Tests', () => {
  describe('Creneaux Management Component', () => {
    let comp: CreneauxComponent;
    let fixture: ComponentFixture<CreneauxComponent>;
    let service: CreneauxService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CreneauxComponent],
      })
        .overrideTemplate(CreneauxComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CreneauxComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CreneauxService);

      const headers = new HttpHeaders().append('link', 'link;link');
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
      expect(comp.creneaux?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
